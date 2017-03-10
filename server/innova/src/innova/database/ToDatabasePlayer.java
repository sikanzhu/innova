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
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ToDatabasePlayer
{	
	private final static Logger _logger = Logger.getLogger( "" );
	
	// constants
	private static final String TABLE = "Player";
	private static final String ADD = "INSERT INTO " + TABLE + " (Id, Account, Name) VALUES (?,?,?);";
	private static final String REMOVE = "DELETE FROM " + TABLE + " WHERE Id=?;";
	private static final String READ = "SELECT Data FROM " + TABLE + " WHERE Id=?;";
	private static final String WRITE = "UPDATE " + TABLE + " SET Data=? WHERE Id=?;";
	
	// variables
	private final Connection _connection;
	private PreparedStatement _adds;
	private PreparedStatement _removes;
	private PreparedStatement _reads;
	private PreparedStatement _writes;
	
	private PreparedStatement _reads_account;
	private PreparedStatement _reads_name;
	private PreparedStatement _find_player;
	
	// constructor
	public ToDatabasePlayer( final Connection connection )
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
			
			_reads_account = _connection.prepareStatement( "SELECT Account FROM " + TABLE + " WHERE Id=?;" );
			_reads_name = _connection.prepareStatement( "SELECT Name FROM " + TABLE + " WHERE Id=?;" );
			_find_player = _connection.prepareStatement( "SELECT Id FROM " + TABLE + " WHERE Account=?;" );
		}
		catch( SQLException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			
			_reads_account.close();
			_reads_name.close();
			_find_player.close();
		}
		catch( SQLException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// methods
	public boolean Add( final long id , final long account , final String name )
	{
		int count = 0;
		try
		{
			_adds.clearParameters();
			_adds.setLong( 1 , id );
			_adds.setLong( 2 , account );
			_adds.setString( 3 , name );
			count = _adds.executeUpdate();
			_connection.commit();
		}
		catch( SQLException e )
		{
			_logger.warn( e );
		}
		return count == 1;
	}
	
	public boolean Remove( final long id )
	{
		int count = 0;
		try
		{
			_removes.setLong( 1 , id );
			count = _removes.executeUpdate();
			_connection.commit();
		}
		catch( SQLException e )
		{
			_logger.warn( e );
		}
		return count == 1;
	}
	
	public boolean Write( final long id , final byte[] data )
	{
		int count = 0;
		try
		{
			_writes.setLong( 2 , id );
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
	
	public byte[] Read( final long id )
	{
		byte[] data = null;
		try
		{
			_reads.setLong( 1 , id );
			ResultSet rs = _reads.executeQuery();
			int count = 0;
			while( rs.next() )
			{
				if( count == 1 )
				{
					_logger.fatal( new Exception( "This code should not be reached: More than one Player has the Id" + id ) );
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
	
	public long ReadAccount( final long id )
	{
		long account = 0;
		try
		{
			_reads_account.setLong( 1 , id );
			ResultSet rs = _reads_account.executeQuery();
			int count = 0;
			while( rs.next() )
			{
				if( count == 1 )
				{
					_logger.fatal( new Exception( "This code should not be reached: More than one Player has the Id" + id ) );
				}
				
				account = rs.getLong( "Account" );
				
				++count;
			}
			rs.close();
		}
		catch( SQLException e )
		{
			_logger.error( new Exception().getStackTrace()[0] , e );
		}
		return account;
	}
	
	public String ReadName( final long id )
	{
		String name = null;
		try
		{
			_reads_name.setLong( 1 , id );
			ResultSet rs = _reads_name.executeQuery();
			int count = 0;
			while( rs.next() )
			{
				if( count == 1 )
				{
					_logger.fatal( new Exception( "This code should not be reached: More than one Player has the Id" + id ) );
				}
				
				name = rs.getString( "Name" );
				
				++count;
			}
			rs.close();
		}
		catch( SQLException e )
		{
			_logger.error( new Exception().getStackTrace()[0] , e );
		}
		return name;
	}
	
	public Long[] FindPlayer( final long account )
	{
		final List<Long> players = new ArrayList<Long>();
		
		try
		{
			_find_player.setLong( 1 , account );
			ResultSet rs = _find_player.executeQuery();
			while( rs.next() )
			{
				players.add( rs.getLong( "Id" ) );
			}
			rs.close();
		}
		catch( SQLException e )
		{
			_logger.error( new Exception().getStackTrace()[0] , e );
		}
		
		final Long[] ret = new Long[players.size()]; 
		return players.toArray( ret );
	}
}
