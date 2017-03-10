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

using System.Collections.Generic;
using Innova.Demo;
using innova.common;
using UnityEngine;
using innova;

public class TryAgent
{
	public long id = 0;
	public GameObject gobj = null;
	public AgentMove amove = null;

	public TryAgent( long pid )
	{
		id = pid;
	}
}

public class AgentManagement : MonoBehaviour, AgentUtils<TryAgent>.IAgentsListener
{
	// methods
	public GameObject CreateGameObject( string name , string appearance)
	{
		GameObject obj = ( GameObject )Instantiate( Resources.Load( Common.AGENT_PREFAB ) );
		obj.name = name;
		LoadAppearance la = obj.GetComponent<LoadAppearance>();
		la.SetAppearance( appearance );
		return obj;
	}

	// implement of IAgentsListener
	public TryAgent ialCreate( long id , Dictionary<int , AgentUtils<TryAgent>.Property> properties )
	{
		// check complete property;
		if( properties.Count < ( int )PTYPE.NumPtype )
		{
			return null;
		}

		//
		TryAgent agent = new TryAgent( id );

		// translate to properties
		AgentUtils<TryAgent>.Property pbasic = properties[ ( int )PTYPE.Basic ];
		ActorBasic abasic = ActorBasic.Parser.ParseFrom( pbasic.data );

		// create agent
		agent.gobj = CreateGameObject( abasic.Name , abasic.Appearance );

		// get movement
		agent.amove = agent.gobj.GetComponent<AgentMove>();

		// unset update
		pbasic.updated = false;

		// log
		{
			string prop_str = "";
			foreach( KeyValuePair<int , AgentUtils<TryAgent>.Property> i in properties )
			{
				prop_str += i.Key + "," + i.Value.data.Length + ";";
			}
			ConsoleOutput.Trace( "ialCreate successfully: \"" + id + "\" |" + prop_str );
		}

		return agent;
	}

	public void ialDestroy( long id , TryAgent agent )
	{
		ConsoleOutput.Trace( "ialDestroy \"" + name + "\"" );

		//destroy agent
		if( null != agent )
		{
			if( null != agent.gobj )
			{
				Destroy( agent.gobj );
			}
		}
	}

	public bool ialUpdate( TryAgent agent , Dictionary<int , AgentUtils<TryAgent>.Property> properties )
	{
		AgentUtils<TryAgent>.Property pbasic;
		if( properties.TryGetValue( ( int )PTYPE.Basic , out pbasic ) )
		{
			if( pbasic.updated )
			{
				//Basic abasic = Basic.Parser.ParseFrom( pbasic.data );
				// nothing is allowed to be updated
				ConsoleOutput.Trace( "ialUpdate: \"" + agent.id + "\" |Basic " + pbasic.data.Length );
				pbasic.updated = false;
			}
		}

		AgentUtils<TryAgent>.Property pmovement;
		if( properties.TryGetValue( ( int )PTYPE.Movement , out pmovement ) )
		{
			if( pmovement.updated )
			{
				if( null != agent.amove )
				{
					ConsoleOutput.Trace( "ialUpdate: \"" + agent.id + "\" |Movement " + pmovement.data.Length );
					agent.amove.DecodeMovement( pmovement.data );
				}
				pmovement.updated = false;
			}
		}

		AgentUtils<TryAgent>.Property pscale;
		if( properties.TryGetValue( ( int )PTYPE.Scale , out pscale ) )
		{
			if( pscale.updated )
			{
				ActorScale ascale = ActorScale.Parser.ParseFrom( pscale.data );
				agent.gobj.transform.localScale = DataBridge.cnv( ascale.Scale );
				ConsoleOutput.Trace( "ialUpdate: \"" + agent.id + "\" |Scale " + pscale.data.Length );
				pscale.updated = false;
			}
		}
		return false;
	}

	public bool ialIsSelf( long id )
	{
		return GameState.Singleton.PlayerId == id;
	}
}
