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

package innova.client;

import java.nio.channels.AsynchronousSocketChannel;

import org.apache.log4j.Logger;

import innova.aio.Client;

public class PlayerLogin extends Client
{
	private final static Logger _logger = Logger.getLogger("");
	
	// implement of Client
	@Override
	protected void OnConnect( final AsynchronousSocketChannel socket )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
	}

	@Override
	public void OnRead( final AsynchronousSocketChannel socket , final byte[] buffer )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
	}

	@Override
	public void OnWrite( final AsynchronousSocketChannel socket , final int size )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
	}

	@Override
	protected void OnConnectFailed( final Throwable e )
	{
		_logger.error( new Exception() );
	}

	@Override
	public void OnReadFailed( final AsynchronousSocketChannel socket , final Throwable e )
	{
		_logger.warn( new Exception().getStackTrace()[0] );
	}

	@Override
	public void OnWriteFailed( final AsynchronousSocketChannel socket , final Throwable e )
	{
		_logger.warn( new Exception().getStackTrace()[0] );
	}
}
