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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.apache.log4j.Logger;

public class Core
{
	private List<Service> _services = new ArrayList<Service>();
	private final static Logger _logger = Logger.getLogger("");
	
	public class PluginWrap
	{
		public float timer;
		public float interval;
		public IPlugin plugin;
		
		public PluginWrap( final float i , final IPlugin p )
		{
			timer = 0;
			interval = i;
			plugin = p;
		}
	}
	
	public class PluginName
	{
		public final String pack;
		public final String name;
		public IPlugin plugin;
		
		public PluginName( final String p , final String n )
		{
			pack = p;
			name = n;
		}
	}
	
	public class Service implements IService
	{
		private List<PluginName> _plugins = new ArrayList<PluginName>();
		private List<PluginWrap> _sometimes = new ArrayList<PluginWrap>();
		private List<IPlugin> _always = new ArrayList<IPlugin>();
		
		private final String _name;
		public String GetName() { return _name; }

		public Service( final String name , final Element element )
		{
			// set name
			_name = name;
			
			// read plugin list
			final NodeList plugins = element.getElementsByTagName( "plugin" );
			
			for( int ni = 0 ; ni < plugins.getLength() ; ++ni )
			{
				final Node node = plugins.item( ni );
				if( node.getNodeType() == Node.ELEMENT_NODE )
				{
					final Element plugin = (Element)node; 
					final String tpack =  plugin.getAttribute( "package" );
					final String tname =  plugin.getAttribute( "name" );
					if( null == tpack || "" == tpack )
					{
						_logger.warn( "A plugin has no package" );
						continue;
					}
					if( null == tname || "" == tname )
					{
						_logger.warn( "A plugin has no name" );
						continue;
					}
					
					_plugins.add( new PluginName( tpack , tname ) );
				}
			}
			
			if( _plugins.size() == 0 )
			{
				_logger.warn( "Can not find a valid plugin in service: " + name );
				return;
			}
			
			// load plugins
			for( PluginName p : _plugins )
			{				
				Class<?> cls;
				try
				{
					cls = Class.forName( p.pack + "." + p.name );
				}
				catch( ClassNotFoundException e )
				{
					_logger.error( e , new Exception( "Can not find a plugin: " + p.pack + "." + p.name ) );
					return;
				}
				
				if( false == IPlugin.class.isAssignableFrom( cls ) )
				{
					_logger.error( new Exception( "A plugin does not implements IPlugin: " + p.pack + "." + p.name ) );
					return;
				}
				
				try
				{
					p.plugin = ( IPlugin ) cls.newInstance();
				}
				catch( InstantiationException e )
				{
					_logger.error( e , new Exception( "newInstance(): " + p.pack + "." + p.name ) );
					return;
				}
				catch( IllegalAccessException e )
				{
					_logger.error( e , new Exception( "newInstance(): " + p.pack + "." + p.name ) );
					return;
				}
			}
		}
		
		public void Run( final String[] args )
		{
			//
			_logger.trace( new Exception().getStackTrace()[0] + " Enter Service: " + _name );
			
			// init
			for( PluginName p : _plugins )
			{				
				if( null == p.plugin )
				{
					_logger.error( new Exception( "Can not find a plugin: " + p.pack + "." + p.name ) );
					return;
				}
				p.plugin.Init( args );
			}
			
			// start
			for( PluginName p : _plugins )
			{
				final float interval = p.plugin.Start( args );
				if( 0 == interval )
					_always.add( p.plugin );
				else if( interval > 0 )
					_sometimes.add( new PluginWrap( interval , p.plugin ) );
			}
			
			// update
			if( _always.size() > 0 || _sometimes.size() > 0 )
			{
				boolean running = true;
				long last_time = System.currentTimeMillis();
				while( running )
				{
					final long cur_time = System.currentTimeMillis();
					final long lelapse = cur_time - last_time;
					final float felapse = ((float)lelapse) * 0.001f;
					
					float next = Float.MAX_VALUE;
					
					// update always
					for( IPlugin p : _always )
					{
						if( false == p.Update(felapse) ) running = false;
						next = 0;
					}
					
					// update sometimes
					for( PluginWrap p : _sometimes )
					{
						p.timer += felapse;
						if( p.timer >= p.interval )
						{
							p.timer -= p.interval;
							if( false == p.plugin.Update(p.interval) ) running = false;
						}
						
						final float nxt = Math.max( 0 , p.interval - p.timer );
						if( nxt < next ) next = nxt;
					}
					
					try
					{
						Thread.sleep( ( long ) ( next * 1000 ) );
					}
					catch( InterruptedException e )
					{
						//e.printStackTrace(); //ignore the exception
					}
					
					last_time = cur_time;
				}
			}
			
			// stop
			for( PluginName p : _plugins )
			{
				p.plugin.Stop( args );
			}
			
			// deinit
			for( PluginName p : _plugins )
			{
				p.plugin.Deinit( args );
			}
			
			//
			_logger.trace( new Exception( " Exit service: " + _name ) );
		}

		@Override
		public IPlugin GetPlugin( final String pack , final String name )
		{
			for( PluginName p : _plugins )
			{
				if( p.pack.equals( pack ) && p.name.equals( name ) )
					return p.plugin;
			}
			_logger.warn( "Can not find a plugin: " + pack + "." + name );
			return null;
		}
	}
		
	public void Run( final String service_list_filename , final String run_service_name , final String[] args )
	{
		//
		_logger.trace( new Exception().getStackTrace()[0] + " Enter" );
		
		// read services
		File file = new File( service_list_filename );

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder builder = null;
		try
		{
			builder = factory.newDocumentBuilder();
		}
		catch( ParserConfigurationException e )
		{
			_logger.error( e , new Exception( "newDocumentBuilder()" ) );
			return;
		}
		
		Document doc;
		try
		{
			doc = builder.parse( file );
		}
		catch( SAXException e )
		{
			//e.printStackTrace();
			_logger.error( e , new Exception( "Can not parse service list: " + service_list_filename , new Exception() ) );
			return;
		}
		catch( IOException e )
		{
			//e.printStackTrace();
			_logger.error( e , new Exception( "Can not read service list: " + service_list_filename , new Exception() ) );
			return;
		}
		
		doc.getDocumentElement().normalize();

		NodeList node_list = doc.getElementsByTagName( "service" );
		for( int si = 0 ; si < node_list.getLength() ; si++ )
		{
			Node node = node_list.item( si );
			if( node.getNodeType() == Node.ELEMENT_NODE )
			{
				Element element = ( Element )node;
				final String name = element.getAttribute( "name" );
				if( null != name && "" != name )
				{
					_services.add( new Service( name , element ) );
				}
				else
				{
					_logger.warn( "A service has no name" );
				}
			}
		}
		
		if( _services.size() == 0 )
		{
			_logger.error( new Exception( "Can not find a valid service in service list: " + service_list_filename ) );
			return;
		}
	
		// find service
		Service run_service = null;
		for( Service s : _services )
		{
			if( s.GetName().equals( run_service_name ) )
			{
				run_service = s;
				break;
			}
		}
		
		// run service
		if( null != run_service )
		{
			run_service.Run( args );
		}
		else
		{
			_logger.error( new Exception( "Can not find service: " + service_list_filename + "." + run_service_name ) );
		}
		
		//
		_logger.trace( new Exception().getStackTrace()[0] + " Exit" );
	}
}
