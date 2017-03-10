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

using System;
using System.Net;
using System.Net.Sockets;
using UnityEngine;

namespace innova.aio
{
	public abstract class Client : ReadHandler.Listener , WriteHandler.Listener
	{
		// constants
		private int _max_read_buffer;
		private int _max_write_buffer;
		private int _max_package_size;

		// variables
		private Socket _client = null;
		private WriteHandler _writer = null;

		// callbacks
		private void ConnectCallback( IAsyncResult ar )
		{
			//
			Socket client = ( Socket )ar.AsyncState;
			client.EndConnect( ar );
			_client = client;

			//
			/*ReadHandler _reader = */new ReadHandler( _client , this , _max_read_buffer , _max_package_size );
			_writer = new WriteHandler( _client , this , _max_write_buffer , _max_package_size );

			//
			OnConnect( client );
		}

		// methods
		public bool Connect( string address , int port
			, int max_read_buffer
			, int max_write_buffer
			, int max_package_size )
		{
			//
			_max_read_buffer = max_read_buffer;
			_max_write_buffer = max_write_buffer;
			_max_package_size = max_package_size;

			//
			IPAddress ip_address = IPAddress.Parse( address );
			IPEndPoint remote_ep = new IPEndPoint( ip_address , port );
			Socket client = new Socket( AddressFamily.InterNetwork , SocketType.Stream , ProtocolType.Tcp );
			client.BeginConnect( remote_ep , new AsyncCallback( ConnectCallback ) , client );
			return true;
		}

		public void Disconnect()
		{
			if( null != _client )
			{
				// Release the socket.
				if( _client.Connected )
				{
					_client.Shutdown( SocketShutdown.Both );
				}
				_client.Close();
				_client = null;
			}
			else
			{
				Debug.LogErrorFormat( "socket is null" );
			}
		}

		public int Write( byte[] buffer )
		{
			return _writer.Write( buffer );
		}

		// interfaces
		protected abstract void OnConnect( Socket socket );
		public abstract void OnRead( Socket socket , byte[] buffer );
		public abstract void OnWrite( Socket socket , int size );
	}
}