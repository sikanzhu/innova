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

package innova;

import java.nio.channels.AsynchronousSocketChannel;

import org.apache.log4j.Logger;

import innova.common.Constant;
import innova.server.WorldGate;
import innova.server.WorldLogin;
import innova.server.WorldServe;
import innova.core.IPlugin;
import innova.protocol.InnovaProtocol.WorldWrite;

public class World implements IPlugin
{
	private final static Logger _logger = Logger.getLogger("");
	
	WorldLogin _login;
	WGate _gate;
	WServe _serve;
	
	private class WGate extends WorldGate
	{
		@Override
		public void OnAcceptGate( AsynchronousSocketChannel socket )
		{
		}
	}
	
	private class WServe extends WorldServe
	{
		
	}

	@Override
	public void Init( final String[] args )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
		_gate = new WGate();
		_login = new WorldLogin();
		_serve = new WServe();
	}

	@Override
	public float Start( final String[] args )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
		if( false == _login.Accept
				( Constant.ADDRESS_ANY , Constant.WORLD_LOGIN_PORT
						, Constant.MAX_READ
						, Constant.WRITE_TIMEOUT
						, Constant.MAX_WRITE
						, Constant.MAX_PACKAGE ) )
			return -1;
		if( false == _gate.Accept
				( Constant.ADDRESS_ANY , Constant.WORLD_GATE_PORT
						, Constant.MAX_READ
						, Constant.WRITE_TIMEOUT
						, Constant.MAX_WRITE
						, Constant.MAX_PACKAGE ) )
			return -1;
		if( false == _serve.Accept
				( Constant.ADDRESS_ANY , Constant.WORLD_SERVE_PORT
						, Constant.MAX_READ
						, Constant.WRITE_TIMEOUT
						, Constant.MAX_WRITE
						, Constant.MAX_PACKAGE ) )
			return -1;
		return Constant.WORLD_UPDATE_INTERVAL;
	}

	@Override
	public boolean Update( final float elapse )
	{
		_logger.trace( new Exception().getStackTrace()[0] + "( elapse = " + elapse + ")" );
		
		final WorldWrite.Builder ww = WorldWrite.newBuilder();
		ww.setType( WorldWrite.ETYPE.PING );
		ww.setWorldTime( System.currentTimeMillis() );
		
		_gate.Broadcast( ww.build().toByteArray() );
		
		return true;
	}

	@Override
	public void Pause()
	{
		_logger.trace( new Exception().getStackTrace()[0] );
	}

	@Override
	public void Resume()
	{
		_logger.trace( new Exception().getStackTrace()[0] );
	}

	@Override
	public void Stop( final String[] args )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
	}

	@Override
	public void Deinit( final String[] args )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
	}
}
