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

using innova.connect.client;
using innova.common;
using Innova.Protocol;
using Google.Protobuf;
using Google.Protobuf.Collections;
using System.Collections.Generic;

namespace innova
{
	public class ActorManager
	{
		// thread safe variables
		private PlayerToGate _message = new PlayerToGate();
		private Dictionary<long , ActorUtils.RegionActorPair> _region_actor = new Dictionary<long , ActorUtils.RegionActorPair>();

		// single thread variables
		private ActorUtils.IActorsListener _actors_listener = null;

		// watch and unwatch region
		public void RegionWatch( RepeatedField<Region> rs )
		{
			ConsoleOutput.Trace( "ActorManager: RegionWatch " + rs.Count );

			// copy to message
			/* this lock is not necessary *///lock( _message )
			{
				_message.WatchingRegions.AddRange( rs );
			}
		}

		// own actor. 
		// update the actor
		// and watch operations on the actor
		private void ActorEnter( Region region , long id , Dictionary<int , ByteString> properties )
		{
			/* this lock is not necessary *///lock( _region_actor )
			{
				// find actor
				// if actor is found, update its properties and replace the dest region.
				// because before sending the message, the same actor may change region several times,
				// the latest dest region will be the true that the actor truely enters.
				ActorUtils.RegionActorPair rap = null;
				if( _region_actor.TryGetValue( id , out rap ) )
				{
					// copy properties
					ActorUtils.CopyProperties( properties , rap.actor.Ptypes , rap.actor.Pdatas );

					// set new dest
					// replace the region was set
					rap.dest = region;
				}
				// else insert the actor.
				else
				{
					rap = new ActorUtils.RegionActorPair();
					rap.actor = ActorUtils.CreateActor( id , properties );
					rap.dest = region;

					_region_actor.Add( id , rap );
				}
			}
		}

		// actor exit
		private void ActorExit( Region region , long id )
		{
			/* this lock is not necessary *///lock( _region_actor )
			{
				// find actor
				// if the actor is found, only set null src region.
				// because before sending the message, the same actor may change region several times,
				// the first src regin should be the truely region that the actor exits from.
				ActorUtils.RegionActorPair rap = null;
				if( _region_actor.TryGetValue( id , out rap ) )
				{
					// set old region
					if( null == rap.src )
					{
						rap.src = region;
					}
				}
				else
				{
					rap = new ActorUtils.RegionActorPair();
					rap.actor = ActorUtils.CreateActor( id , null );
					rap.src = region;

					_region_actor.Add( id , rap );
				}
			}
		}

		// update properties
		public void ActorUpdate( Region region , long id , Dictionary<int , ByteString> properties )
		{
			/* this lock is not necessary *///lock( _message )
			{
				Actor a = ActorUtils.FindActor( id , _message.UpdateActors );
				if( null == a )
				{
					a = ActorUtils.CreateActor( id , properties );
					_message.UpdateActors.Add( a );
				}
				else
				{
					ActorUtils.CopyProperties( properties , a.Ptypes , a.Pdatas );
				}
			}
		}

		// requests
		public void RequestProperty( long id , int ptype )
		{
			/* this lock is not necessary *///lock( _message )
			{
				IdPtype ip = new IdPtype();
				ip.Id = id;
				ip.Ptype = ptype;
				if( false == _message.RequestProperty.Contains( ip ) )
				{
					_message.RequestProperty.Add( ip );
				}
			}
		}

		public void RequestConfig( long id , int ctype )
		{
			/* this lock is not necessary *///lock( _message )
			{
				IdCtype ic = new IdCtype();
				ic.Id = id;
				ic.Ctype = ctype;
				if( false == _message.RequestConfig.Contains( ic ) )
				{
					_message.RequestConfig.Add( ic );
				}
			}
		}

		// set actor's region
		public void ActorSetRegion( long id , Region src , Region dest , Dictionary<int , ByteString> properties )
		{
			if( null != src ) ActorExit( src , id );
			if( null != dest ) ActorEnter( dest , id , properties );
		}

