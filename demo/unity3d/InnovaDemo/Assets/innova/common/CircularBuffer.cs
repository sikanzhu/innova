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

namespace innova.common
{
	public class CircularBuffer
	{
		private byte[] _buffer;
		private int _size = 0;
		private int _rp = 0; // reading position
		private int _wp = 0; // writing position

		public CircularBuffer( int capacity )
		{
			_buffer = new byte[ capacity ];
		}

		public int Write( byte[] b , int offset , int length )
		{
			if( _size + length > _buffer.Length ) return 0;

			if( _buffer.Length > _wp + length )
			{
				Buffer.BlockCopy( b , offset , _buffer , _wp , length );
				_wp += length;
			}
			else
			{
				int len = _buffer.Length - _wp;
				Buffer.BlockCopy( b , offset , _buffer , _wp , len );

				int new_wp = length - len;
				if( new_wp > 0 )
				{
					Buffer.BlockCopy( b , offset + len , _buffer , 0 , new_wp );
				}

				_wp = new_wp;
			}

			_size += length;

			return length;
		}
		public int Write( byte[] b , int length )
		{
			return Write( b , 0 , length );
		}
		public int Write( byte[] b )
		{
			return Write( b , 0 , b.Length );
		}

		public byte[] Read( int length )
		{
			if( length > _size ) return null;

			byte[] ret = new byte[ length ];
			if( _buffer.Length > _rp + length )
			{
				Buffer.BlockCopy( _buffer , _rp , ret , 0 , length );
				_rp += length;
			}
			else
			{
				int len = _buffer.Length - _rp;
				Buffer.BlockCopy( _buffer , _rp , ret , 0 , len );

				int new_rp = length - len;
				if( new_rp > 0 )
				{
					Buffer.BlockCopy( _buffer , 0 , ret , len , new_rp );
				}

				_rp = new_rp;
			}

			_size -= length;

			return ret;
		}

		public int Size()
		{
			return _size;
		}

		public int Left()
		{
			return _buffer.Length - _size;
		}

		public int Capacity()
		{
			return _buffer.Length;
		}
	}
}