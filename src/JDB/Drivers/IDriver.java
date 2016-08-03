package JDB.Drivers;

import JDB.Lib.Query;
import java.sql.ResultSet;

/**
 * @author sam@samwaters.com
 * @version 1.0.0
 * @date 04/08/2016
 */
public interface IDriver
{
  int insert(Query query);
  boolean isConnected();
  ResultSet query(Query query);
  int update(Query query);
}
