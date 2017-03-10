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
using System.Collections.Generic;
using Google.Protobuf;
using Google.Protobuf.Collections;

namespace innova
{
	public class ActorUtils
	{
		public interface IActorsListener
		{
			void ialOperate( long src , long dest , int ptype , ByteString data );
			void ialResponseProperties( long id , Dictionary<int , ByteString> properties );
			void ialResponseConfigs( long id , Dictionary<int , ByteString> configs );
		}

		public static Actor FindActor( long id , RepeatedField<Actor> actors )
		{
			foreach( Actor actor in actors )
			{
				if( actor.Id == id )
				{
					return actor;
				}
			}
			return null;
		}

		public static void CopyProperties( Dictionary<int , ByteString> src , RepeatedField<int> pids , RepeatedField<ByteString> pdatas )
		{
			foreach( KeyValuePair<int , ByteString> property in src )
			{
				int pid_index = pids.IndexOf( property.Key );
				if( pid_index < 0 )
				{
					pids.Add( property.Key );
					pdatas.Add( property.Value );
				}
				else
				{
					pdatas[ pid_index ] = property.Value;
				}
			}
		}

		public static Actor CreateActor( long id , Dictionary<int , ByteString> properties )
		{
			Actor na = new Actor();
			na.Id = id;
			if( null != properties )
			{
				CopyProperties( properties , na.Ptypes , na.Pdatas );
			}
			return na;
		}

		public static void InsertIdTypeData
			( long id , int type , ByteString data , Dictionary<long , Dictionary<int , ByteString>> dest )
		{
			Dictionary<int , ByteString> dib;
			if( false == dest.TryGetValue( id , out dib ) )
			{
				dib = new Dictionary<int , ByteString>();
				dib.Add( type , data );
				dest.Add( id , dib );
			}
			else
			{
				dib.Add( type , data );
			}
		}

		public class RegionActorPair
		{
			public Region src = null;
			public Region dest = null;
			public Actor actor = null;
		}
	}
}
