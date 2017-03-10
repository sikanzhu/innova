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

using Innova.Protocol;
using Google.Protobuf;
using Google.Protobuf.Collections;
using UnityEngine;
using System.Collections.Generic;
using innova.common;
using Innova.Demo;
using innova;

[RequireComponent( typeof( AgentManagement ) )]
public class TryPlayerStub : MonoBehaviour, ActorUtils.IActorsListener
{
	// enum
	enum STATE
	{
		INIT ,
		RUN ,
		NUM ,
	}

	// stub
	private PlayerStub<TryAgent> _player_stub = new PlayerStub<TryAgent>();

	// _player self
	private Region _last_region = new Region();
	public Player _player;
	private STATE _state = STATE.INIT;

	// utilities
	private RepeatedField<Region> GenerateWatchingRegions( Region region )
	{
		int xx, yy, zz, ww;
		Common.FromRegion( region , out xx , out yy , out zz , out ww );

		const int y = 0;
		const int w = 0;
		RepeatedField<Region> ret = new RepeatedField<Region>();
		for( int x = Common.REGION_WATCH_MINX ; x < Common.REGION_WATCH_MAXX ; ++x )
		{
			for( int z = Common.REGION_WATCH_MINX ; z < Common.REGION_WATCH_MAXX ; ++z )
			{
				Region r = Common.ToRegion( xx + x , yy + y , zz + z , ww + w );
				ret.Add( r );
			}
		}
		return ret;
	}

	// implement of MonoBehaviour
	public void Start()
	{
		// init
		_player_stub.Init( GetComponent<AgentManagement>() , this );
	}

	public void OnDestroy()
	{
		if( null != _player.AMove )
		{
			// unown actor
			_player_stub.ActorSetRegion( GameState.Singleton.PlayerId , _player.AMove.CurrentRegion , null , null );
		}

		// deinit
		_player_stub.Deinit();
	}
	
	public void Update()
	{
		switch( _state )
		{
		case STATE.INIT:
			DoInit();
			break;
		case STATE.RUN:
			DoRun();
			break;
		default:
			ConsoleOutput.Error( "TryPlayerStub: unknown state." );
			break;
		}

		// update _player stub. send new message
		_player_stub.SendMessage();
		_player_stub.ProcessMessage();
	}

	// implement of IActorsListener
	public void ialOperate( long src , long dest , int ptype , ByteString data )
	{
		//TODO process operations
	}

	public void ialResponseProperties( long id , Dictionary<int , ByteString> properties )
	{
		//TODO process properties
		ConsoleOutput.Trace( "ialResponseProperties: " + id + "." + properties.Count );
		foreach( KeyValuePair<int , ByteString> kvp in properties )
		{
			switch( ( PTYPE )kvp.Key )
			{
			case PTYPE.Basic:
				_player.ABasic = ActorBasic.Parser.ParseFrom( kvp.Value );
				break;
			case PTYPE.Movement:
				_player.SetMovement( ActorMovement.Parser.ParseFrom( kvp.Value ) );
				_last_region = _player.AMove.CurrentRegion;
				break;
			case PTYPE.Scale:
				_player.AScale = ActorScale.Parser.ParseFrom( kvp.Value );
				break;
			default:
				ConsoleOutput.Error( "TryPlayerStub: unknown response property type" );
				break;
			}
		}

		if( STATE.INIT == _state ) //TODO thread safe?
		{
			if( null != _player.ABasic && null != _player.AMove && null != _player.AScale )
			{
				RepeatedField<Region> regions = GenerateWatchingRegions( _player.AMove.CurrentRegion );
				_player_stub.RegionWatch( regions );
				_player_stub.ActorSetRegion( GameState.Singleton.PlayerId , null , _player.AMove.CurrentRegion , _player.BuildProperties() );

				_state = STATE.RUN;
			}
		}
	}

	public void ialResponseConfigs( long id , Dictionary<int , ByteString> configs )
	{
		//TODO process configs
		ConsoleOutput.Trace( "ialResponseConfigs: " + id + "." + configs.Count );
	}

	// methods
	private void DoInit()
	{
		if( null == _player.ABasic ) _player_stub.RequestProperty( GameState.Singleton.PlayerId , ( int )PTYPE.Basic );
		if( null == _player.AMove ) _player_stub.RequestProperty( GameState.Singleton.PlayerId , ( int )PTYPE.Movement );
		if( null == _player.AScale ) _player_stub.RequestProperty( GameState.Singleton.PlayerId , ( int )PTYPE.Scale );
		_player_stub.RequestConfig( GameState.Singleton.PlayerId , ( int )CTYPE.Graphics );
	}

	private void DoRun()
	{
		float delta_time = Time.deltaTime;

		//TODO update properties:
		Dictionary<int , ByteString> updated_properties = new Dictionary<int , ByteString>();

		// update properties: MOVEMENT
		{
			// build ActorMovement
			if( _player.AMove.IsMoved )
			{
				// create property dictionary
				updated_properties.Add( ( int )PTYPE.Movement , _player.AMove.Movement.ToByteString() );

				// if the _player changes region.
				if( false == _last_region.Equals( _player.AMove.CurrentRegion ) )
				{
					// watch new regions
					RepeatedField<Region> regions = GenerateWatchingRegions( _player.AMove.CurrentRegion );
					_player_stub.RegionWatch( regions );

					// set region, if the _player changes region.
					_player_stub.ActorSetRegion( GameState.Singleton.PlayerId , _last_region , _player.AMove.CurrentRegion , _player.BuildProperties() );

					// _last_region
					_last_region = _player.AMove.CurrentRegion.Clone();
				}
			}
		}

		// update the message
		_player_stub.ActorUpdate( _player.AMove.CurrentRegion , GameState.Singleton.PlayerId , updated_properties );

		//TODO push operations that affect other actors
	}
}
