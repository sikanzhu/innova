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

using UnityEngine;
using Google.Protobuf;
using System.Collections.Generic;
using Innova.Demo;

[RequireComponent(typeof(LoadAppearance))]
public class Player : MonoBehaviour
{
	// variables
	private ActorBasic _actor_basic = null;
	private ActorScale _actor_scale = null;
	private PlayerMove _actor_move = null;

	// get and set
	public ActorBasic ABasic { get { return _actor_basic; } set { _actor_basic = value; } }
	public ActorScale AScale { get { return _actor_scale; } set { _actor_scale = value; } }
	public PlayerMove AMove { get { return _actor_move; } private set {} }

	// 
	public void SetMovement( ActorMovement amovement )
	{
		_actor_move = transform.GetComponent<PlayerMove>();
		_actor_move.SetMovement( amovement );
	}

	public Dictionary<int , ByteString> BuildProperties()
	{
		Dictionary<int , ByteString> properties = new Dictionary<int , ByteString>();
		properties.Add( ( int )PTYPE.Basic , _actor_basic.ToByteString() );
		properties.Add( ( int )PTYPE.Movement , _actor_move.Movement.ToByteString() );
		properties.Add( ( int )PTYPE.Scale , _actor_scale.ToByteString() );
		return properties;
	}

	// Awake
	public void Start()
	{
		LoadAppearance la = GetComponent<LoadAppearance>();
		la.SetAppearance( "innova/demos/appearance/AppPlayer" );
	}
}
