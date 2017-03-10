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

using System;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

namespace innova.common
{
	public class ConsoleOutput : MonoBehaviour
	{
		public static int MaxLine = 10;
		private static Text _text_out;
		private static List<string> _lines = new List<string>();
		private static bool _updated = false;

		public enum LEVEL
		{
			TRACE ,
			DEBUG ,
			INFO ,
			WARNING ,
			ERROR ,
			FATAL ,
		}

		public static LEVEL level = LEVEL.DEBUG;

		// Use this for initialization
		void Awake()
		{
			_text_out = GetComponent<Text>();
		}

		// Update
		void Update()
		{
			lock (_lines)
			{
				if(_updated)
				{
					_text_out.text = "";
					for (int i = 0; i < _lines.Count; ++i)
					{
						string cur = _lines[i];
						_text_out.text += cur + "\n";
					}

					_updated = false;
                }
			}
		}

		// log
		private static void Output( string f )
		{
			lock(_lines)
			{
				if (_lines.Count >= MaxLine)
				{
					_lines.RemoveAt(0);
				}
				_lines.Add(DateTime.Now.ToLongTimeString() + f);

				UnityEngine.Debug.Log( f );

				_updated = true;
            }
		}

		public static void Trace(string f)
		{
			if(level <= LEVEL.TRACE)
				Output("[TRACE] " + f);
		}

		public static void Debug(string f)
		{
			if (level <= LEVEL.DEBUG)
				Output("[DEBUG] " + f);
		}

		public static void Info(string f)
		{
			if (level <= LEVEL.INFO)
				Output("[INFO] " + f);
		}

		public static void Warning(string f)
		{
			if (level <= LEVEL.WARNING)
				Output("[WARNING] " + f);
		}

		public static void Error(string f)
		{
			if (level <= LEVEL.ERROR)
				Output("[ERROR] " + f);
		}

		public static void Fatal(string f)
		{
			if (level <= LEVEL.FATAL)
				Output("[FATAL] " + f);
		}
	}
}