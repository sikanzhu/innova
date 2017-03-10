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
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import innova.utility.CircularBuffer;
import innova.utility.Constant;

public class WriteHandler implements CompletionHandler< Integer , Void >
{
	// interface
	public interface Listener
	{
		void OnWrite( final AsynchronousSocketChannel socket , final int size );
		void OnWriteFailed( final AsynchronousSocketChannel socket , final Throwable e );
	}
	
	// constants
	private int _write_timeout;
	private int _max_package_size;
	
	// variables
	private final static Logger _logger = Logger.getLogger("");

	private final AsynchronousSocketChannel _channel;
	private final Listener _listener;
	private final CircularBuffer _write_buffer;
	private boolean _writing = false;
	
	private int _summon = 0;
	
	private final ByteBuffer _length_translate = ByteBuffer.allocate( Constant.SIZE_OF_INT );
	
	public WriteHandler
	( final AsynchronousSocketChannel channel , final Listener listener
			, final int write_timeout
			, final int max_write_buffer
			, final int max_package_size )
	{
		_channel = channel;
		_listener = listener;
		_write_timeout = write_timeout;
		_write_buffer = new CircularBuffer(max_write_buffer);
		_max_package_size = max_package_size;
	}
	
	@Override
	public void completed( final Integer result , Void attachment )
	{
		_logger.trace( new Exception().getStackTrace()[0] + "| wrote: " + result );
		synchronized(_write_buffer)
		{
			_summon += result;
			
			if( _write_buffer.Size() > 0 )
			{
				final int len = Math.min( _max_package_size , _write_buffer.Size() );				
				final byte[] b = _write_buffer.Read( len );
				_channel.write( ByteBuffer.wrap( b ) , _write_timeout , TimeUnit.MILLISECONDS , null , this );
			}
			else
			{
				_listener.OnWrite( _channel , _summon );
				_summon = 0;
				_writing = false;
			}
		}
	}
	
	@Override
	public void failed( final Throwable exc , Void attachment )
	{
		_logger.error( exc , new Exception() );
		_listener.OnWriteFailed( _channel , exc );
		try
		{
			_channel.close();
		}
		catch( IOException e )
		{
			_logger.error( e , new Exception() );
		}
	}
	
	public int Write( final byte[] buffer )
	{
		if( null == buffer ) return 0;
		if( 0 == buffer.length ) return 0;
		
		_logger.trace( new Exception().getStackTrace()[0] + "| write: " + buffer.length );
		
		synchronized(_write_buffer)
		{
			// has enough buffer?
			if( _write_buffer.Left() > Constant.SIZE_OF_INT + buffer.length )
			{
				// write length
				byte[] len_bytes = _length_translate.putInt(buffer.length).array();
				_write_buffer.Write( len_bytes );
				_length_translate.rewind();
				
				// write data
				_write_buffer.Write( buffer );
				
				// begin writing to server
				if( false == _writing )
				{
					final int len = Math.min( _max_package_size , _write_buffer.Size() );				
					final byte[] b = _write_buffer.Read( len );
					_channel.write( ByteBuffer.wrap( b ) , _write_timeout , TimeUnit.MILLISECONDS , null , this );
					_writing = true;
				}
				
				return buffer.length;
			}
			
			return 0;
		}
	}
}
