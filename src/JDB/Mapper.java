package JDB;

import JDB.Drivers.IDriver;
import JDB.Enums.IdType;
import JDB.Enums.QueryType;
import JDB.Enums.WhereOperator;
import JDB.Lib.Query;
import JDB.Lib.Where;
import JDB.Utils.Utils;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author sam@samwaters.com
 * @version 1.0.0
 * @date 04/08/2016
 */
abstract public class Mapper<T> implements Iterator
{
  private Class<T> _class;
  private HashMap<String, Object> _dataCache = new HashMap<>();
  private IDriver _driver;
  private int _pointer = 0;
  private ArrayList<T> _rows = new ArrayList<>();

  public Mapper()
  {
    this._class = (Class<T>)this.getClass();
  }

  abstract protected String getDatabaseName();
  abstract protected String getTableName();

  /**
   * The id field (primary key) of the table
   * @return The id field of the table
   */
  public String getIdField()
  {
    return "id";
  }

  /**
   * The id type of the table (none, auto, manual)
   * @return The id type of the table
   */
  public IdType getIdType()
  {
    return IdType.NONE;
  }

  /**
   * Whether the record collection has more items
   * @return bool
   */
  public final boolean hasNext()
  {
    return this._rows.size() > this._pointer;
  }

  /**
   * Load all the records from the table into the record collection
   * @return bool
   */
  public final boolean loadAll()
  {
    Query query = new Query(QueryType.SELECT, this.getTableName());
    Field[] fields = this._class.getFields();
    for(Field f : fields)
    {
      query.addSelect(f.getName());
    }
    ResultSet resultSet = this._driver.query(query);
    try
    {
      while(resultSet.next())
      {
        T instance = (T)this.getClass().getDeclaredConstructors()[0].newInstance();
        ((Mapper) instance)._hydrate(fields, resultSet);
        this._rows.add(instance);
      }
      return true;
    }
    catch(Exception e)
    {
      Utils.logMessage("Error loading records : " + e.getMessage());
      return false;
    }
  }

  /**
   * Load one record from the table matching the specified query
   * @param where Conditions to match
   * @return T|null
   */
  public final T loadOneWhere(ArrayList<Where> where)
  {
    Query query = new Query(QueryType.SELECT, this.getTableName());
    Field[] fields = this._class.getFields();
    for(Field f : fields)
    {
      query.addSelect(f.getName());
    }
    for(Where w : where)
    {
      query.addWhere(w);
    }
    ResultSet resultSet = this._driver.query(query);
    try
    {
      if(resultSet.next())
      {
        T instance = (T)this.getClass().getDeclaredConstructors()[0].newInstance();
        ((Mapper) instance)._hydrate(fields, resultSet);
        return instance;
      }
      return null;
    }
    catch(Exception e)
    {
      Utils.logMessage("Error loading record : " + e.getMessage());
      return null;
    }
  }

  /**
   * Load all the records from the table matching the specified query
   * @param where Conditions to match
   * @return boolean
   */
  public final boolean loadWhere(ArrayList<Where> where)
  {
    Query query = new Query(QueryType.SELECT, this.getTableName());
    Field[] fields = this._class.getFields();
    for(Field f : fields)
    {
      query.addSelect(f.getName());
    }
    for(Where w : where)
    {
      query.addWhere(w);
    }
    ResultSet resultSet = this._driver.query(query);
    try
    {
      while(resultSet.next())
      {
        T instance = (T)this.getClass().getDeclaredConstructors()[0].newInstance();
        ((Mapper) instance)._hydrate(fields, resultSet);
        this._rows.add(instance);
      }
      return true;
    }
    catch(Exception e)
    {
      Utils.logMessage("Error loading records : " + e.getMessage());
      return false;
    }
  }

  /**
   * Move to the next record in the collection
   * @return T|null
   */
  public final T next()
  {
    if(this._rows.size() > this._pointer)
    {
      T instance = this._rows.get(this._pointer);
      this._pointer++;
      return instance;
    }
    return null;
  }

  /**
   * Save the current mapper
   * @return Whether the save was successful
   */
  public final boolean save()
  {
    try
    {
      if(this._dataCache.size() == 0)
      {
        //This is a new mapper, save as insert
        this._saveInsert();
        //Save the current data to the cache
        this._cacheCurrent();
      }
      else
      {
        //Have any fields changed?
        boolean hasChanged = false;
        for(Field f : this._class.getFields())
        {
          if(!f.get(this).equals(this._dataCache.get(f.getName())))
          {
            hasChanged = true;
            break;
          }
        }
        if(hasChanged)
        {
          if(this.getIdType() == IdType.NONE)
          {
            //No Id, so save as insert
            this._saveInsert();
          }
          else
          {
            //Has the id field changed?
            if(this._class.getField(this.getIdField()).get(this).equals(this._dataCache.get(this.getIdField())))
            {
              //No, do an update
              this._saveUpdate();
            }
            else
            {
              //Yes, does the new id already exist?
              ArrayList<Where> whereList = new ArrayList<>();
              Where where = new Where(this.getIdField(), WhereOperator.EQUALS, this._class.getField(this.getIdField()).get(this).toString());
              whereList.add(where);
              if(this.loadOneWhere(whereList) != null)
              {
                //Yes, do an update
                this._saveUpdate();
              }
              else
              {
                //No, do an insert
                this._saveInsert();
              }
            }
          }
          //Save the current data to the cache
          this._cacheCurrent();
        }
      }
      return true;
    }
    catch(Exception e)
    {
      Utils.logMessage("Cannot save record : " + e.getMessage());
      return false;
    }
  }

