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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class ToDatabase
{
	private final static Logger _logger = Logger.getLogger( "" );
	
	private static final String JDBC_NAME = "org.sqlite.JDBC";
	private static final String DATABASE_NAME = "jdbc:sqlite:";
	private Connection _connection = null;
	
	public Connection GetConnection() { return _connection; }
	
	public boolean Connect( final String dbfile_name )
	{
		try
		{
			Class.forName( JDBC_NAME );
		}
		catch( ClassNotFoundException e )
		{
			_logger.error( e );
			return false;
		}
		try
		{
			_connection = DriverManager.getConnection( DATABASE_NAME + dbfile_name );
			_connection.setAutoCommit( false );
		}
		catch( SQLException e )
		{
			_logger.error( new Exception().getStackTrace()[0] , e );
			return false;
		}
		return true;
	}
	
	public boolean Disconnect()
	{
		try
		{
			_connection.close();
		}
		catch( SQLException e )
		{
			_logger.error( new Exception().getStackTrace()[0] , e );
			return false;
		}
		return true;
	}
	
	public boolean CreateTables()
	{
		try
		{
			Statement stmt = _connection.createStatement();
			
			stmt.executeUpdate( "CREATE TABLE Account "
					+ "( Id            BIGINT         NOT NULL        PRIMARY KEY"
					+ ", Name          TEXT                           UNIQUE"
					+ ", Password      TEXT"
					+ ", Data          BLOB );" );
			stmt.executeUpdate( "CREATE TABLE Player "
					+ "( Id            BIGINT         NOT NULL        PRIMARY KEY"
					+ ", Account       BIGINT         NOT NULL"
					+ ", Name          TEXT           NOT NULL        UNIQUE"
					+ ", Data          BLOB"
					+ ", FOREIGN KEY(Account) REFERENCES Account (Id) );" );
			stmt.executeUpdate( "CREATE TABLE Config "
					+ "( Player        BIGINT         NOT NULL"
					+ ", Type          INTEGER        NOT NULL"
					+ ", Data          BLOB"
					+ ", FOREIGN KEY(Player) REFERENCES Player (Id) );" );
			stmt.executeUpdate( "CREATE TABLE Property "
					+ "( Player        BIGINT         NOT NULL"
					+ ", Type          INTEGER        NOT NULL"
					+ ", Data          BLOB"
					+ ", FOREIGN KEY(Player) REFERENCES Player (Id) );" );
			
			stmt.close();
			
			_connection.commit();
		}
		catch( SQLException e )
		{
			_logger.error( new Exception().getStackTrace()[0] , e );
			return false;
		}		
		return true;
	}
	
	public boolean DestroyTables()
	{
		try
		{
			Statement stmt = _connection.createStatement();
			
			stmt.executeUpdate( "DROP TABLE Account;" );
			stmt.executeUpdate( "DROP TABLE Player;" );
			stmt.executeUpdate( "DROP TABLE Config;" );
			stmt.executeUpdate( "DROP TABLE Property;" );
			
			stmt.close();
			
			_connection.commit();
		}
		catch( SQLException e )
		{
			_logger.error( new Exception().getStackTrace()[0] , e );
			return false;
		}		
		return true;
	}
}
