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
using Google.Protobuf.Collections;
using System.Collections.Generic;
using innova.common;

namespace innova
{
	public class AgentManager<OBJECT>
	{
		// thread safe variables
		private RepeatedField<Region> _watching_regions = new RepeatedField<Region>();

		private Dictionary<long , AgentUtils<OBJECT>.Agent> _agents = new Dictionary<long , AgentUtils<OBJECT>.Agent>(); // manage actors owned by others
		private AgentUtils<OBJECT>.IAgentsListener _agents_listener = null;

		// watch and unwatch region
		public void RegionWatch( RepeatedField<Region> rs )
		{
			// destroy actors out of the regions
			/* this lock is not necessary *///lock( _agents )
			{
				List<long> ids = new List<long>( _agents.Keys );
				foreach( long id in ids )
				{
					AgentUtils<OBJECT>.Agent agent = _agents[ id ];
					/* this lock is not necessary *///lock( _agents_listener ) //TODO need lock?
					{
						Region region = agent.region;
						if( null != region )
						{
							if( false == rs.Contains( region ) )
							{
								_agents_listener.ialDestroy( id , agent.obj );
								_agents.Remove( id );
							}
						}
					}
				}
			}

			/* this lock is not necessary *///lock( _watching_regions )
			{
				_watching_regions = rs;
			}
		}

		public bool RegionIsWatched( Region ri )
		{
			bool watched = false;
			/* this lock is not necessary *///lock( _watching_regions )
			{
				watched = _watching_regions.Contains( ri );
			}
			return watched;
		}

		public void AgentRemove( long id )
		{
			/* this lock is not necessary *///lock( _agents )
			{
				AgentUtils<OBJECT>.Agent agent;
				if( _agents.TryGetValue( id , out agent ) )
				{
					_agents_listener.ialDestroy( id , agent.obj );
					_agents.Remove( id );
				}
			}
		}

		//
		public void Init( AgentUtils<OBJECT>.IAgentsListener al )
		{
			_agents_listener = al;
		}

		public void Deinit()
		{
			_agents_listener = null;
		}

		public void MessageProcess( GateToPlayer msg )
		{
			ConsoleOutput.Trace( "AgentManager: MessageProcess" + " U" + msg.Updated.Count + " E" + msg.Exited.Count );
			// build actor map
			/* this lock is not necessary *///lock( _agents )
			{
				/* this lock is not necessary *///lock( _agents_listener ) //TODO need lock?
				{
					foreach( RegionActorPair rap in msg.Updated )
					{
						ConsoleOutput.Trace( "AgentManager: MessageProcess " + rap.Actor.Id + "." + rap.Actor.Ptypes.Count + " updated." );
						if( false == _agents_listener.ialIsSelf( rap.Actor.Id ) )
						{
							// find or create agent
							AgentUtils<OBJECT>.UpdateOrInsertAgent
								( rap.Region , rap.Actor.Id , rap.Actor.Ptypes , rap.Actor.Pdatas , _agents );
						}
					}

					foreach( KeyValuePair< long , AgentUtils<OBJECT>.Agent> a in _agents )
					{
						long id = a.Key;
						AgentUtils<OBJECT>.Agent agent = a.Value;

						// try to create GameObject for new agents
						if( null == agent.obj )
						{
							{
								string prop_str = "";
								foreach( KeyValuePair<int ,AgentUtils<OBJECT>.Property> kvp in agent.properties )
								{
									prop_str += kvp.Key + "," + kvp.Value.data.Length + ";";
								}
								ConsoleOutput.Trace( "Agent enter: " + id + "|" + prop_str + " in " + agent.region.Index );
							}

							agent.obj = _agents_listener.ialCreate( id , agent.properties );
						}
						// update agents
						else
						{
							if( agent.updated )
							{
								{
									string prop_str = "";
									foreach( KeyValuePair<int , AgentUtils<OBJECT>.Property> kvp in agent.properties )
									{
										prop_str += kvp.Key + "," + kvp.Value.data.Length + ";";
									}
									ConsoleOutput.Trace( "Agent update: " + id + "|" + prop_str + " in " + agent.region.Index );
								}
								agent.updated = _agents_listener.ialUpdate( agent.obj , agent.properties );
							}
						}
					}

					// delete exited agents
					foreach( RegionIdPair rip in msg.Exited )
					{
						AgentUtils<OBJECT>.Agent agent;
						if( _agents.TryGetValue( rip.Id , out agent ) )
						{
							{
								ConsoleOutput.Trace( "Agent exit: " + rip.Id + " from " + rip.Region.Index );
							}
							if( rip.Region.Equals( agent.region ) )
							// if they are different
							// means the agent has entered another region
							{
								_agents_listener.ialDestroy( rip.Id , agent.obj );
								_agents.Remove( rip.Id );
							}
						}
					}
				}
			}
		}
	}
}
