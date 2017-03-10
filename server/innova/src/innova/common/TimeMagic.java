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

// description Version 3
// This is a class to generate proper time stamp,
// so that the Gates will be free to synchronize between each other.
// The Gates don't have to use distributed lock to synchronize the data.
//
// explain
// Ideally, the time line is continuous, 
//         .C r   .C r       .C r     .C r          .C r           .C r
// [--------[------[----------[--------[-------------[--------------[--------------------
//    ^B w    ^B w       ^B w    ^B w        ^B w ^B w       ^B w
// B writes randomly at s and C reads randomly in [S,+infinity).
// Every time B write will need a time period.
// For example, when B want to write, it will read the time stamp s,
// then it writes at true time s+t1,
// so B is not writing the true time.
// We don't know what exactly the t1 is. 
// And also every time C reads a time span from the database, 
// it does not read the true time span,
// there will be some delay.
// To overcome the problem, we have to let C reads with some overlay.
//         .C r   .C r       .C r     .C r          .C r           .C r
// [--[=====-[=====-----[=====---[=====--------[=====---------[=====---------------
//    ^B w1^    ^B w1^    ^B w2^    ^B w2^    ^B w3^    ^B w3^    ^B w4^
//    <-t1->
// Every time B will write with a delayed time stamp s+t1.
// While C will read a time span [S-t1,+infinity).
// As we can see, the time marked with "=" is read twice by C,
// and any update will be read, there will be no miss.
// Though some update may be read twice.
// t1 may change due to different message length and CPU performance and load,
// but generally, we can use a MAXIMUN t1( named T1 ) to ensure there will be no update lost.
// 
// It that enough? The reality is NO. 
// Because there will be some delay when synchronizing the clock.
// So we have to introduce another variable t2, 
// that means the latency when synchronizing clocks.
// And when C reads, it will read a time span [S-t1-t2,+infinity).

public class TimeMagic
{
	// variables
	private Long _remote_time = ( long ) 0;
	private long _local_time = System.currentTimeMillis();

	public void SynchronizeTime( final long time )
	{
		synchronized( _remote_time )
		{
			_remote_time = time;
			_local_time = System.currentTimeMillis();
		}
	}
	
	public long GetTime()
	{
		synchronized( _remote_time )
		{
			final long cur_time = System.currentTimeMillis();
			return _remote_time + ( cur_time - _local_time );
		}
	}
}

// deprecated
//description Version 2
//This is a class to generate proper time stamp,
//so that the Gates will be free to synchronize between each other.
//The Gates don't have to use distributed lock to synchronize the data.
//
//explain
//Ideally, the time line is continuous, 
//    .C r   .C r       .C r  .C r          .C r           .C r
//--------------------------------------------------------------------------------
//^B w    ^B w       ^B w    ^B w        ^B w ^B w       ^B w
//B writes randomly and C reads randomly.
//Every time B write will need a time period.
//For example, when B want to write, it will read the time stamp s,
//then it writes at true time s+t,
//so B is not writing the true time.
//We don't know what exactly the t is. 
//And also every time C reads a time span from the database, 
//it does not read the true time span,
//there will be some delay.
//To overcome the problem, we have to introduce a clock into the system.
//      .C r12    .C r12    .C r23    .C r23    .C r34    .C r34
//1-------------------2-------------------3-------------------4-------------------
//  ^B w1     ^B w1     ^B w2     ^B w2     ^B w3     ^B w3     ^B w4
//<---------T-------->|
//T is the interval of the clock.
//Every time B will write with a delayed time stamp S+t.
//While C will read a time span [S,S+T].
//
//This looks good when C always reads after B writes.
//What if C always reads before B writes?
//The time line should be like this:
//.C r12    .C r12    .C r23    .C r23    .C r34    .C r34
//1-------------------2-------------------3-------------------4-------------------
//   ^B w1     ^B w1     ^B w2     ^B w2     ^B w3     ^B w3     ^B w4
//As we can see, every last update of B in an time span will be lost.
//So we extend the reading time span of C to [S-T,S+T]
//Then the time line should be like this:
//.C r02    .C r02    .C r13    .C r13    .C r24    .C r24
//1-------------------2-------------------3-------------------4-------------------
//    ^B w1     ^B w1     ^B w2     ^B w2     ^B w3     ^B w3     ^B w4
//
//It that enough? The reality is NO. B does not writes at a time point,
//it will need a time period (we name it "w") to finish writing.
//then we get a time line like this:
//.C r02    .C r02    .C r13    .C r13    .C r24    .C r24
//1-------------------2-------------------3-------------------4-------------------
//    ^B w1^    ^B w1^    ^B w2^    ^B w2^    ^B w3^    ^B w3^    ^B w4^
//    <--w->
//As we can see, the B ends writing after a tick comes,
//then anything, which is written to database with time stamp S+t,
//will be actually in the next time span.
//But this is OK, because C will read a larger time span [S-T,S+T].
//
//Implementation
//In the implementation, we will use World Server to tick the Gate Servers.
//The World Server will synchronize all the Gate Servers.
//Due to performance and network latency,
//the Gate Servers can not be accurately synchronized.
//However this is also overcome by reading 2 time spans.


