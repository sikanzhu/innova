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

package innova.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import innova.common.Constant;
import innova.protocol.InnovaProtocol.IdPtype;
import innova.protocol.InnovaProtocol.Region;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

public class ToRedisProperty
{
	// sync pipeline
	public static void PCommit( final Pipeline pipeline )
	{
		pipeline.sync();
	}

	private static List< IdPtype > GetByTimeRange( final Pipeline pipeline , final byte[] key , final long time_start , final long time_end )
	{
		final Response< Set< byte[] > > res = pipeline.zrangeByScore( key , time_start , time_end );
		pipeline.sync();

		final List< IdPtype > ret = new ArrayList< IdPtype >();
		for( byte[] k : res.get() )
		{
			final IdPtype com = ToRedisUtility.ByteToPropertyIdentity( k );
			if( null != com ) ret.add( com );
		}
		return ret;
	}

	// Active and inactive management
	// Get active properties in region
	public static List< IdPtype > UGetActive( final Pipeline pipeline , final Region region , final long time_start , final long time_end )
	{
		return GetByTimeRange( pipeline , ToRedisUtility.RegionToKey( Constant.REDIS_REGION_PREFIX , region ) , time_start , time_end );
	}

	// Get inactive properties in region
	public static List< IdPtype > UGetInactive( final Pipeline pipeline , final Region region , final long time_start , final long time_end )
	{
		return GetByTimeRange( pipeline , ToRedisUtility.RegionToKey( Constant.REDIS_REGION_PREFIX , region ) , -time_end , -time_start );
	}

	// Set or Add active properties update time in region
	public static void PSetActive( final Pipeline pipeline , final Region region , final IdPtype ap , final long time )
	{
		final byte[] key = ToRedisUtility.RegionToKey( Constant.REDIS_REGION_PREFIX , region );
		pipeline.zadd( key , time , ToRedisUtility.PropertyIdentityToByte( ap ) );
	}

	// Deactivate properties in region
	public static void PDeactivate( final Pipeline pipeline , final Region region , final IdPtype property , final long time )
	{
		final byte[] key = ToRedisUtility.RegionToKey( Constant.REDIS_REGION_PREFIX , region );
		pipeline.zadd( key , -time , ToRedisUtility.PropertyIdentityToByte( property ) );
	}

	// Remove inactive properties in region
	public static void PRemoveInactive( final Pipeline pipeline , final Region region , final List< IdPtype > ids )
	{
		final byte[] key = ToRedisUtility.RegionToKey( Constant.REDIS_REGION_PREFIX , region );
		for( IdPtype i : ids )
		{
			pipeline.zrem( key , ToRedisUtility.PropertyIdentityToByte( i ) );
		}
	}

	// Remove inactive properties in region
	public static void PRemoveInactiveByTime( final Pipeline pipeline , final Region region , final long time_start , final long time_end )
	{
		final byte[] key = ToRedisUtility.RegionToKey( Constant.REDIS_REGION_PREFIX , region );
		pipeline.zremrangeByScore( key , -time_end , -time_start );
	}

	// Properties
	// Set or add properties
	public static void PSetProperty( final Pipeline pipeline , final IdPtype ap , final byte[] buffer )
	{
		pipeline.hset( Constant.REDIS_PROPERTY_KEY , ToRedisUtility.PropertyIdentityToByte( ap ) , buffer );
	}

	// Get properties
	public static List< byte[] > UGetProperties( final Pipeline pipeline , final List< IdPtype > ids )
	{
		final List< Response< byte[] > > res = new ArrayList< Response< byte[] > >();
		for( IdPtype i : ids )
		{
			res.add( pipeline.hget( Constant.REDIS_PROPERTY_KEY , ToRedisUtility.PropertyIdentityToByte( i ) ) );
		}
		pipeline.sync();

		final List< byte[] > ret = new ArrayList< byte[] >();
		for( Response< byte[] > i : res )
		{
			ret.add( i.get() );
		}
		return ret;
	}

	// remvoe properties
	public static void PRemoveProperty( final Pipeline pipeline , final IdPtype property )
	{
		pipeline.hdel( Constant.REDIS_PROPERTY_KEY , ToRedisUtility.PropertyIdentityToByte( property ) );
	}
}
