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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import innova.common.Constant;
import innova.common.Utility;
import innova.redis.ToRedis;
import innova.redis.ToRedisOperation;
import innova.redis.ToRedisProperty;
import innova.core.IPlugin;
import innova.protocol.InnovaProtocol.IdPtype;
import innova.protocol.InnovaProtocol.Region;
import redis.clients.jedis.Pipeline;

public class RegionCleaner implements IPlugin
{
	private final static Logger _logger = Logger.getLogger("");
	
	private ToRedis _redis;
	private Pipeline _pipline;

	private final List<Region> _regions = new ArrayList<Region>();
	
	// implement of IPlugin
	@Override
	public void Init( final String[] args )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
		
		_redis = new ToRedis();

		//TODO set up cleaning regions
		for( int x = -10 ; x < 10 ; ++x )
			for( int z = -10 ; z < 10 ; ++z )
			{
				_regions.add( Utility.ToRegion( x , 0 , z , 0 ) );
			}
	}

	@Override
	public float Start( final String[] args )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
		
		if( false == _redis.Connect( Constant.ADRESS_LOCALHOST ))
		{
			_logger.error( "Can not connect to redis." );
			return -1;
		}
		
		_pipline = _redis.Get().pipelined();
		
		return Constant.REGION_CLEANER_INTERVAL;
	}

	@Override
	public boolean Update( final float elapse )
	{
		_logger.trace( new Exception().getStackTrace()[0] + "( elapse = " + elapse + ")" );

		final long max_removing_time = _redis.GetTime() - Constant.INACTIVE_PROPERTY_REMAIN; //TODO use world server to synchronize time

		for( Region ri : _regions )
		{
			// max_removing_time must be > 0 because Redis server time is Unix timestamp,
			// so it is not necessary to check max_removing_time's sign.
			// we are sure that no active property will be in [-max_removing_time , 0],
			// because all the active properties will be in [ServerStartUnixTimeStamp , CurrentUnixTimeStamp].
			final List<IdPtype> aps = ToRedisProperty.UGetInactive( _pipline , ri , 0 , max_removing_time );
			
			// pop operations on these inactive properties
			for( IdPtype ap : aps )
			{
				ToRedisOperation.UPopOperations( _pipline , ap );
			}
			
			// remove these properties
			ToRedisProperty.PRemoveInactive( _pipline , ri , aps );
		}
		ToRedisProperty.PCommit( _pipline );
		
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
		try
		{
			_pipline.close();
		}
		catch( IOException e )
		{
			_logger.error( e );
		}
		_redis.Disconnect();
	}

	@Override
	public void Deinit( final String[] args )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
	}
}
