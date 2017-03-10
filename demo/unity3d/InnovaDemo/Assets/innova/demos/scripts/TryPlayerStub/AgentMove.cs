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

using Google.Protobuf;
using Innova.Demo;
using innova.common;
using UnityEngine;

public class AgentMove : MonoBehaviour
{
	private Vector3 _local_velocity = Vector3.zero;
	private Vector4 _local_angular = Vector4.zero;

	public float Acceleration = 1;
	public float AngularAcceleration = 360;

	// movement
	public void DecodeMovement(ByteString movement)
	{
		ActorMovement amovement = ActorMovement.Parser.ParseFrom(movement);

		transform.localPosition = DataBridge.cnv(amovement.Location);
		_local_velocity = DataBridge.cnv(amovement.Velocity);

		transform.localRotation = DataBridge.cnv(amovement.Rotation);
		_local_angular = DataBridge.cnv(amovement.Angular);

		ConsoleOutput.Trace("AgentMove " + amovement.Angular.W + " @ " + amovement.Angular.Y);
	}

	// MonoBehaviour methods
	void Update()
	{
		float delta_time = Time.deltaTime;

		{
			float speed = _local_velocity.magnitude;
			if (speed > 0)
			{
				float vt = speed / Acceleration;
				_local_velocity = Vector3.Lerp(_local_velocity, Vector3.zero, delta_time / vt);
			}

			transform.localPosition += _local_velocity * delta_time;
		}

		{
			if (_local_angular.w > 0)
			{
				_local_angular.w -= AngularAcceleration * delta_time;
				if (_local_angular.w < 0) _local_angular.w = 0;
			}
			else if (_local_angular.w < 0)
			{
				_local_angular.w += AngularAcceleration * delta_time;
				if (_local_angular.w > 0) _local_angular.w = 0;
			}
			if (_local_angular.w != 0)
			{
				transform.localRotation *= Quaternion.AngleAxis(_local_angular.w * delta_time, new Vector3(_local_angular.x, _local_angular.y, _local_angular.z));
			}
		}
	}
}
