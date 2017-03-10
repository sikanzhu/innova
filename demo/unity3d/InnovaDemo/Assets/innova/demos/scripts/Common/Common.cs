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

using System;
using Innova.Protocol;
using UnityEngine;

public class Common
{
	public static Quaternion Mul( Quaternion q , float s )
	{
		Vector3 axis;
		float angle;
		q.ToAngleAxis(out angle, out axis);
		return Quaternion.AngleAxis(angle * s, axis);
	}

	public static Region ToRegion( int x , int y , int z , int w )
	{
		Region ret = new Region();
		ret.Index = "(" + x + "," + y + "," + z + "," + w + ")";
		return ret;
    }

	public static Region ToRegion( Vector3 position , int w )
	{
		return ToRegion
				( Mathf.FloorToInt( position.x / Common.GroundXSize )
				, Mathf.FloorToInt( position.y / Common.GroundYSize )
				, Mathf.FloorToInt( position.z / Common.GroundZSize )
				, w );
	}

	public static void FromRegion( Region region , out int x , out int y , out int z , out int w )
	{
		char[] delimiter_chars = { '(' , ',' , ')' };
		string[] numbers = region.Index.Split( delimiter_chars );
		x = Int32.Parse( numbers[ 1 ] );
		y = Int32.Parse( numbers[ 2 ] );
		z = Int32.Parse( numbers[ 3 ] );
		w = Int32.Parse( numbers[ 4 ] );
	}

	public const float GroundXOffset = 5;
	public const float GroundYOffset = 0;
	public const float GroundZOffset = 5;

	public const float GroundXSize = 10;
	public const float GroundYSize = 10;
	public const float GroundZSize = 10;

	public const int REGION_WATCH_MINX = -1;
	public const int REGION_WATCH_MAXX = 2;
	public const int REGION_WATCH_MINZ = -1;
	public const int REGION_WATCH_MAXZ = 2;

	public const int TARGET_FRAME_RATE = 60;

	public const string AGENT_PREFAB = "innova/demos/prefabs/PreAgent";

	// My Id
	public static long MY_ID_BASE = 2000;
}