		// push operations that affect other properties
		public void OperationPush( long src , long dest , int ptype , ByteString data )
		{
			/* this lock is not necessary *///lock( _message )
			{
				Operation op = new Operation();
				op.Src = src;
				op.Dest = dest;
				op.Ptype = ptype;
				op.Data = data;

				_message.PushOperations.Add( op );
			}
		}

		//
		public void Init( ActorUtils.IActorsListener pl )
		{
			_actors_listener = pl;
		}

		public void Deinit()
		{
			_actors_listener = null;
		}

		public void MessageSend( PlayerGate gate )
		{
			/* this lock is not necessary *///lock( _message )
			{

				// build message: type
				_message.Type = PlayerToGate.Types.ETYPE.Message;

				// magic number
				_message.MagicNumber = Constant.PLAYER_TO_GATE_MAGIC_NUMBER;

				// entering and exiting region
				lock( _region_actor )
				{
					foreach( KeyValuePair<long , ActorUtils.RegionActorPair> kvp in _region_actor )
					{
						ActorUtils.RegionActorPair rap = kvp.Value;

						// if exit and enter target are the same, skip this actor
						if( null != rap.src && null != rap.dest )
						{
							if( rap.src.Equals( rap.dest ) )
							{
								continue;
							}
						}

						// exiting
						if( null != rap.src )
						{
							IdList ail = CollectionUtils.FindOrInsert( _message.ExitedRegions , rap.src , _message.ExitedActors , new IdList() );
							ail.Ids.Add( rap.actor.Id );

							ConsoleOutput.Trace( "Actor exits: " + rap.src.Index );
						}

						// entering
						if( null != rap.dest )
						{
							ActorList al = CollectionUtils.FindOrInsert( _message.EnteredRegions , rap.dest , _message.EnteredActors , new ActorList() );
							al.Actors.Add( rap.actor );

							ConsoleOutput.Trace( "Actor enters: " + rap.dest.Index );
						}
					}

					// clear buffer
					_region_actor.Clear();
				}

				// write message
				byte[] buffer = new byte[ _message.CalculateSize() ];
				CodedOutputStream os = new CodedOutputStream( buffer );
				_message.WriteTo( os );

				gate.Write( buffer );

				// new message
				_message = new PlayerToGate();

				// log
				ConsoleOutput.Trace( "SendMessage " + buffer.Length );
			}
		}

		public void MessageProcess( GateToPlayer msg )
		{
			ConsoleOutput.Trace( "ActorManager: MessageProcess" + " P" + msg.ResponseProperty.Count + " C" + msg.ResponseConfig.Count );
			// process operations
			foreach( Operation operation in msg.Operations )
			{
				_actors_listener.ialOperate( operation.Src , operation.Dest , operation.Ptype , operation.Data );
            }

			// process responses of property
			{
				Dictionary<long , Dictionary<int , ByteString>> properties
					= new Dictionary<long , Dictionary<int , ByteString>>();
				foreach( IdPtypeData property in msg.ResponseProperty )
				{
					ActorUtils.InsertIdTypeData
						( property.Id , property.Ptype , property.Data , properties );
				}

				/* this lock is not necessary *///lock( _actors_listener )
				{
					foreach( KeyValuePair<long , Dictionary<int , ByteString>> i in properties )
					{
						_actors_listener.ialResponseProperties( i.Key , i.Value );
					}
				}
			}

			// process responses of config
			{
				Dictionary<long , Dictionary<int , ByteString>> configs
					= new Dictionary<long , Dictionary<int , ByteString>>();
				foreach( IdCtypeData config in msg.ResponseConfig )
				{
					ActorUtils.InsertIdTypeData
						( config.Id , config.Ctype , config.Data , configs );
				}

				/* this lock is not necessary *///lock( _actors_listener )
				{
					foreach( KeyValuePair<long , Dictionary<int , ByteString>> i in configs )
					{
						_actors_listener.ialResponseConfigs( i.Key , i.Value );
					}
				}
			}
		}
	}
}