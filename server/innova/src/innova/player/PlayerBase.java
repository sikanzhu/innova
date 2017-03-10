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

package innova.player;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import innova.client.PlayerGate;
import innova.client.PlayerGate.IListener;
import innova.common.Constant;
import innova.core.IPlugin;
import innova.protocol.InnovaProtocol.GateToPlayer;
import innova.protocol.InnovaProtocol.PlayerToGate;
import innova.protocol.InnovaProtocol.PlayerToGate.ETYPE;
import innova.protocol.InnovaProtocol.Region;

public abstract class PlayerBase implements IPlugin , IListener
{
	private final static Logger _logger = Logger.getLogger("");
	
	private PlayerGate _gate;
	private List<Region> _regions = new ArrayList<Region>();
	private boolean _first_update = true;
	
	// call child class
	public abstract void InitPlayer();
	public abstract void DeinitPlayer();
	public abstract void UpdatePlayer( final float elapse );
	public abstract void BuildPlayerToGate( final PlayerToGate.Builder builder );
	public abstract void ProcessGateToPlayer( final GateToPlayer message );
	
	public void AddWatchingRegion( final Region r )
	{
		_regions.add( r );
	}

	// methods of IPlugin
	@Override
	public void Init( final String[] args )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
		_gate = new PlayerGate( this );
		InitPlayer();
	}

	@Override
	public float Start( final String[] args )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
		if( false == _gate.Connect
				( Constant.ADRESS_LOCALHOST , Constant.GATE_PLAYER_PORT
						, Constant.MAX_READ
						, Constant.WRITE_TIMEOUT
						, Constant.MAX_WRITE
						, Constant.MAX_PACKAGE ) )
		{
			_logger.error( "Can not connect." );
			return -1;
		}
		return 1;
	}

	@Override
	public boolean Update( final float elapse )
	{
		// log
		_logger.trace( new Exception().getStackTrace()[0] + "( elapse = " + elapse + ")" );
	
		// update
		UpdatePlayer( elapse );
		
		// create
		final PlayerToGate.Builder p2gbuilder = PlayerToGate.newBuilder();
		
		// build message: type
		p2gbuilder.setType( ETYPE.MESSAGE );
		p2gbuilder.setMagicNumber( Constant.PLAYER_TO_GATE_MAGIC_NUMBER );
		
		// build message: requested region
		if( _first_update )
		{
			p2gbuilder.addAllWatchingRegions( _regions );

			_first_update = false;
		}

		// build message
		BuildPlayerToGate( p2gbuilder );
	
		// write to gate
		final PlayerToGate p2g = p2gbuilder.build();
		
		//buffer.flip(); // unnecessary because ByteBuffer.wrap will do.
		_gate.Write( p2g.toByteArray() );
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
		_gate.Disconnect();
	}

	@Override
	public void Deinit( final String[] args )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
		DeinitPlayer();
	}

	// methods of IListener
	@Override
	public void ProcessMessage( final GateToPlayer msg )
	{
		ProcessGateToPlayer( msg );
	}
}
