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

import innova.common.Constant;
import innova.protocol.InnovaProtocol.IdPtype;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

public class ToRedisOperation
{
	// sync pipline
	public static void PCommit( final Pipeline pipline )
	{
		pipline.sync();
	}
	
	// Operations
	// Push operation
	public static void PPushOperation( final Pipeline pipline , final long id , final int ptype , final byte[] buffer )
	{
		final IdPtype.Builder ipb = IdPtype.newBuilder();
		ipb.setId( id ).setPtype( ptype );
		final byte[] key = ToRedisUtility.PropertyIdentityToKey( Constant.REDIS_OPERATION_PREFIX , ipb.build() );
		pipline.rpush( key , buffer );
	}
	
	// Pop operations
	public static List<byte[]> UPopOperations( final Pipeline pipline , final IdPtype id )
	{		
		// translate to key
		final byte[] key = ToRedisUtility.PropertyIdentityToKey( Constant.REDIS_OPERATION_PREFIX , id );
		
		// Get queue size
		final Response< Long > rsize = pipline.llen( key );
		pipline.sync();
		final long size = rsize.get();
		
		if( 0 == size )
		{
			return null;
		}
		
		// pop operations
		final List<Response< byte[] >> res = new ArrayList<Response< byte[] >>();
		for( int i = 0 ; i < size ; ++i )
		{
			res.add( pipline.lpop( key ) );
		}
		pipline.sync();
		
		// get operations
		final List<byte[]> ret = new ArrayList<byte[]>();
		for( Response< byte[] > o : res)
		{
			if( null != o.get() )
				ret.add( o.get() );
		}
		
		return ret;
	}
}
