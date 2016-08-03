package JDB.Lib;

import JDB.Enums.QueryType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sam@samwaters.com
 * @version 1.0.0
 * @date 26/04/2016
 */
public class Query
{
  private String _databaseName;
  private HashMap<String, String> _insert;
  private int _limit = 0;
  private ArrayList<String> _select;
  private QueryType _queryType;
  private String _tableName;
  private HashMap<String, String> _update;
  private ArrayList<Where> _where;

  public Query(QueryType type, String tableName)
  {
    this._insert = new HashMap<String, String>();
    this._queryType = type;
    this._select = new ArrayList<String>();
    this._tableName = tableName;
    this._update = new HashMap<String, String>();
    this._where = new ArrayList<Where>();
  }

  private String _getDeleteQuery()
  {
    String query = "DELETE FROM " + this._tableName;
    query += this._getWhereFields();
    if(this._limit > 0)
    {
      query += " LIMIT " + this._limit;
    }
    return query;
  }

  private String _getInsertQuery()
  {
    String query = "INSERT INTO " + this._tableName + "(";
    String values = "";
    for(Map.Entry<String, String> entry : this._insert.entrySet())
    {
      query += entry.getKey() + ",";
      values += "'" + entry.getValue() + "',";
    }
    query = query.substring(0, query.length() - 1);
    values = values.substring(0, values.length() - 1);
    query += ") VALUES(" + values + ")";
    return query;
  }

  private String _getSelectQuery()
  {
    String query = "SELECT ";
    for(String field : this._select)
    {
      query += field + ",";
    }
    query = query.substring(0, query.length() - 1);
    query += " FROM " + this._tableName;
    query += this._getWhereFields();
    if(this._limit > 0)
    {
      query += " LIMIT " + this._limit;
    }
    return query;
  }

  private String _getUpdateQuery()
  {
    String query = "UPDATE " + this._tableName + " SET ";
    for(Map.Entry<String, String> entry : this._update.entrySet())
    {
      query += entry.getKey() + "='" + entry.getValue() + "',";
    }
    query = query.substring(0, query.length() - 1);
    query += this._getWhereFields();
    if(this._limit > 0)
    {
      query += " LIMIT " + this._limit;
    }
    return query;
  }

  private String _getUseQuery()
  {
    return "USE " + this._databaseName;
  }

  private String _getWhereFields()
  {
    if(this._where.size() == 0)
    {
      return "";
    }
    String where = " WHERE ";
    for(Where w : this._where)
    {
      where += w.getAsString() + " AND ";
    }
    return where.substring(0, where.length() - 5);
  }

  public void addInsert(String field, String value)
  {
    this._insert.put(field, value);
  }

  public void addSelect(String field)
  {
    this._select.add(field);
  }

  public void addWhere(Where where)
  {
    this._where.add(where);
  }

  public void addUpdate(String field, String value)
  {
    this._update.put(field, value);
  }

  public void clearInsert()
  {
    this._insert.clear();
  }

  public void clearSelect()
  {

  }

  public void clearWhere()
  {
    this._where.clear();
  }

  public void clearUpdate()
  {
    this._update.clear();
  }

  public String getQuery()
  {
    switch(this._queryType)
    {
      case SELECT:
        return this._getSelectQuery();
      case INSERT:
        return this._getInsertQuery();
      case UPDATE:
        return this._getUpdateQuery();
      case DELETE:
        return this._getDeleteQuery();
      case USE:
        return this._getUseQuery();
      default:
        return "";
    }
  }

  public void limit(int limit)
  {
    this._limit = limit;
  }

  public void removeInsert(String field)
  {
    this._insert.remove(field);
  }

  public void removeSelect(String field)
  {
    if(this._select.contains(field))
    {
      this._select.remove(this._select.indexOf(field));
    }
  }

  public void removeWhere(String field)
  {
    this._where.remove(field);
  }

  public void removeUpdate(String field)
  {
    this._update.remove(field);
  }

  public void setDatabaseName(String dbName)
  {
    this._databaseName = dbName;
  }
}
