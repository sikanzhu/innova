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
	public class WriteHandler
	{
		// interface
		public interface Listener
		{
			void OnWrite( Socket socket , int size );
		}

		// constants
		private int _max_package_size;

		private Socket _channel;
		private Listener _listener;
		private CircularBuffer _write_buffer;
		private bool _writing = false;

		private int _summon = 0;
	
		public WriteHandler
			( Socket channel , Listener listener
					, int max_write_buffer
					, int max_package_size )
		{
			_channel = channel;
			_listener = listener;
			_write_buffer = new CircularBuffer( max_write_buffer );
			_max_package_size = max_package_size;
		}

		private void WriteCallback( IAsyncResult ar )
		{
			//
			//byte[] wcb = ( byte[] )ar.AsyncState;
			int bytes_sent = _channel.EndSend( ar );

			//
			lock ( _write_buffer )
			{
				_summon += bytes_sent;

				if( _write_buffer.Size() > 0 )
				{
					int len = Math.Min( _max_package_size , _write_buffer.Size() );
					byte[] b = _write_buffer.Read( len );
					_channel.BeginSend
						( b , 0 , b.Length
						, SocketFlags.None
						, new AsyncCallback( WriteCallback )
						, b );
				}
				else
				{
					_listener.OnWrite( _channel , _summon );
					_summon = 0;
					_writing = false;
				}
			}
		}

		public int Write( byte[] buffer )
		{
			if( null == buffer ) return 0;
			if( 0 == buffer.Length ) return 0;

			lock( _write_buffer )
	        {
				// has enough buffer?
				if( _write_buffer.Left() > Constant.SIZE_OF_INT + buffer.Length )
				{
					// write length
					byte[] len_bytes = ByteConverter.IntToBytes( buffer.Length );
					_write_buffer.Write( len_bytes );

					// write data
					_write_buffer.Write( buffer );

					// begin writing to server
					if( false == _writing )
					{
						int len = Math.Min( _max_package_size , _write_buffer.Size() );
						byte[] b = _write_buffer.Read( len );
						_channel.BeginSend
							( b , 0 , b.Length
							, SocketFlags.None
							, new AsyncCallback( WriteCallback )
							, b );
						_writing = true;
					}

					return buffer.Length;
				}

				return 0;
			}
		}
	}
}