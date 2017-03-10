﻿/* Copyright (c) 2017, Sikan Zhu
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

using System.Net.Sockets;
using Innova.Protocol;
using UnityEngine;

namespace innova.connect.client
{
	public class PlayerGate : innova.aio.Client
	{
		IListener _listener;

		// interface
		public interface IListener
		{
			void ProcessMessage( GateToPlayer msg );
		}

		public PlayerGate( IListener lis )
		{
			if( null == lis )
			{
				Debug.LogWarning( "Listener is null" );
			}

			_listener = lis;
		}
		
		protected override void OnConnect( Socket socket )
		{
		}

		public override void OnRead( Socket socket , byte[] buffer )
		{
			if( buffer.Length > 0 )
			{
				GateToPlayer g2p = GateToPlayer.Parser.ParseFrom( buffer );

				switch( g2p.Type )
				{
				case GateToPlayer.Types.ETYPE.Message:
					_listener.ProcessMessage( g2p );
					break;
				default:
					Debug.LogWarning( "Unknown message from gate" );
					break;
				}
			}
		}

		public override void OnWrite( Socket socket , int size )
		{
		}
	}
}