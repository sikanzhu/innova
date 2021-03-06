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

package innova.core;

public interface IPlugin
{
	public void Init( final String[] args );
	
	// @return: Return Update() interval.
	//			nagetive number means "Do not trigger Update()".
	//			0 means "Always trigger Update()".
	public float Start( final String[] args );
	
	// @return: Continue? If any of the plugin return false, the Core will Stop() all the plugins.
	public boolean Update( final float elapse );
	
	public void Pause();
	
	public void Resume();
	
	public void Stop( final String[] args );
	
	public void Deinit( final String[] args );
}

/*
 * template

public class ?? implements IPlugin
{
	private final static Logger _logger = Logger.getLogger(.class.getName());

	@Override
	public void Init( final String[] args )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
	}

	@Override
	public float Start( final String[] args )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
		return -1;
	}

	@Override
	public boolean Update( final float elapse )
	{
		_logger.trace( new Exception().getStackTrace()[0] + "( elapse = " + elapse + ")" );
		return true;
	}

	@Override
	public void Pause()
	{
		_logger.trace( new Exception().getStackTrace()[0] );
	}

	@Override
	public void Resume()
	{
		_logger.trace( new Exception().getStackTrace()[0] );
	}

	@Override
	public void Stop( final String[] args )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
	}

	@Override
	public void Deinit( final String[] args )
	{
		_logger.trace( new Exception().getStackTrace()[0] );
	}
}

 */
