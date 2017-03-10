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

package innova.demo;

import innova.common.Constant;
import innova.common.InnovaCommon.Float3;
import innova.common.InnovaCommon.Float4;
import innova.common.InnovaCommon.Quaternion;
import innova.core.IPlugin;
import innova.database.ToDatabase;
import innova.database.ToDatabaseAccount;
import innova.database.ToDatabaseConfig;
import innova.database.ToDatabasePlayer;
import innova.database.ToDatabaseProperty;
import innova.demo.InnovaDemo.ActorBasic;
import innova.demo.InnovaDemo.ActorMovement;
import innova.demo.InnovaDemo.ActorScale;
import innova.demo.InnovaDemo.CTYPE;
import innova.demo.InnovaDemo.ConfigGraphics;
import innova.demo.InnovaDemo.PTYPE;

public class Register implements IPlugin
{
	final ToDatabase _database = new ToDatabase();
	ToDatabaseAccount _tda = null;
	ToDatabasePlayer _tdp = null;
	ToDatabaseProperty _tdr = null;
	ToDatabaseConfig _tdc = null;
	
	//
	@Override
	public void Init( final String[] args )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Deinit( final String[] args )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Pause()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Resume()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public float Start( final String[] args )
	{
		// connect and create tables
		if( args.length >= 3 )
		{
			_database.Connect( args[2] );
		}
		else
		{
			_database.Connect( Constant.DEFAULT_DATABASE_FILE );
		}
				
		if( args.length >= 4 )
		{
			if( args[3].equals( "help" ) )
			{
				Help();
			}
			else if( args[3].equals( "table" ) )
			{
				_database.CreateTables();
			}
			else if( args[3].equals( "register" ) )
			{
				InitTables();
				
				final String[] param = args[4].split( "," );
				if( param.length >= 3 )
				{
					RegisterAccount( Long.parseLong( param[0] ) , param[1] , param[2] );			
				}
				else
				{
					Help();
				}
			}
			else if( args[3].equals( "create" ) )
			{
				InitTables();
				
				final String[] param = args[4].split( "," );
				
				if( param.length >= 6 )
				{
					final long account = Long.parseLong( param[0] );
					final long player = Long.parseLong( param[1] );
					final String name = param[2];
					final String appearance = "innova/demos/appearance/AppPlayer";
					final float x = Float.parseFloat( param[3] );
					final float y = Float.parseFloat( param[4] );
					final float z = Float.parseFloat( param[5] );
					
					// create player
					CreatePlayer( player , account , name );
					
					// properties
					final ActorBasic.Builder abb = ActorBasic.newBuilder();
					final ActorMovement.Builder amb = ActorMovement.newBuilder();
					final ActorScale.Builder asb = ActorScale.newBuilder();
					
					{
						abb.setName( name ).setAppearance( appearance );
						amb.setLocation( Float3.newBuilder().setX( x ).setY( y ).setZ( z ) );
						amb.setVelocity( Float3.newBuilder().setX( 0 ).setY( 0 ).setZ( 0 ) );
						amb.setRotation( Quaternion.newBuilder().setS( 1 ).setI( 0 ).setJ( 0 ).setK( 0 ) );
						amb.setAngular( Float4.newBuilder().setX( 0 ).setY( 0 ).setZ( 0 ).setW( 0 ) );
						asb.setScale( Float3.newBuilder().setX( 1 ).setY( 1 ).setZ( 1 ) );
					}
					
					// configs
					final ConfigGraphics.Builder cgb = ConfigGraphics.newBuilder();
					
					{
						cgb.setMaxCameraDistance( 20 + 2 * x ).setMaxViewDistance( 5 * x );
					}
					
					// write to database
					InsertProperty( player , PTYPE.BASIC_VALUE , abb.build().toByteArray() );
					InsertProperty( player , PTYPE.MOVEMENT_VALUE , amb.build().toByteArray() );
					InsertProperty( player , PTYPE.SCALE_VALUE , asb.build().toByteArray() );
					InsertConfig( player , CTYPE.GRAPHICS_VALUE , cgb.build().toByteArray() );
				}
				else
				{
					Help();
				}
			}
			else
			{
				Help();
			}
		}
		return -1;
	}

	@Override
	public void Stop( final String[] args )
	{
		DeinitTables();
		_database.Disconnect();
	}

	@Override
	public boolean Update( float arg0 )
	{
		// TODO Auto-generated method stub
		return false;
	}

	//
	private void RegisterAccount
		( final long id
		, final String name
		, final String password )
	{
		_tda.Add( id );
		_tda.WriteNamePassword( id , name , password );
	}
	
	private void CreatePlayer
	( final long id
			, final long account
			, final String name )
	{
		_tdp.Add( id , account , name );
	}
	
	private void InsertProperty
	( final long player
			, final int type
			, final byte[] data )
	{
		_tdr.Add( player , type );
		_tdr.Write( player , type , data );
	}
	
	private void InsertConfig
	( final long player
			, final int type
			, final byte[] data )
	{
		_tdc.Add( player , type );
		_tdc.Write( player , type , data );
	}
	
	private void Help()
	{
		System.out.println( "Help" );
		System.out.println( "$database_filename table" );
		System.out.println( "$database_filename register $account_id,$username,$password" );
		System.out.println( "$database_filename create $account_id,$player_id,$player_name,$x,$y,$z" );
	}
	
	private void InitTables()
	{
		_tda = new ToDatabaseAccount( _database.GetConnection() );
		_tdp = new ToDatabasePlayer( _database.GetConnection() );
		_tdr = new ToDatabaseProperty( _database.GetConnection() );
		_tdc = new ToDatabaseConfig( _database.GetConnection() );
		
		_tda.Init();
		_tdp.Init();
		_tdr.Init();
		_tdc.Init();
	}
	
	private void DeinitTables()
	{
		// deinit
		if( null != _tda ) _tda.Deinit();
		if( null != _tdp ) _tdp.Deinit();
		if( null != _tdr ) _tdr.Deinit();
		if( null != _tdc ) _tdc.Deinit();
	}
}
