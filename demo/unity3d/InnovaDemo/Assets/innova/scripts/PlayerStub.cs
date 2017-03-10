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

using UnityEngine;
using innova.connect.client;
using innova.common;
using Innova.Protocol;
using Google.Protobuf.Collections;
using System.Collections.Generic;
using Google.Protobuf;

namespace innova
{
	public class PlayerStub<OBJECT> : PlayerGate.IListener
	{
		// thread safe variables
		private AgentManager<OBJECT> _agent_manager = new AgentManager<OBJECT>();
		private ActorManager _actor_manager = new ActorManager();
		private Queue<GateToPlayer> _messages = new Queue<GateToPlayer>();

		// single thread variables
		private float _network_timer = 0;
		private PlayerGate _gate = null;

		// watch and unwatch region
		public void RegionWatch( RepeatedField<Region> rs )
		{
			// copy to message
			_actor_manager.RegionWatch( rs );

			// destroy actors out of the regions
			_agent_manager.RegionWatch( rs );
		}

		// actor methods
		public void ActorSetRegion( long id , Region src , Region dest , Dictionary<int , ByteString> properties )
		{
			_actor_manager.ActorSetRegion( id , src , dest , properties );
		}

		public void ActorUpdate( Region region , long id , Dictionary<int , ByteString> properties )
		{
			_actor_manager.ActorUpdate( region , id , properties );
		}

		public void OperationPush( long src , long dest , int ptype , ByteString data )
		{
			_actor_manager.OperationPush( src , dest , ptype , data );
        }

		// requests
		public void RequestProperty( long id , int ptype )
		{
			_actor_manager.RequestProperty( id , ptype );
		}

		public void RequestConfig( long id , int ctype )
		{
			_actor_manager.RequestConfig( id , ctype );
		}

		// initialize
		public void Init( AgentUtils<OBJECT>.IAgentsListener al , ActorUtils.IActorsListener pl )
		{
			_agent_manager.Init( al );
			_actor_manager.Init( pl );

			_gate = new PlayerGate( this );
			if( false == _gate.Connect
				( Constant.ADRESS_LOCALHOST , Constant.GATE_PLAYER_PORT
				, Constant.MAX_READ , Constant.MAX_WRITE , Constant.MAX_PACKAGE ) )
			{
				Debug.LogErrorFormat( "Can not connect to gate." );
			}
		}

		// deinitialize
		public void Deinit()
		{
			_gate.Disconnect();

			_agent_manager.Deinit();
			_actor_manager.Deinit();
		}

		// called every update
		public void SendMessage()
		{
			// network
			_network_timer += Time.deltaTime;
			if( _network_timer >= Constant.NETWORK_INTERVAL )
			{
				_network_timer -= Constant.NETWORK_INTERVAL;
				_actor_manager.MessageSend( _gate );
			}
		}

		public void ProcessMessage()
		{
			GateToPlayer[] messages = null;
			lock( _messages )
			{
				if( _messages.Count > 0 )
				{
					messages = new GateToPlayer[ _messages.Count ];
					_messages.CopyTo( messages , 0 );
				}
				_messages.Clear();
			}
			if( null != messages )
			{
				foreach( GateToPlayer msg in messages )
				{
					_actor_manager.MessageProcess( msg );
					_agent_manager.MessageProcess( msg );
				}
			}
		}

		// methods of IListener
		// internal use only
		public void ProcessMessage( GateToPlayer msg )
		{
			lock( _messages )
			{
				_messages.Enqueue( msg );
			}
		}
	}
}
