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

package innova.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class ToDatabaseProperty
{
	private final static Logger _logger = Logger.getLogger( "" );
	
	// constants
	private static final String TABLE = "Property";
	private static final String ADD = "INSERT INTO " + TABLE + " (Player, Type) VALUES (?,?);";
	private static final String REMOVE = "DELETE FROM " + TABLE + " WHERE Player=? AND Type=?;";
	private static final String READ = "SELECT Data FROM " + TABLE + " WHERE Player=? AND Type=?;";
	private static final String WRITE = "UPDATE " + TABLE + " SET Data=? WHERE Player=? AND Type=?;";
	
	// variables
	private final Connection _connection;
	private PreparedStatement _adds;
	private PreparedStatement _removes;
	private PreparedStatement _reads;
	private PreparedStatement _writes;
	
	// constructor
	public ToDatabaseProperty( final Connection connection )
	{
		_connection = connection;
	}
	
	public void Init()
	{
		try
		{
			_adds = _connection.prepareStatement( ADD );
			_removes = _connection.prepareStatement( REMOVE );
			_reads = _connection.prepareStatement( READ );
			_writes = _connection.prepareStatement( WRITE );
		}
		catch( SQLException e )
		{
			_logger.error( new Exception().getStackTrace()[0] , e );
		}
	}
	
	public void Deinit()
	{
		try
		{
			_adds.close();
			_removes.close();
			_reads.close();
			_writes.close();
		}
		catch( SQLException e )
		{
			_logger.error( new Exception().getStackTrace()[0] , e );
		}
	}
	
	// methods
	public boolean Add( final long player , final int type )
	{
		int count = 0;
		try
		{
			_adds.clearParameters();
			_adds.setLong( 1 , player );
			_adds.setInt( 2 , type );
			count = _adds.executeUpdate();
			_connection.commit();
		}
		catch( SQLException e )
		{
			_logger.warn( e );
		}
		return count == 1;
	}
	
	public boolean Remove( final long player , final int type )
	{
		int count = 0;
		try
		{
			_removes.setLong( 1 , player );
			_removes.setInt( 2 , type );
			count = _removes.executeUpdate();
			_connection.commit();
		}
		catch( SQLException e )
		{
			_logger.warn( e );
		}
		return count == 1;
	}
	
	public boolean Write( final long player , final int type , final byte[] data )
	{
		int count = 0;
		try
		{
			_writes.setLong( 2 , player );
			_writes.setInt( 3 , type );
			_writes.setBytes( 1 , data );
			count = _writes.executeUpdate();
			_connection.commit();
		}
		catch( SQLException e )
		{
			_logger.error( new Exception().getStackTrace()[0] , e );
		}
		return count == 1;
	}
	
	public byte[] Read( final long player , final int type )
	{
		byte[] data = null;
		try
		{
			_reads.setLong( 1 , player );
			_reads.setInt( 2 , type );
			ResultSet rs = _reads.executeQuery();
			int count = 0;
			while( rs.next() )
			{
				if( count == 1 )
				{
					_logger.fatal( new Exception( "This code should not be reached: More than one Property links to Player" + player + "." + type ) );
				}
				
				data = rs.getBytes( "Data" );
				
				++count;
			}
			rs.close();
		}
		catch( SQLException e )
		{
			_logger.error( new Exception().getStackTrace()[0] , e );
		}
		return data;
	}
}
