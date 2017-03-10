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

public class ToDatabaseAccount
{
	private final static Logger _logger = Logger.getLogger( "" );
	
	// variables
	final Connection _connection;
	private PreparedStatement _adds;
	private PreparedStatement _removes;
	private PreparedStatement _reads_data;
	private PreparedStatement _reads_name;
	private PreparedStatement _reads_id;
	private PreparedStatement _writes_name_password;
	private PreparedStatement _writes_data;
	private PreparedStatement _match_name_password;
	
	// constructor
	public ToDatabaseAccount( final Connection connection )
	{
		_connection = connection;
	}
	
	public void Init()
	{
		try
		{
			_adds = _connection.prepareStatement
					( "INSERT INTO Account (Id) VALUES (?);" );
			_removes = _connection.prepareStatement
					( "DELETE FROM Account WHERE Id=?;" );
			_reads_data = _connection.prepareStatement
					( "SELECT Data FROM Account WHERE Id=?;" );
			_reads_name = _connection.prepareStatement
					( "SELECT Name FROM Account WHERE Id=?;" );
			_reads_id = _connection.prepareStatement
					( "SELECT Id FROM Account WHERE Name=?;" );
			_writes_name_password = _connection.prepareStatement
					( "UPDATE Account SET Name=?, Password=? WHERE Id=?;" );
			_writes_data = _connection.prepareStatement
					( "UPDATE Account SET Data=? WHERE Id=?;" );
			_match_name_password = _connection.prepareStatement
					( "SELECT Id FROM Account WHERE Name=? AND Password=?;" );
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
			_reads_data.close();
			_reads_name.close();
			_reads_id.close();
			_writes_name_password.close();
			_writes_data.close();
			_match_name_password.close();
		}
		catch( SQLException e )
		{
			_logger.error( new Exception().getStackTrace()[0] , e );
		}
	}
	
	// methods
	public boolean Add( final long id )
	{
		int count = 0;
		try
		{
			_adds.clearParameters();
			_adds.setLong( 1 , id );
			count = _adds.executeUpdate();
			_connection.commit();
		}
		catch( SQLException e )
		{
			_logger.warn( new Exception().getStackTrace()[0] , e );
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
			_logger.warn( new Exception().getStackTrace()[0] , e );
		}
		return count == 1;
	}
	
	public byte[] ReadData( final long id )
	{
		byte[] data = null;
		try
		{
			_reads_data.setLong( 1 , id );
			ResultSet rs = _reads_data.executeQuery();
			int count = 0;
			while( rs.next() )
			{
				if( count == 1 )
				{
					_logger.fatal( new Exception( "This code should not be reached: More than one Account has the Id" + id ) );
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
					_logger.fatal( new Exception( "This code should not be reached: More than one Account has the Id" + id ) );
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
	
	// return: 
	//	return the Id. If the account is not found, return 0.
	public long ReadId( final String name )
	{
		long id = 0;
		try
		{
			_reads_id.setString( 1 , name );
			ResultSet rs = _reads_id.executeQuery();
			int count = 0;
			while( rs.next() )
			{
				if( count == 1 )
				{
					_logger.fatal( new Exception( "This code should not be reached: More than one Account has the Name" + name ) );
				}
				
				id = rs.getLong( "Id" );
				
				++count;
			}
			rs.close();
		}
		catch( SQLException e )
		{
			_logger.error( new Exception().getStackTrace()[0] , e );
		}
		return id;
	}

	public boolean WriteNamePassword( final long id , final String name , final String password )
	{
		int count = 0;
		try
		{
			_writes_name_password.setLong( 3 , id );
			_writes_name_password.setString( 1 , name );
			_writes_name_password.setString( 2 , password );
			count = _writes_name_password.executeUpdate();
			_connection.commit();
		}
		catch( SQLException e )
		{
			_logger.error( new Exception().getStackTrace()[0] , e );
		}
		return count == 1;
	}
	
	public boolean WriteData( final long id , final byte[] data )
	{
		int count = 0;
		try
		{
			_writes_data.setLong( 2 , id );
			_writes_data.setBytes( 1 , data );
			count = _writes_data.executeUpdate();
			_connection.commit();
		}
		catch( SQLException e )
		{
			_logger.error( new Exception().getStackTrace()[0] , e );
		}
		return count == 1;
	}
	
	public boolean MatchNamePassword( final String name , final String password )
	{
		int count = 0;
		try
		{
			_match_name_password.setString( 1 , name );
			_match_name_password.setString( 2 , password );
			ResultSet rs = _match_name_password.executeQuery();
			while( rs.next() )
			{
				//final long id = rs.getLong( "id" );
				if( count == 1 )
				{
					_logger.fatal( new Exception( "This code should not be reached: More than one Account has the Name" + name ) );
				}
				++count;
			}
			rs.close();
		}
		catch( SQLException e )
		{
			_logger.error( new Exception().getStackTrace()[0] , e );
		}
		return count == 1;
	}
}
