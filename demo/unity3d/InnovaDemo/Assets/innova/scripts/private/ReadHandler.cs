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
using System.Net.Sockets;
using innova.common;

namespace innova.aio
{
	public class ReadHandler
	{
		// interface
		public interface Listener
		{
			void OnRead( Socket socket , byte[] buffer );
		}

		// variables
		private Socket _channel;
		private Listener _listener;
	
		private CircularBuffer _buffer;
		private int _next_size = 0; // == 0 means data length is not detected.

		public ReadHandler
			( Socket channel , Listener listener
			, int max_read_buffer , int max_package_size )
		{
			_buffer = new CircularBuffer( max_read_buffer );
			_channel = channel;
			_listener = listener;

			byte[] buffer = new byte[ max_package_size ];
			_channel.BeginReceive
				( buffer , 0 , max_package_size
				, SocketFlags.None
				, new AsyncCallback( ReadCallback )
				, buffer );
		}

		private void ReadCallback( IAsyncResult ar )
		{
			byte[] rcb = ( byte[] )ar.AsyncState;
			int bytes_read = _channel.EndReceive( ar );

			ConsoleOutput.Trace( "ReadCallback: " + bytes_read );

			// copy to circular reading buffer
			_buffer.Write( rcb , bytes_read );

			// if has more data
			while( _buffer.Size() >= _next_size )
			{
				// is not waiting for data?
				if( _next_size == 0 ) // == 0 means data length is not detected.
				{
					if( _buffer.Size() >= Constant.SIZE_OF_INT )
					{
						byte[] sb = _buffer.Read( Constant.SIZE_OF_INT );
						_next_size = ByteConverter.BytesToInt( sb );
					}
					else
					{
						break;
					}
				}

				// is waiting for data?
				if( _next_size > 0 )
				{
					// has enough data?
					if( _buffer.Size() >= _next_size )
					{
						byte[] b = _buffer.Read( _next_size );
						_listener.OnRead( _channel , b );
						ConsoleOutput.Trace( "OnRead: " + b.Length );
						_next_size = 0; // == 0 means data length is not detected.
					}
				}
			}

			// continue reading
			_channel.BeginReceive
				( rcb , 0 , rcb.Length
				, SocketFlags.None
				, new AsyncCallback( ReadCallback )
				, rcb );
		}
	}
}