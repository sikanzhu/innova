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

using Innova.Common;
using UnityEngine;

namespace innova.common
{
	public class DataBridge
	{
		public static Vector3 cnv( Float3 v )
		{
			return new Vector3( v.X , v.Y , v.Z );
		}

		public static Vector4 cnv(Float4 v)
		{
			return new Vector4(v.X, v.Y, v.Z , v.W);
		}

		public static Float3 cnv( Vector3 v )
		{
			Float3 ret = new Float3();
			ret.X = v.x; ret.Y = v.y; ret.Z = v.z;
			return ret;
		}

		public static Float4 cnv(Vector4 v)
		{
			Float4 ret = new Float4();
			ret.X = v.x; ret.Y = v.y; ret.Z = v.z; ret.W = v.w;
			return ret;
		}

		public static UnityEngine.Quaternion cnv( Innova.Common.Quaternion q )
		{
			return new UnityEngine.Quaternion( q.I , q.J , q.K , q.S );
		}

		public static Innova.Common.Quaternion cnv( UnityEngine.Quaternion q )
		{
			Innova.Common.Quaternion ret = new Innova.Common.Quaternion();
			ret.I = q.x; ret.J = q.y; ret.K = q.z; ret.S = q.w;
			return ret;
		}
	}
}
