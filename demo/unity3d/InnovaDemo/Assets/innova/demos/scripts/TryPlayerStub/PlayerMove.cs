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

using innova.common;
using Innova.Protocol;
using UnityEngine;
using Innova.Demo;
using innova;

[RequireComponent( typeof( CharacterController ) )]
public class PlayerMove : MonoBehaviour
{
	// 
	public float MaxAngularSpeed = 180.0f;
	public float MaxLinearSpeed = 5.0f;

	public CameraController CameraController;
	private CharacterController _character_controller;

	// movement
	private Vector3 _falling = Vector3.zero;
	private Vector4 _angular = Vector4.zero;
	private ActorMovement _movement = new ActorMovement();
	private bool _moved = false;

	// status
	private Region _current_region = new Region();

	// get set
	public Region CurrentRegion { get { return _current_region; } private set { _current_region = value; } }
	public ActorMovement Movement { get { return _movement; } private set {  } }
	public bool IsMoved { get { bool ret = _moved; _moved = false; return ret; } private set { } }

	// movement
	public ActorMovement GetMovement()
	{
		ActorMovement mov = new ActorMovement();
		return mov;
	}

	public void SetMovement( ActorMovement amovement )
	{
		_falling = Vector3.zero;
		_angular = Vector4.zero;

		transform.position = DataBridge.cnv( amovement.Location );
		transform.rotation = DataBridge.cnv( amovement.Rotation );

		_current_region = Common.ToRegion( transform.position , 0 );
	}

	// Use this for initialization
	void Awake()
	{
		_character_controller = GetComponent<CharacterController>();
	}

	// Update is called once per frame
	void Update()
	{
		float delta_time = Time.deltaTime;

		// update region
		{
			_current_region = Common.ToRegion( transform.position , 0 );
		}

		//if( null != _camera_controller)
		{
			// zero movement
			Vector3 movement = Vector3.zero;

			// get input
			float hinput = Input.GetAxis( "Horizontal" );
			float vinput = Input.GetAxis( "Vertical" );

			if( 0 != hinput || 0 != vinput )
			{
				// get yaw
				float yaw = CameraController.Yaw;
				UnityEngine.Quaternion ryaw = UnityEngine.Quaternion.Euler( 0 , yaw , 0 );

				// get forward and rightward
				Vector3 forward = ryaw * Vector3.forward;
				Vector3 right = ryaw * Vector3.right;
				Vector3 target_move = hinput * right + vinput * forward;

				// move
				{
					movement += target_move * MaxLinearSpeed * Time.deltaTime;
				}

				// rotation
				{
					UnityEngine.Quaternion target_dir = UnityEngine.Quaternion.LookRotation( target_move );
					UnityEngine.Quaternion current_dir = transform.rotation;

					// To Angle Axis
					float delta_angle;
					Vector3 delta_axis;
					InnovaMath.ToAngleAxis( current_dir , target_dir , out delta_angle , out delta_axis );

					float need_time = Mathf.Abs( delta_angle ) / MaxAngularSpeed;

					transform.rotation = UnityEngine.Quaternion.Slerp( transform.rotation , target_dir , Time.deltaTime / need_time );
					_angular = new Vector4( delta_axis.x , delta_axis.y , delta_axis.z , delta_angle > 0 ? MaxAngularSpeed : delta_angle < 0 ? -MaxAngularSpeed : 0 );
				}
			}
			else
			{
				_angular = Vector4.zero;
			}

			{
				// gravity
				if( false == _character_controller.isGrounded )
				{
					_falling += Physics.gravity * Time.deltaTime;
					movement += _falling * Time.deltaTime;
				}
				else
				{
					_falling = Vector3.zero;
				}

				// move
				_character_controller.Move( movement );
			}

			// let camera follow
			{
				CameraController.LookAtPosition = transform.position;
			}

			// update movement
			{
				ActorMovement new_movement = new ActorMovement();
				new_movement.Location = DataBridge.cnv( transform.localPosition );
				new_movement.Rotation = DataBridge.cnv( transform.localRotation );
				new_movement.Velocity = DataBridge.cnv( _character_controller.velocity );
				new_movement.Angular = DataBridge.cnv( _angular );

				if( false == new_movement.Equals( _movement ) )
				{
					_movement = new_movement;
					_moved = true;
				}
				else
				{
					//_moved = false; // will be set false when getting.
				}
			}
		}
	}
}
