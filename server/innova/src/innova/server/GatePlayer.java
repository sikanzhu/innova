/* Copyright (c) 2017, Sikan Zhu
/* All rights reserved.
/* 
/* Redistribution and use in source and binary forms, with or without
/* modification, are permitted provided that the following conditions are met:
/* 
/* * Redistributions of source code must retain the above copyright notice, this
/*   list of conditions and the following disclaimer.
/* 
/* * Redistributions in binary form must reproduce the above copyright notice,
/*   this list of conditions and the following disclaimer in the documentation
/*   and/or other materials provided with the distribution.
/* 
/* * Neither the name of Sikan Zhu nor the names of its
/*   contributors may be used to endorse or promote products derived from
/*   this software without specific prior written permission.
/* 
/* THIS SOFTWARE IS PROVIDED BY SIKAN ZHU AND CONTRIBUTORS "AS IS"
/* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
/* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
/* DISCLAIMED. IN NO EVENT SHALL SIKAN ZHU OR CONTRIBUTORS BE LIABLE
/* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
/* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
/* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
/* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
/* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
/* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package innova.server;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import innova.common.Constant;
import innova.common.TimeMagic;
import innova.common.TimeMagicIns;
import innova.database.ToDatabase;
import innova.database.ToDatabaseConfig;
import innova.database.ToDatabaseProperty;
import innova.redis.ToRedisOperation;
import innova.redis.ToRedisPool;
import innova.redis.ToRedisProperty;
import innova.aio.Server;
import innova.protocol.InnovaProtocol.Actor;
import innova.protocol.InnovaProtocol.IdCtype;
import innova.protocol.InnovaProtocol.IdCtypeData;
import innova.protocol.InnovaProtocol.IdList;
import innova.protocol.InnovaProtocol.ActorList;
import innova.protocol.InnovaProtocol.IdPtype;
import innova.protocol.InnovaProtocol.IdPtypeData;
import innova.protocol.InnovaProtocol.GateToPlayer;
import innova.protocol.InnovaProtocol.Operation;
import innova.protocol.InnovaProtocol.PlayerToGate;
import innova.protocol.InnovaProtocol.Region;
import innova.protocol.InnovaProtocol.RegionActorPair;
import innova.protocol.InnovaProtocol.RegionIdPair;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

class WatchingRegion
{
	public final TimeMagicIns tm_ins;
	public final Region region;

	public WatchingRegion( final Region r , final TimeMagic tm , final long latency )
	{
		region = r;
		tm_ins = new TimeMagicIns( tm , latency );
	}
}

class OwningActor
{
	Region region;
	Actor actor;

	public OwningActor( Region r , Actor a )
	{
		region = r;
		actor = a;
	}
}

class GPlayer
{
	public List< WatchingRegion > watching_regions = new ArrayList< WatchingRegion >();
	public Map< Long , OwningActor > owning_actors = new HashMap< Long , OwningActor >();
}

public class GatePlayer extends Server< GPlayer >
{
	// thread safe
	private final static Logger _logger = Logger.getLogger( "" );
	private TimeMagic _time_magic = new TimeMagic();

	// multi-thread
	private ToRedisPool _redis_pool;
	private ToDatabase _database;
	private ToDatabaseProperty _tdbproperty;
	private ToDatabaseConfig _tdbconfig;

	// constructors
	public void Init( final ToRedisPool redis , final ToDatabase database )
	{
		_redis_pool = redis;
		_database = database;
		_tdbproperty = new ToDatabaseProperty( database.GetConnection() );
		_tdbproperty.Init();
		_tdbconfig = new ToDatabaseConfig( database.GetConnection() );
		_tdbconfig.Init();
	}

	public void Deinit()
	{
		_tdbproperty.Deinit();
		_tdbconfig.Deinit();
	}

	//
	public void WorldSynchronizeTime( final long time )
	{
		_logger.trace( "World time synchronized: " + time );
		_time_magic.SynchronizeTime( time );
	}

	// implementations of Server
	@Override
	public GPlayer OnAccept( final AsynchronousSocketChannel socket )
	{
		_logger.trace( new Exception().getStackTrace()[ 0 ] );
		return new GPlayer();
	}

	@Override
	protected void OnAcceptFailed( Throwable e )
	{
		_logger.fatal( e );
	}

	@Override
	public void OnRead( final GPlayer client , final AsynchronousSocketChannel socket , final byte[] buffer )
	{
		_logger.trace( new Exception().getStackTrace()[ 0 ] );
		try
		{
			final PlayerToGate p2g = PlayerToGate.parseFrom( buffer );
			switch( p2g.getType() )
			{
			case MESSAGE:
				// TODO check magic number
				@SuppressWarnings( "unused" )
				String name;
				try
				{
					name = socket.getRemoteAddress().toString();
				}
				catch( IOException e )
				{
					name = "UnknownRemote";
					_logger.warn( "Can not get socket remote address" , e );
				}

				final GateToPlayer resp = ParsePlayerToGateMessage( String.valueOf( client.hashCode() ) , p2g , client );
				if( 0 == Write( socket , resp.toByteArray() ) )
				{
					_logger.error( new Exception( "Writing to player overflow" ) );
				}
				break;
			default:
				_logger.warn( new Exception( "Unknown message from player" ) );
				break;
			}
		}
		catch( InvalidProtocolBufferException e )
		{
			_logger.error( e , new Exception() );
		}
	}

	@Override
	public void OnWrite( final GPlayer client , final AsynchronousSocketChannel socket , final int size )
	{
		_logger.trace( new Exception().getStackTrace()[ 0 ] );
	}

	@Override
	public void OnReadFailed( final GPlayer client , final AsynchronousSocketChannel socket , final Throwable e )
	{
		_logger.trace( new Exception().getStackTrace()[ 0 ] );
		RemovePlayer( String.valueOf( client.hashCode() ) , _time_magic.GetTime() , client , _redis_pool , _database , _tdbproperty );
	}

	@Override
	public void OnWriteFailed( final GPlayer client , final AsynchronousSocketChannel socket , final Throwable e )
	{
		_logger.trace( new Exception().getStackTrace()[ 0 ] );
		RemovePlayer( String.valueOf( client.hashCode() ) , _time_magic.GetTime() , client , _redis_pool , _database , _tdbproperty );
	}

	// parsing
	private enum PERFORMANCE_LOG
	{
		BEGIN_WRITING( 0 ) , BEGIN_READING( 1 ) , BEGIN_CONFIG( 2 ) , END( 3 ) , NUM( 4 );

		private final int value;

		PERFORMANCE_LOG( final int nv )
		{
			value = nv;
		}

		public int get()
		{
			return value;
		}
	}

	// Remarks
	// All the "synchronized" should be done in order of
	private GateToPlayer ParsePlayerToGateMessage( final String name , final PlayerToGate p2g , final GPlayer player )
	{
		// Get jedis from pool
		final Jedis jedis;
		synchronized( _redis_pool )
		{
			jedis = _redis_pool.Obtain();
		}
		final Pipeline pipline = jedis.pipelined();

		// GateToPlayer
		final GateToPlayer.Builder resp = GateToPlayer.newBuilder();

		// type
		resp.setType( GateToPlayer.ETYPE.MESSAGE );

		// time of writing
		final long time_writing = _time_magic.GetTime();

		// performance log
		final long[] message_process = new long[ PERFORMANCE_LOG.NUM.get() ];
		message_process[ PERFORMANCE_LOG.BEGIN_WRITING.get() ] = System.currentTimeMillis();

		// WRITING
		{
			synchronized( player.owning_actors )
			{
				// actors enter region
				// actor enters new region before exiting old region.
				// to ensure the actor has entered before existing,
				// then it will not be deleted while changing region.
				for( int i = 0 ; i < p2g.getEnteredActorsList().size() ; ++i )
				{
					final Region region = p2g.getEnteredRegionsList().get( i );
					final ActorList actors = p2g.getEnteredActorsList().get( i );

					for( Actor actor : actors.getActorsList() )
					{
						_logger.trace( name + ": actor enters: \"" + actor.getId() + "\"|" + actor.getPtypesCount() + " " + region.getIndex() );

						// find actor
						OwningActor oa = player.owning_actors.get( actor.getId() );

						// add to dest if did not found.
						if( null == oa )
						{
							oa = new OwningActor( region , actor );
							player.owning_actors.put( actor.getId() , oa );
						}
						else
						{
							oa.region = region;
							oa.actor = actor;
						}

						// set the properties
						PutActorToRedis( region , actor , pipline , time_writing );
					}
				}

				// actors update
				for( Actor actor : p2g.getUpdateActorsList() )
				{
					final OwningActor oa = player.owning_actors.get( actor.getId() );
					if( null != oa )
					{
						final Region region = oa.region;

						PutActorToRedis( region , actor , pipline , time_writing );

						// log
						if( actor.getPtypesCount() > 0 )
						{
							_logger.trace( name + ": actor update: \"" + actor.getId() + "\"|" + actor.getPtypesCount() + " " + region.getIndex() );
						}
					}
					else
					{
						_logger.warn( name + ": violate updating actor: " + actor.getId() + "\"|" + actor.getPtypesCount() );
					}
				}
			}

			// actors exit region
			for( int i = 0 ; i < p2g.getExitedRegionsCount() ; ++i )
			{
				final Region region = p2g.getExitedRegions( i );
				final IdList aids = p2g.getExitedActors( i );

				for( Long aid : aids.getIdsList() )
				{
					// find actor
					Actor actor = null;
					synchronized( player.owning_actors )
					{
						final OwningActor oa = player.owning_actors.get( aid );
						if( null != oa )
						{
							actor = oa.actor;
						}
					}

					// deactivate
					if( null != actor )
					{
						final IdPtype.Builder ipb = IdPtype.newBuilder();
						ipb.setId( actor.getId() );
						for( Integer ptype : actor.getPtypesList() )
						{
							ipb.setPtype( ptype );
							ToRedisProperty.PDeactivate( pipline , region , ipb.build() , time_writing );
						}

						// log
						_logger.trace( name + ": actor exits: \"" + actor.getId() + "\"|" + actor.getPtypesCount() + " " + region.getIndex() );
					}
					else
					{
						// TODO violate message that try to deactivate actor the
						_logger.warn( name + ": violate exiting actor: " + aid + "\"" );
						// player does not own the actor.
					}
				}
			}

			// push operations
			for( Operation operation : p2g.getPushOperationsList() )
			{
				//TODO should I check the src actor is owned by the player?
				ToRedisOperation.PPushOperation( pipline , operation.getDest() , operation.getPtype() , operation.toByteArray() );
			}
			ToRedisOperation.PCommit( pipline );
		}

		// performance log
		message_process[ PERFORMANCE_LOG.BEGIN_READING.get() ] = System.currentTimeMillis();

		// READING
		// pop operations that affect the player.
		// Here player begins to read from Redis.
		{
			synchronized( player.owning_actors )
			{
				List< byte[] > datas;
				for( Map.Entry< Long , OwningActor > oa : player.owning_actors.entrySet() )
				{
					final IdPtype.Builder ipb = IdPtype.newBuilder();
					ipb.setId( oa.getValue().actor.getId() );

					for( Integer ptype : oa.getValue().actor.getPtypesList() )
					{
						ipb.setPtype( ptype );
						datas = ToRedisOperation.UPopOperations( pipline , ipb.build() );

						if( null != datas )
						{
							for( byte[] data : datas )
							{
								try
								{
									final Operation op = Operation.parseFrom( data );
									resp.addOperations( op );
								}
								catch( InvalidProtocolBufferException e )
								{
									_logger.warn( new Exception().getStackTrace()[0] + " invalid Operation in Redis" , e );
								}
							}
						}
					}
				}
			}
		}

		// request updated of watching regions
		// watching regions
		synchronized( player.watching_regions )
		{
			// watching regions
			if( p2g.getWatchingRegionsList().size() > 0 )
			{
				// add new regions
				for( Region i : p2g.getWatchingRegionsList() )
				{
					boolean found = false;
					for( WatchingRegion j : player.watching_regions )
					{
						if( j.region.equals( i ) )
						{
							found = true;
							break;
						}
					}

					if( false == found )
					{
						_logger.trace( name + ": watch region: " + i.getIndex() );
						player.watching_regions.add( new WatchingRegion( i , _time_magic , Constant.TIME_MAGIC_LATENCY ) );
					}
				}

				// remove unwatching region
				for( Iterator< WatchingRegion > iwr = player.watching_regions.iterator() ; iwr.hasNext() ; )
				{
					final WatchingRegion wr = iwr.next();
					if( false == p2g.getWatchingRegionsList().contains( wr.region ) )
					{
						_logger.trace( name + ": unwatch region: " + wr.region.getIndex() );
						iwr.remove();
					}
				}
			}

			// get updated properties in regions
			_logger.trace( name + ": get activated and inactivated actors in: " + player.watching_regions.size() + " regions." );
			for( int i = 0 ; i < player.watching_regions.size() ; ++i )
			{
				final WatchingRegion region = player.watching_regions.get( i );
				final long time_begin = region.tm_ins.GetReadingTimeBegin();

				// get updated
				final List< IdPtype > updated = ToRedisProperty.UGetActive( pipline , region.region , time_begin , Long.MAX_VALUE );
				final List< RegionActorPair.Builder > updated_list = new ArrayList< RegionActorPair.Builder >();
				IdPtypeListToActorListBuilder( region.region , updated , updated_list );
				for( RegionActorPair.Builder rapb : updated_list )
				{
					resp.addUpdated( rapb.build() );
				}

				// get exited
				final List< IdPtype > exited = ToRedisProperty.UGetInactive( pipline , region.region , time_begin , Long.MAX_VALUE );

				final List< RegionIdPair > exited_list = new ArrayList< RegionIdPair >();
				IdPtypeListToIdentityListBuilder( region.region , exited , exited_list );
				resp.addAllExited( exited_list );

				//
				_logger.trace( name + ": request region: @(" + time_begin + "~+inf) " + region.region.getIndex() + " | " + updated.size() + "/" + exited.size() );
			}
		}

		// get updated data in regions
		{
			// build request
			final List< IdPtype > requests = new ArrayList< IdPtype >();
			for( RegionActorPair.Builder rapb : resp.getUpdatedBuilderList() )
			{
				final IdPtype.Builder ipb = IdPtype.newBuilder();
				ipb.setId( rapb.getActor().getId() );

				for( Integer ptype : rapb.getActor().getPtypesList() )
				{
					ipb.setPtype( ptype );
					requests.add( ipb.build() );
				}
			}

			// get datas
			final List< byte[] > datas = ToRedisProperty.UGetProperties( pipline , requests );

			// copy to datas
			int cnt = 0;
			for( RegionActorPair.Builder rapb : resp.getUpdatedBuilderList() )
			{
				_logger.trace( name + ": get updated: \"" + rapb.getActor().getId() + "\"|" + rapb.getActor().getPtypesCount() );
				for( @SuppressWarnings( "unused" )
				Integer ptype : rapb.getActor().getPtypesList() )
				{
					final byte[] data = datas.get( cnt );
					if( null != data )
					{
						rapb.getActorBuilder().addPdatas( ByteString.copyFrom( data ) );
					}
					else
					{
						final ByteString bs = ByteString.copyFrom( new byte[ 0 ] );
						rapb.getActorBuilder().addPdatas( bs );
						_logger.warn( "a property miss data" , new Exception() );
					}
					++cnt;
				}
			}
		}

		// performance log
		message_process[ PERFORMANCE_LOG.BEGIN_CONFIG.get() ] = System.currentTimeMillis();

		// Write Config
		for( Actor actor : p2g.getUpdateActorsList() )
		{
			for( int ci = 0 ; ci < actor.getCtypesCount() ; ++ci )
			{
				final int ctype = actor.getCtypes( ci );
				final byte[] cdata = actor.getCdatas( ci ).toByteArray();
				synchronized( _database )
				{
					_tdbconfig.Write( actor.getId() , ctype , cdata );
				}
			}
		}

		// Read Property
		_logger.trace( name + ": request property count = " + p2g.getRequestPropertyCount() );
		for( IdPtype ap : p2g.getRequestPropertyList() )
		{
			final byte[] data;
			synchronized( _database )
			{
				_logger.trace( name + ": request property \"" + ap.getId() + "\"|" + ap.getPtype() );
				data = _tdbproperty.Read( ap.getId() , ap.getPtype() );
			}
			if( null != data )
			{
				if( data.length > 0 )
				{
					_logger.trace( name + ": response property \"" + ap.getId() + "\"|" + ap.getPtype() );
					resp.addResponseProperty( IdPtypeData.newBuilder().setId( ap.getId() ).setPtype( ap.getPtype() ).setData( ByteString.copyFrom( data ) ) );
				}
				else
				{
					_logger.warn( "a property miss data" , new Exception() );					
				}
			}
			else
			{
				_logger.warn( "can not find property from database" , new Exception() );
			}
		}

		// Read Config
		_logger.trace( name + ": request config count = " + p2g.getRequestConfigCount() );
		for( IdCtype ac : p2g.getRequestConfigList() )
		{
			final byte[] data;
			synchronized( _database )
			{
				data = _tdbconfig.Read( ac.getId() , ac.getCtype() );
			}
			if( null != data )
			{
				if( data.length > 0 )
				{
					resp.addResponseConfig( IdCtypeData.newBuilder().setId( ac.getId() ).setCtype( ac.getCtype() ).setData( ByteString.copyFrom( data ) ) );
				}
				else
				{
					_logger.warn( "a config miss data" , new Exception() );
				}
			}
			else
			{
				_logger.warn( "can not find config from database" , new Exception() );
			}
		}

		// release Jedis
		try
		{
			pipline.close();
		}
		catch( IOException e )
		{
			_logger.error( e , new Exception() );
		}
		synchronized( _redis_pool )
		{
			_redis_pool.Release( jedis );
		}

		// performance log
		message_process[ PERFORMANCE_LOG.END.get() ] = System.currentTimeMillis();
		_logger.debug( name + ": process message in " + ( message_process[ 1 ] - message_process[ 0 ] ) + "," + ( message_process[ 2 ] - message_process[ 1 ] ) + "," + ( message_process[ 3 ] - message_process[ 2 ] ) + "ms" );

		// set server time
		resp.setWorldTime( time_writing );

		return resp.build();
	}

	// operation, deactived and updated
	private static void IdPtypeListToActorListBuilder( final Region region , final List< IdPtype > src , final List< RegionActorPair.Builder > dest )
	{
		for( IdPtype ap : src )
		{
			// find actor
			RegionActorPair.Builder rapb = null;
			for( RegionActorPair.Builder i : dest ) // TODO efficiency
			{
				if( i.getActor().getId() == ap.getId() )
				{
					rapb = i;
					break;
				}
			}

			// create actor if not found
			if( null == rapb )
			{
				// new actor
				final Actor.Builder ab = Actor.newBuilder();
				ab.setId( ap.getId() );

				// add property here
				// add the property here,
				// because after dest.addActors,
				// the actor is no more the actor in dest
				ab.addPtypes( ap.getPtype() );

				// new region actor pair builder
				final RegionActorPair.Builder nrapb = RegionActorPair.newBuilder();

				nrapb.setRegion( region ).setActor( ab.build() );

				// add to dest
				dest.add( nrapb );
			}
			else
			{
				// add property
				rapb.getActorBuilder().addPtypes( ap.getPtype() );
			}
		}
	}

	private static void IdPtypeListToIdentityListBuilder( final Region region , final List< IdPtype > src , final List< RegionIdPair > dest )
	{
		for( IdPtype ap : src )
		{
			// find actor identity
			RegionIdPair rip = null;
			for( RegionIdPair i : dest ) // TODO efficiency
			{
				if( i.getId() == ap.getId() )
				{
					rip = i;
					break;
				}
			}

			// add actor identity
			if( null == rip )
			{
				final RegionIdPair.Builder ripb = RegionIdPair.newBuilder();
				ripb.setRegion( region );
				ripb.setId( ap.getId() );
				dest.add( ripb.build() );
			}
		}
	}

	// remove
	private static void RemovePlayer( final String name , final long time , final GPlayer player , final ToRedisPool redis_pool , final ToDatabase database , final ToDatabaseProperty tdbproperty )
	{
		final Jedis redis;
		synchronized( redis_pool )
		{
			redis = redis_pool.Obtain();
		}

		final Pipeline pipeline = redis.pipelined();

		synchronized( player.owning_actors )
		{
			// read properties and persist into database
			for( Map.Entry< Long , OwningActor > ioa : player.owning_actors.entrySet() )
			{
				final Actor actor = ioa.getValue().actor;
				final long id = actor.getId();

				final IdPtype.Builder ipb = IdPtype.newBuilder();
				ipb.setId( id );

				//
				final List< IdPtype > aps = new ArrayList< IdPtype >();
				for( Integer ptype : actor.getPtypesList() )
				{
					ipb.setPtype( ptype );
					aps.add( ipb.build() );
				}

				// read propertie in Redis
				final List< byte[] > pdatas = ToRedisProperty.UGetProperties( pipeline , aps );

				// persist into database
				for( int pi = 0 ; pi < actor.getPtypesCount() ; ++pi )
				{
					final int ptype = actor.getPtypes( pi );
					final byte[] pdata = pdatas.get( pi );
					synchronized( database )
					{
						tdbproperty.Write( id , ptype , pdata );
					}
				}
			}

			// deactivate and remove properties
			for( Map.Entry< Long , OwningActor > ioa : player.owning_actors.entrySet() )
			{
				final IdPtype.Builder ipb = IdPtype.newBuilder();
				final OwningActor oa = ioa.getValue();
				ipb.setId( oa.actor.getId() );

				for( Integer ptype : oa.actor.getPtypesList() )
				{
					ipb.setPtype( ptype );
					final IdPtype ap = ipb.build();

					// deactivate propertie in Redis
					ToRedisProperty.PDeactivate( pipeline , oa.region , ap , time );

					// remove properties from Redis
					ToRedisProperty.PRemoveProperty( pipeline , ap );
				}

				// log
				_logger.trace( name + ": actor removed: \"" + oa.actor.getId() + "\"|" + oa.actor.getPtypesCount() + " " + oa.region.getIndex() );
			}
			ToRedisProperty.PCommit( pipeline );
		}

		try
		{
			pipeline.close();
		}
		catch( IOException e )
		{
			_logger.error( e , new Exception() );
		}
		synchronized( redis_pool )
		{
			redis_pool.Release( redis );
		}
	}

	private static void PutActorToRedis( final Region region , final Actor actor , final Pipeline pipeline , final long time )
	{
		final IdPtype.Builder ipb = IdPtype.newBuilder();
		ipb.setId( actor.getId() );

		for( int j = 0 ; j < actor.getPtypesCount() ; ++j )
		{
			final Integer ptype = actor.getPtypes( j );
			final ByteString data = actor.getPdatas( j );

			ipb.setPtype( ptype );
			final IdPtype ap = ipb.build();

			ToRedisProperty.PSetActive( pipeline , region , ap , time );
			ToRedisProperty.PSetProperty( pipeline , ap , data.toByteArray() );
		}
	}
}
