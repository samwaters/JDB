package JDB.Tests;

import JDB.Enums.IdType;
import JDB.Mapper;
/**
 * @author sam@samwaters.com
 * @version 1.0.0
 * @date 25/04/2016
 */
public class TestTableMapper extends Mapper
{
  public int id;
  public String name;
  public String email;
  public Integer score;
  public double value;

  public String getDatabaseName()
  {
    return "test";
  }

  public IdType getIdType()
  {
    return IdType.AUTO;
  }

  protected String getTableName()
  {
    return "TestTable";
  }
}
