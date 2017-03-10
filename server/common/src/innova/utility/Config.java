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

package innova.utility;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Config
{
	private final Map<String,String> _configs = new HashMap<String,String>();
	private final static Logger _logger = Logger.getLogger("");
	
	public void Load( final String filename )
	{
		File file = new File( filename );

		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try
		{
			builder = factory.newDocumentBuilder();
		}
		catch( ParserConfigurationException e )
		{
			_logger.error( e );
			return;
		}
		Document doc;
		try
		{
			doc = builder.parse( file );
		}
		catch( SAXException e )
		{
			_logger.error( e );
			return;
		}
		catch( IOException e )
		{
			_logger.error( e );
			return;
		}

		doc.getDocumentElement().normalize();

		_logger.info( "config file root name: " + doc.getDocumentElement().getNodeName() );
		
		final NodeList node_list = doc.getElementsByTagName( "property" );
		for( int prop = 0 ; prop < node_list.getLength() ; ++prop )
		{
			final Node node = node_list.item( prop );
			if( node.getNodeType() == Node.ELEMENT_NODE )
			{
				Element element = ( Element )node;
				
				final String name = element.getAttribute( "name" );
				final String value = element.getAttribute( "value" );
			
				_logger.info( "config property: " + name + " = " + value );
				
				_configs.put( name , value );
			}
		}
	}
	
	public String GetString( final String name )
	{
		return _configs.get( name );
	}
	
	public Float GetFloat( final String name )
	{
		return Float.valueOf( _configs.get( name ) );
	}
	
	public Double GetDouble( final String name )
	{
		return Double.valueOf( _configs.get( name ) );
	}
	
	public Integer GetInteger( final String name )
	{
		return Integer.valueOf( _configs.get( name ) );
	}
	
	public Long GetLong( final String name )
	{
		return Long.valueOf( _configs.get( name ) );
	}
}
