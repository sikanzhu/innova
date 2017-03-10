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

package innova.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.apache.log4j.Logger;

import innova.utility.CircularBuffer;
import innova.utility.Constant;

public class ReadHandler implements CompletionHandler< Integer , ByteBuffer >
{
	// interface
	public interface Listener
	{
		void OnRead( final AsynchronousSocketChannel socket , final byte[] buffer );
		void OnReadFailed( final AsynchronousSocketChannel socket , final Throwable e );
	}
	
	// variables
	private final static Logger _logger = Logger.getLogger("");
	
	private final AsynchronousSocketChannel _channel;
	private final Listener _listener;
	
	private CircularBuffer _buffer;
	private int _next_size = 0; // == 0 means data length is not detected.
	
	public ReadHandler
	( final AsynchronousSocketChannel channel , final Listener listener
			, final int max_read_buffer , final int max_package_size )
	{
		_buffer = new CircularBuffer( max_read_buffer );
		_channel = channel;
		_listener = listener;
		
		final ByteBuffer read_buffer = ByteBuffer.allocate( max_package_size );
		_channel.read( read_buffer , read_buffer , this );
	}

	@Override
	public void completed( final Integer result , ByteBuffer attachment )
	{
		_logger.trace( new Exception().getStackTrace()[0] + "| read: " + result );
		
		if( result < 0 )
		{
			_listener.OnReadFailed( _channel , new Exception() );
			try
			{
				_channel.close();
			}
			catch( IOException e )
			{
				_logger.error( new Exception().getStackTrace()[0] , e );
			}
			
			return;
		}
		
		// copy to circular reading buffer
		if( 0 == _buffer.Write( attachment.array() , attachment.position() ) )
		{
			_logger.error( new Exception( "| read buffer overflow: " + result ).getStackTrace()[0] );
		}
		
		// if has more data
		while( _buffer.Size() >= _next_size )
		{
			// is not waiting for data?
			if( _next_size == 0 ) // == 0 means data length is not detected.
			{
				if( _buffer.Size() >= Constant.SIZE_OF_INT )
				{
					final byte[] sb = _buffer.Read( Constant.SIZE_OF_INT ); 
					_next_size = ByteBuffer.wrap( sb ).getInt();
					_logger.trace( new Exception().getStackTrace()[0] + "| read next size: " + _next_size );
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
					final byte[] b = _buffer.Read( _next_size );
					_listener.OnRead( _channel , b );
					_logger.trace( new Exception().getStackTrace()[0] + "| on read: " + b.length );
					_next_size = 0; // == 0 means data length is not detected.
				}
			}
		}
		
		// continue reading
		attachment.rewind();
		_channel.read( attachment , attachment , this );
	}

	@Override
	public void failed( final Throwable exc , ByteBuffer attachment )
	{
		_logger.warn( exc );
		_listener.OnReadFailed( _channel , exc );
		try
		{
			_channel.close();
		}
		catch( IOException e )
		{
			_logger.error( new Exception().getStackTrace()[0] , e );
		}
	}
}
