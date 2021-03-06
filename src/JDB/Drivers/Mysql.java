package JDB.Drivers;

import JDB.Lib.Query;
import JDB.Utils.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author sam@samwaters.com
 * @version 1.0.0
 * @date 25/04/2016
 */
public class Mysql implements IDriver
{
  private String _host;
  private String _username;
  private String _password;
  private Connection _connection;

  public Mysql(String host, String username, String password)
  {
    try
    {
      Class.forName("com.mysql.jdbc.Driver");
      this._host = "jdbc:mysql://" + host;
      this._username = username;
      this._password = password;
      this._refreshConnection();
    }
    catch(Exception e)
    {
      Utils.logMessage("MySQL Driver Error: " + e.getMessage());
    }
  }

  private boolean _refreshConnection()
  {
    try
    {
      this._connection = DriverManager.getConnection(this._host, this._username, this._password);
      return true;
    }
    catch(Exception e)
    {
      Utils.logMessage("Failed to connect to mysql : " + e.getMessage());
      return false;
    }
  }

  private void _checkConnection() throws Exception
  {
    if(!this.isConnected() && !this._refreshConnection())
    {
      throw new Exception("Failed to connect to MySQL");
    }
  }

  /**
   * Run in Insert query on the database
   * @param query The insert query
   * @return The insert id
   */
  public int insert(Query query)
  {
    try
    {
      this._checkConnection();
      Statement statement = this._connection.createStatement();
      statement.executeUpdate(query.getQuery(), Statement.RETURN_GENERATED_KEYS);
      ResultSet resultSet = statement.getGeneratedKeys();
      return resultSet.next() ? resultSet.getInt(1) : -1;
    }
    catch(Exception e)
    {
      Utils.logMessage("Failed to execute query : " + e.getMessage());
      return -1;
    }
  }

  /**
   * @return Whether this driver is currently connected
   */
  public boolean isConnected()
  {
    try
    {
      return this._connection.isValid(5);
    }
    catch(Exception e)
    {
      Utils.logMessage("Failed to validate connection : " + e.getMessage());
      return false;
    }
  }

  /**
   * Run a Select query on the database
   * @param query The select query to run
   * @return A ResultSet object of results
   */
  public ResultSet query(Query query)
  {
    try
    {
      this._checkConnection();
      Statement statement = this._connection.createStatement();
      return statement.executeQuery(query.getQuery());
    }
    catch(Exception e)
    {
      Utils.logMessage("Failed to execute query : " + e.getMessage());
      return null;
    }
  }

  /**
   * Run an Update query on the database
   * @param query The update query to run
   * @return The number of rows affected
   */
  public int update(Query query)
  {
    try
    {
      this._checkConnection();
      Statement statement = this._connection.createStatement();
      return statement.executeUpdate(query.getQuery());
    }
    catch(Exception e)
    {
      Utils.logMessage("Failed to execute query : " + e.getMessage());
      return -1;
    }
  }
}
