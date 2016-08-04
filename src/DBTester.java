import JDB.Drivers.Mysql;
import JDB.Utils.Utils;
import JDB.Enums.WhereOperator;
import JDB.Lib.Where;
import JDB.Tests.TestTableMapper;

import java.util.ArrayList;

/**
 * @author sam@samwaters.com
 * @version 1.0.0
 * @date 25/04/2016
 */
public class DBTester
{
  public DBTester()
  {

  }

  public static void main(String args[])
  {
    Mysql driver = new Mysql("localhost", "test", "test");
    TestTableMapper n = new TestTableMapper();
    n.setDriver(driver);
    n.id = 0;
    n.name = "Sam New";
    n.email = "sam_new@samwaters.com";
    n.score = 75;
    n.value = 9.98;
    n.save();
    Utils.logMessage("Insert ID " + n.id);
    //n.name = "Sam New User";
    n.email = "sam_new_test@samwaters.com";
    n.score = 76;
    n.value = 9.99;
    n.save();

    TestTableMapper m = new TestTableMapper();
    m.setDriver(driver);
    m.loadAll();
    while(m.hasNext())
    {
      TestTableMapper row = (TestTableMapper)m.next();
      Utils.logMessage("Row : " + row.id + " , " + row.name + " , " + row.email + " , " + row.score + " , " + row.value);
    }
    Utils.logMessage("---------------------------");
    TestTableMapper o = new TestTableMapper();
    o.setDriver(driver);
    ArrayList<Where> where = new ArrayList<Where>();
    where.add(new Where("id", WhereOperator.GREATER_THAN_OR_EQUAL, "5"));
    where.add(new Where("id", WhereOperator.LESS_THAN, "10"));
    o.loadWhere(where);
    while(o.hasNext())
    {
      TestTableMapper row = (TestTableMapper)o.next();
      Utils.logMessage("Row : " + row.id + " , " + row.name + " , " + row.email + " , " + row.score + " , " + row.value);
    }
  }
}
