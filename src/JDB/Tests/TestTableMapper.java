package JDB.Tests;

import JDB.Mapper;
/**
 * @author sam@samwaters.com
 * @version 1.0.0
 * @date 25/04/2016
 */
public class TestTableMapper extends Mapper
{
  public String id;
  public String name;
  public String email;
  public Integer score;
  public double value;

  public String getDatabaseName()
  {
    return "test";
  }

  protected String getTableName()
  {
    return "TestTable";
  }
}