  /**
   * Set the database driver for this mapper
   * @param driver An instance of IDriver to use
   */
  public void setDriver(IDriver driver)
  {
    this._driver = driver;
    Query query = new Query(QueryType.USE, this.getTableName());
    query.setDatabaseName(this.getDatabaseName());
    this._driver.query(query);
  }

  /**
   * Cache a field value
   * @param key The field name
   * @param value The field value
   */
  private void _cache(String key, Object value)
  {
    this._dataCache.put(key, value);
  }

  /**
   * Cache the current mapper's data
   */
  private void _cacheCurrent()
  {
    try
    {
      for(Field f : this._class.getFields())
      {
        this._dataCache.put(f.getName(), f.get(this));
      }
    }
    catch(Exception e)
    {
      Utils.logMessage("Unable to cache current : " + e.getMessage());
    }
  }

  /**
   * Hydrate (set the field values) with the given data
   * @param fields The fields to hydrate
   * @param data The data to hydrate with
   */
  private void _hydrate(Field[] fields, ResultSet data)
  {
    try
    {
      for(Field f : fields)
      {
        switch(f.getType().getSimpleName().toLowerCase())
        {
          case "byte":
            this._cache(f.getName(), data.getByte(f.getName()));
            f.set(this, data.getByte(f.getName()));
            break;
          case "date":
            ((Mapper) this)._cache(f.getName(), data.getDate(f.getName()));
            f.set(this, data.getDate(f.getName()));
            break;
          case "double":
            ((Mapper) this)._cache(f.getName(), data.getDouble(f.getName()));
            f.set(this, data.getDouble(f.getName()));
            break;
          case "float":
            ((Mapper) this)._cache(f.getName(), data.getFloat(f.getName()));
            f.set(this, data.getFloat(f.getName()));
            break;
          case "int":
          case "integer":
            ((Mapper) this)._cache(f.getName(), new Integer(data.getInt(f.getName())));
            f.set(this, data.getInt(f.getName()));
            break;
          case "long":
            ((Mapper) this)._cache(f.getName(), new Long(data.getLong(f.getName())));
            f.set(this, data.getLong(f.getName()));
            break;
          case "short":
            ((Mapper) this)._cache(f.getName(), new Short(data.getShort(f.getName())));
            f.set(this, data.getShort(f.getName()));
            break;
          case "string":
            ((Mapper) this)._cache(f.getName(), data.getString(f.getName()));
            f.set(this, data.getString(f.getName()));
            break;
          case "time":
            ((Mapper) this)._cache(f.getName(), data.getTime(f.getName()));
            f.set(this, data.getTime(f.getName()));
            break;
          case "timestamp":
            ((Mapper) this)._cache(f.getName(), data.getTimestamp(f.getName()));
            f.set(this, data.getTimestamp(f.getName()));
            break;
          default:
            Utils.logMessage("Unable to determine how to get " + f.getType().getSimpleName() + " from resultSet");
        }
      }
    }
    catch(Exception e)
    {
      Utils.logMessage("Error hydrating object : " + e.getMessage());
    }
  }

  /**
   * Save the current mapper as a new entry (insert query)
   * @return Whether the save was successful
   */
  private boolean _saveInsert()
  {
    try
    {
      Query query = new Query(QueryType.INSERT, this.getTableName());
      Field[] fields = this._class.getFields();
      for(Field f : fields)
      {
        query.addInsert(f.getName(), f.get(this).toString());
      }
      if(this.getIdType() == IdType.AUTO)
      {
        //Auto-increment id - we need to return it
        int insertId = this._driver.insert(query);
        if(insertId > 0)
        {
          if(this._class.getField(this.getIdField()).getType().getSimpleName().equals("String"))
          {
            this._class.getField(this.getIdField()).set(this, "" + insertId);
            this._dataCache.put(this.getIdField(), new String("" + insertId));
          }
          else
          {
            this._class.getField(this.getIdField()).set(this, insertId);
            this._dataCache.put(this.getIdField(), new Integer(insertId));
          }
          return true;
        }
        return false;
      }
      else
      {
        //Manual id - no need to return it
        return this._driver.update(query) > 0;
      }
    }
    catch(Exception e)
    {
      Utils.logMessage("Cannot save insert record : " + e.getMessage());
      return false;
    }
  }

  /**
   * Save the current mapper as an update (update query)
   * @return Whether the save was successful
   */
  private boolean _saveUpdate()
  {
    try
    {
      Query query = new Query(QueryType.UPDATE, this.getTableName());
      Field[] fields = this._class.getFields();
      for(Field f : fields)
      {
        query.addUpdate(f.getName(), f.get(this).toString());
      }
      Where where = new Where(this.getIdField(), WhereOperator.EQUALS, this._class.getField(this.getIdField()).get(this).toString());
      query.addWhere(where);
      return this._driver.update(query) >= 0;
    }
    catch(Exception e)
    {
      Utils.logMessage("Cannot save update record : " + e.getMessage());
      return false;
    }
  }

  public final void reset()
  {
    this._pointer = 0;
  }
}
