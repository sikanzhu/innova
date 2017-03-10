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

import java.util.List;

import innova.common.Constant;
import redis.clients.jedis.Jedis;

public class ToRedis
{
	Jedis _jedis;
	
	// 
	public Jedis Get() { return _jedis; }

	// Connection
	public boolean Connect( final String address )
	{
		return Connect( address , Constant.REDIS_DEFAULT_PORT );
	}
	
	public boolean Connect( final String address , final int port )
	{
		_jedis = new Jedis( address , port );
		if( null == _jedis ) return false;
		return true;
	}

	public void Disconnect()
	{
		if( null != _jedis )
		{
			_jedis.close();
		}
	}
	
	// Time
	// in milliseconds
	public long GetTime()
	{
		final List<String> ts;
		ts = _jedis.time();
		final long second = Long.parseLong( ts.get( 0 ) );
		final long microsecond = Long.parseLong( ts.get( 1 ) );
		final long ret = second * 1000 + microsecond / 1000;
		return ret;
	}
}
