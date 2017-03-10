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

package innova;

import org.apache.log4j.Logger;

import innova.client.GateWorld;
import innova.common.Constant;
import innova.database.ToDatabase;
import innova.redis.ToRedisPool;
import innova.server.GatePlayer;
import innova.core.IPlugin;


public class Gate implements IPlugin , GateWorld.IListener
{
	private final static Logger _logger = Logger.getLogger("");
	private GateWorld _world;
	private GatePlayer _player;
	private ToRedisPool _redis;
	private ToDatabase _database;
	
	// implement of IPlugin
	@Override
	public void Init( final String[] args )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
		_redis = new ToRedisPool();
		_database = new ToDatabase();
		_world = new GateWorld( this );
		_player = new GatePlayer();
	}

	@Override
	public float Start( final String[] args )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
		
		if( false == _redis.Connect( Constant.ADRESS_LOCALHOST ))
		{
			_logger.error( new Exception( "Can not connect to Redis." ) );
			return -1;
		}
		
		boolean connected;
		if( args.length >= 3 )
		{
			connected = _database.Connect( args[2] );
		}
		else
		{
			connected = _database.Connect( Constant.DEFAULT_DATABASE_FILE );
		}
		if( false == connected )
		{
			_logger.error( new Exception( "Can not connect to database." ) );
			return -1;
		}
		_player.Init( _redis , _database );
		
		if( false == _world.Connect
				( Constant.ADRESS_LOCALHOST , Constant.WORLD_GATE_PORT
						, Constant.MAX_READ
						, Constant.WRITE_TIMEOUT
						, Constant.MAX_WRITE
						, Constant.MAX_PACKAGE ) )
		{
			_logger.error( "Can not connect to world." );
			return -1;
		}
		
		if( false == _player.Accept
				( Constant.ADDRESS_ANY , Constant.GATE_PLAYER_PORT
						, Constant.MAX_READ
						, Constant.WRITE_TIMEOUT
						, Constant.MAX_WRITE
						, Constant.MAX_PACKAGE ) )
		{
			_logger.error( "Can not listen to players." );
			return -1;
		}
		
		return Constant.GATE_UPDATE_INTERVAL;
	}

	@Override
	public boolean Update( final float elapse )
	{
		_logger.trace( new Exception().getStackTrace()[0] + "( elapse = " + elapse + ")" );
		return true;
	}

	@Override
	public void Pause()
	{
		_logger.trace( new Exception().getStackTrace()[0] );
	}

	@Override
	public void Resume()
	{
		_logger.trace( new Exception().getStackTrace()[0] );
	}

	@Override
	public void Stop( final String[] args )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
		_world.Disconnect();
		_player.Deinit();
		_database.Disconnect();
		_redis.Disconnect();
	}

	@Override
	public void Deinit( final String[] args )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
	}

	// implementation of GateWorld.IListener
	@Override
	public void WorldSynchronizeTime( long time )
	{
		_player.WorldSynchronizeTime( time );
	}
}
