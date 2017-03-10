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
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

abstract class SlotListener<CLIENT>
{
	public abstract void OnRead( final CLIENT client , final AsynchronousSocketChannel socket , final byte[] buffer );
	public abstract void OnWrite( final CLIENT client , final AsynchronousSocketChannel socket , final int size );
	public abstract void OnReadFailed( final CLIENT client , final AsynchronousSocketChannel socket , final Throwable e );
	public abstract void OnWriteFailed( final CLIENT client , final AsynchronousSocketChannel socket , final Throwable e );
}

public abstract class Server<CLIENT> extends SlotListener<CLIENT>
{
	// constants
	private int _max_read_buffer;
	private int _write_timeout;
	private int _max_write_buffer;
	private int _max_package_size;
	
	// variables
	private final static Logger _logger = Logger.getLogger("");
	
	private AsynchronousServerSocketChannel _server;
	private final Map<AsynchronousSocketChannel,Slot> _slots = new HashMap<AsynchronousSocketChannel,Slot>();

	private class Slot implements innova.aio.ReadHandler.Listener, innova.aio.WriteHandler.Listener
	{		
		public final CLIENT client;
		public final AsynchronousSocketChannel socket;
		public final SlotListener<CLIENT> listener;
		public final WriteHandler write_handler;
		
		public Slot( final CLIENT d , final AsynchronousSocketChannel s
				, final int max_read_buffer
				, final int write_timeout
				, final int max_write_buffer
				, final int max_package_size
				, final SlotListener< CLIENT > lis )
		{
			client = d;
			socket = s;
			listener = lis;
			
			@SuppressWarnings( "unused" )
			final ReadHandler read_handler = new ReadHandler
					( socket , this , max_read_buffer , max_package_size ); // when should I stop reading?
			
			write_handler = new WriteHandler
					( socket , this
					, write_timeout
					, max_write_buffer
					, max_package_size );
		}

		@Override
		public void OnRead( AsynchronousSocketChannel socket , byte[] buffer )
		{
			listener.OnRead( client , socket , buffer );
		}

		@Override
		public void OnReadFailed( AsynchronousSocketChannel socket , Throwable e )
		{
			listener.OnReadFailed( client , socket , e );
			synchronized(_slots)
			{
				try
				{
					socket.close();
				}
				catch( IOException es )
				{
					_logger.error( es , new Exception() );
				}
				_slots.remove( socket );
			}
		}

		@Override
		public void OnWrite( AsynchronousSocketChannel socket , int size )
		{
			listener.OnWrite( client , socket , size );
		}

		@Override
		public void OnWriteFailed( AsynchronousSocketChannel socket , Throwable e )
		{
			listener.OnWriteFailed( client , socket , e );
			synchronized(_slots)
			{
				try
				{
					socket.close();
				}
				catch( IOException es )
				{
					_logger.error( es );
				}
				_slots.remove( socket );
			}
		}
	}

	// handlers
	private class AcceptHandler implements CompletionHandler< AsynchronousSocketChannel , Void >
	{
		private final SlotListener<CLIENT> _listener;
		
		public AcceptHandler( SlotListener<CLIENT> lis )
		{
			_listener = lis;
		}
		
		@Override
		public void completed( final AsynchronousSocketChannel socket , Void attachment )
		{
			_logger.trace( new Exception().getStackTrace()[0] );

			synchronized(_slots)
			{
				_slots.put( socket , new Slot
						( OnAccept( socket ) , socket
								, _max_read_buffer
								, _write_timeout
								, _max_write_buffer
								, _max_package_size
								, _listener ) );
			}
			
			_server.accept( null , this );
		}

		@Override
		public void failed( final Throwable exc , Void attachment )
		{
			_logger.error( exc );
			OnAcceptFailed( exc ); 
		}
	}
	
	// methods
	
	// @ return: Successfully listen to address:port
	public boolean Accept
		( final String address , final int port
			, final int max_read_buffer
			, final int write_timeout
			, final int max_write_buffer
			, final int max_package_size
 )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
		
		//
		_max_read_buffer = max_read_buffer;
		_write_timeout = write_timeout;
		_max_write_buffer = max_write_buffer;
		_max_package_size = max_package_size;
		
		// create AsynchronousServerSocketChannel
		try
		{
			_server = AsynchronousServerSocketChannel.open();
		}
		catch( IOException e )
		{
			_logger.error( e );
			return false;
		}
		
		try
		{
			_server.bind( new InetSocketAddress( address , port ) );
		}
		catch( IOException e )
		{
			_logger.error( e );
			return false;
		}
		
		// begin to accept
		_server.accept( null , new AcceptHandler( this ) );
		
		return true;
	}
	
	public int Write( final AsynchronousSocketChannel socket , final byte[] buffer )
	{
		_logger.trace( new Exception().getStackTrace()[0] + "| write: " + buffer.length );
		synchronized(_slots)
		{
			final Slot slot = _slots.get( socket );
			if( null != slot )
			{
				return slot.write_handler.Write( buffer );
			}
		}
		return 0;
	}

	public void Broadcast( final byte[] buffer )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
		synchronized(_slots)
		{
			for (Map.Entry<AsynchronousSocketChannel, Slot> entry : _slots.entrySet())
			{
				entry.getValue().write_handler.Write( buffer );
			}
		}
	}

	protected abstract CLIENT OnAccept( final AsynchronousSocketChannel socket );
	protected abstract void OnAcceptFailed( final Throwable e );
}
