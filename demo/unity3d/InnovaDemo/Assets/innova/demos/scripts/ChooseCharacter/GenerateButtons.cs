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
using UnityEngine.EventSystems;
using UnityEngine.UI;

[RequireComponent(typeof(EventTrigger))]
public class GenerateButtons : MonoBehaviour
{
	public RectTransform panel;
	public GameObject template;

	private static int COLUME = 4;
	
	void Start()
	{
		for( int i = 1 ; i < 16 ; ++i )
		{
			GameObject button_obj = ( GameObject )Instantiate( template , panel );
			SetupButton( button_obj , i );
		}
		SetupButton( template , 0 );
    }

	void ButtonClicked( int button_number )
	{
		PointerEventData e = new PointerEventData(EventSystem.current);
        GetComponent<EventTrigger>().OnPointerClick( e );
	}

	void SetupButton( GameObject button_obj , int index )
	{
		button_obj.name = "Player " + ( index + 1 );
		button_obj.GetComponentInChildren<Text>().text = "Player " + ( index + 1 );
        button_obj.GetComponent<RectTransform>().anchoredPosition = new Vector2( 50 + 100 * ( index % COLUME ) , -25 - 50 * ( index / COLUME ) );
		button_obj.GetComponent<Button>().onClick.AddListener( () => ButtonClicked( ( index + 1 ) ) );
	}
}
