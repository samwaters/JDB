package JDB.Lib;

import JDB.Enums.WhereOperator;

/**
 * @author sam@samwaters.com
 * @version 1.0.0
 * @date 26/04/2016
 */
public class Where
{
  private String _field;
  private WhereOperator _operator;
  private String _value;

  public Where(String field, WhereOperator operator, String value)
  {
    this._field = field;
    this._operator = operator;
    this._value = value;
  }

  public String getAsString()
  {
    String whereString = this._field;
    switch(this._operator)
    {
      case EQUALS:
        whereString += "=";
        break;
      case LESS_THAN:
        whereString += "<";
        break;
      case LESS_THAN_OR_EQUAL:
        whereString += "<=";
        break;
      case GREATER_THAN:
        whereString += ">";
        break;
      case GREATER_THAN_OR_EQUAL:
        whereString += ">=";
        break;
      default:
        whereString += "=";
    }
    whereString += "'" + this._value + "'";
    return  whereString;
  }

  public String getField()
  {
    return this._field;
  }

  public WhereOperator getOperator()
  {
    return this._operator;
  }

  public String getValue()
  {
    return this._value;
  }
}
