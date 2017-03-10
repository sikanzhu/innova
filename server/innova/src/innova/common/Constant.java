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

package innova.common;

public class Constant
{
	public static final int MAX_PACKAGE = 1024; // 1KB
	public static final int MAX_WRITE = 1048576; // 1MB
	public static final int MAX_READ = 1048576; // 1MB
	public static final int WRITE_TIMEOUT = 100;
	
	public static final int WORLD_GATE_PORT = 1264;
	public static final int WORLD_LOGIN_PORT = 1265;
	public static final int WORLD_SERVE_PORT = 1266;
	public static final int SERVE_GATE_PORT = 1267;
	public static final int GATE_PLAYER_PORT = 1268;
	public static final int LOGIN_PLAYER_PORT = 1269;

	public static final String ADDRESS_ANY = "0.0.0.0";
	public static final String ADRESS_LOCALHOST = "127.0.0.1";

	public static final double UNWATCHED_PROPERTY_REMAIN = 10;
	public static final double UNWATCHED_PROPERTY_INTERVAL = 1;
	public static final double UNWATCHED_PROPERTY_POP = 1;
	
	public static final int PLAYER_TO_GATE_MAGIC_NUMBER = 0x12345678;
	
	// the GATE_UPDATE_INTERVAL split entire continuous time into time fragments.
	// the smaller time fragment, the less actor update(including enter, exit) in regions will be sent to players.
	// then the network will be more efficient.
	// but the smallest time fragment should be larger than the time elapse to process a PlayerToGate Message.
	public static final float GATE_UPDATE_INTERVAL = -1;
	public static final float WORLD_UPDATE_INTERVAL = 5;
	public static final long TIME_MAGIC_LATENCY = ( long ) ( 100 ); // t1+t2 in Time Magic
	
	public static final float REGION_CLEANER_INTERVAL = 10;
	public static final long INACTIVE_PROPERTY_REMAIN = 10000; // in milliseconds
	
	// Redis Constants
	public static final byte[] REDIS_REGION_PREFIX = { 0x01 , 0x01 };
	public static final byte[] REDIS_PROPERTY_KEY = { 0x01 , 0x02 };
	public static final byte[] REDIS_OPERATION_PREFIX = { 0x01 , 0x03 };
	public static final byte[] REDIS_CONFIG_KEY = { 0x01 , 0x04 };
	public static final int REDIS_DEFAULT_PORT = 6379;
	
	//
	public static final String DEFAULT_DATABASE_FILE = "../../resource/database/demo.db";
	public static final String DEFAULT_SERVICE_FILE = "../../resource/config/services.xml";
}
