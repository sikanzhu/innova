﻿/* Copyright (c) 2017, Sikan Zhu
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

using UnityEngine;

public class GroundGenerator : MonoBehaviour
{
	public int XMin = -5;
	public int ZMin = -5;
	public int XMax = 5;
	public int ZMax = 5;

	// Use this for initialization
	void Start()
	{
		UnityEngine.Object prefab = Resources.Load("innova/demos/prefabs/Ground10X10");
		for(int x = XMin; x < XMax; ++x)
		{
			for (int z = ZMin; z < ZMax; ++z)
			{
				//GameObject ground = (GameObject)
				Instantiate(prefab
					, new Vector3(x * Common.GroundXSize + Common.GroundXOffset
					, 0 + Common.GroundYOffset
					, z * Common.GroundZSize + Common.GroundZOffset)
					, Quaternion.identity , transform);
            }
		}
	}
}
