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

using Innova.Protocol;
using Google.Protobuf;
using System.Collections.Generic;
using Google.Protobuf.Collections;

namespace innova
{
	public class AgentUtils<OBJECT>
	{
		// structures
		// an agent of actor in local memory
		public class Property
		{
			public ByteString data = null;
			public bool updated = false;
		}

		public class Agent
		{
			public OBJECT obj;
			public Region region;
			public Dictionary<int , Property> properties = new Dictionary<int , Property>();
			public bool updated = false;
		}

		// interfaces
		public interface IAgentsListener
		{
			bool ialIsSelf( long id );

			OBJECT ialCreate( long id , Dictionary<int , Property> properties );

			void ialDestroy( long id , OBJECT obj );

			// update agent and mark property "update = false".
			// return true if need to be updated next frame.
			bool ialUpdate( OBJECT obj , Dictionary<int , Property> properties );
		}

		public static void UpdateOrInsertProperty( int ptype , ByteString data , Dictionary<int , Property> dest )
		{
			Property prop;
			if( dest.TryGetValue( ptype , out prop ) )
			{
				prop.data = data;
				prop.updated = true;
			}
			else
			{
				prop = new Property();

				prop.data = data;
				prop.updated = true;

				dest.Add( ptype , prop );
			}
		}

		public static void MergeProperties( RepeatedField<int> ptypes , RepeatedField<ByteString> pdatas , Dictionary<int , Property> dest )
		{
			for( int i = 0 ; i < ptypes.Count ; ++i )
			{
				int ptype = ptypes[ i ];
				ByteString pdata = pdatas[ i ];
				UpdateOrInsertProperty( ptype , pdata , dest );
			}
		}
		
		public static void UpdateOrInsertAgent(Region region, long id , RepeatedField<int> ptypes , RepeatedField<ByteString> pdatas , Dictionary<long , AgentUtils<OBJECT>.Agent> dest )
		{
			AgentUtils<OBJECT>.Agent agent;
			if( dest.TryGetValue( id , out agent ) )
			{
				agent.region = region;
				MergeProperties( ptypes , pdatas , agent.properties );
                agent.updated = true;
			}
			else
			{
				agent = new Agent();
				agent.region = region;
				MergeProperties( ptypes , pdatas , agent.properties );
				agent.updated = true;
				dest.Add( id , agent );
			}
		}
	}
}