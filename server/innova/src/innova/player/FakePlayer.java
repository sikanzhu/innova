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

import innova.protocol.InnovaProtocol.Region;
import innova.common.Utility;
import innova.protocol.InnovaProtocol.GateToPlayer;
import innova.protocol.InnovaProtocol.PlayerToGate.Builder;

public class FakePlayer extends PlayerBase
{	
	private Region _running_region;

	@Override
	public void InitPlayer()
	{
		_running_region = Utility.ToRegion( 0 , 0 , 0 , 0 );
		AddWatchingRegion( _running_region );
	}

	@Override
	public void DeinitPlayer()
	{
		// TODO Auto-generated method stub|
	}
	
	@Override
	public void UpdatePlayer( float elapse )
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void BuildPlayerToGate( Builder builder )
	{				
		// build message: requested properties
		//TODO setup and add property_identity_builder
	
		// build message: update player
		//TODO setup and add property_data_builder

		// build message: push operations
		//TODO setup and add operation_builder
	}

	@Override
	public void ProcessGateToPlayer( GateToPlayer message )
	{
		System.out.printf( "%f\n" , message.getWorldTime() );
		
		//TODO process updates
		//TODO process properties
		//TODO process operations
	}
}
