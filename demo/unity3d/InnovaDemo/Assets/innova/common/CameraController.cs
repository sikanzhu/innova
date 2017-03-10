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

public class CameraController : MonoBehaviour
{
	public Vector3 LookAtPosition;

	public float Distance = 50;
	public float RotationSensitive = 0.1f;
	public float DistanceSensitive = 0.5f;
	public Vector3 RotationEular = new Vector3(45 , 0 , 0);

	private Vector3 _last_mouse_position;

	public float SmoothTime = 0.3f;
	public float SmoothMaxSpeed = 10f;
	private Vector3 _smooth_velocity = Vector3.zero;

	private float _yaw = 0;
	public float Yaw { get { return _yaw; } private set { } }

	void Start()
	{
		_last_mouse_position = Input.mousePosition;
	}

	void Update()
	{
		// distance
		Distance -= Input.mouseScrollDelta.y * DistanceSensitive;

		// rotate camera
		Vector3 mouse_position = Input.mousePosition;

		if( Input.GetMouseButton(1) )
		{
			Vector3 delta = mouse_position - _last_mouse_position;
			RotationEular += new Vector3(-delta.y , delta.x) * RotationSensitive;
        }

		_last_mouse_position = mouse_position;

		// position and look at
		Quaternion rotation = Quaternion.Euler(RotationEular);
		Vector3 position = LookAtPosition - Distance * ( rotation * Vector3.forward );

		// move smoothly
		Vector3 new_position = Vector3.SmoothDamp(transform.position, position, ref _smooth_velocity, SmoothTime, SmoothMaxSpeed);
		transform.position = new_position;
        transform.rotation = Quaternion.LookRotation( LookAtPosition - new_position);

		// set yaw
		_yaw = transform.rotation.eulerAngles.y;
	}
}
