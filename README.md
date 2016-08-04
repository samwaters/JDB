#Java Database Access Layer
Library designed to simplify database access in Java
##Creating a mapper
1. Extend the `Mapper` class
2. Create all of the database fields as public properties of the class
3. Override the `getDatabaseName()` and `getTableName()` methods
```
public class MyMapper extends Mapper
{
  public int id;
  public String name;
  
  public function getDatabaseName()
  {
    return "TestDatabase";
  }
  
  public function getTableName()
  {
    return "TestTable";
  }
}
```
###Drivers
- All drivers implement the `IDriver` interface
- Currently only `MySQL` is supported
- Set the driver on the mapper with the setDriver method
```
Mysql driver = new Mysql("localhost", "test", "test");
mapper.setDriver(driver);
```

##Fetching records

##Inserting records

##Updating records