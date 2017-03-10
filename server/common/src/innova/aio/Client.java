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
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.apache.log4j.Logger;

public abstract class Client implements innova.aio.ReadHandler.Listener, innova.aio.WriteHandler.Listener
{
	// variables
	private final static Logger _logger = Logger.getLogger("");
	
	private AsynchronousSocketChannel _client;
	
	private WriteHandler _write_handler;

	// handlers
	private class ConnectHandler implements CompletionHandler< Void , Void >
	{
		@Override
		public void completed( Void result , Void attachment )
		{
			_logger.trace( new Exception().getStackTrace()[0] );
			OnConnect( _client );
		}

		@Override
		public void failed( Throwable exc , Void attachment )
		{
			_logger.error( exc , new Exception() );
			OnConnectFailed( exc );
			try
			{
				_client.close();
			}
			catch( IOException e )
			{
				_logger.error( e , new Exception() );
			}
		}
	}

	// methods
	public boolean Connect
	( final String address , final int port
			, final int max_read_buffer
			, final int write_timeout
			, final int max_write_buffer
			, final int max_package_size )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
		
		// open
		try
		{
			_client = AsynchronousSocketChannel.open();
		}
		catch( IOException e )
		{
			_logger.error( e , new Exception() );
			return false;
		}
		
		// connect
		_client.connect( new InetSocketAddress( address , port ) , null , new ConnectHandler() );	
				
		// begin reading
		@SuppressWarnings( "unused" )
		final ReadHandler read_handler = new ReadHandler
				( _client , this , max_read_buffer , max_package_size ); // when should I stop reading?
		
		// create _write_handler
		_write_handler = new WriteHandler
				(_client , this
				, write_timeout
				, max_write_buffer
				, max_package_size );

		return true;
	}

	public void Disconnect()
	{
		_logger.trace( new Exception().getStackTrace()[0] );
		try
		{
			_client.close();
		}
		catch( IOException e )
		{
			_logger.error( e , new Exception() );
		}
	}
	
	public int Write( final byte[] buffer )
	{
		return _write_handler.Write( buffer );
	}

	protected abstract void OnConnect( final AsynchronousSocketChannel socket );
	protected abstract void OnConnectFailed( final Throwable e );
}
