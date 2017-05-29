package ru.sbrf.testcurrencyconverter.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Contians information about Currency. It used to be loaded by SimpleXML, so contains this frmwrk's annotations.
 */

@Root(name = "Valute")
public class Valute
{
  @Attribute(name = "ID")
  public String ms_valuteId;   // ex. ID="R01500"

  @Element(name = "NumCode")
  public int mj_valuteCode;    // ex. 498

  @Element(name = "CharCode")
  public String ms_charCode;   // ex. MDL

  @Element(name = "Nominal")
  public int j_nominal;        // ex. 10

  @Element(name = "Name")
  public String ms_name;       // ex. Moldavian lei

  public double md_value;      // ex. 30,7762

  public Valute(String rub)
  {
    // create rouble
    ms_valuteId ="R0000";
    mj_valuteCode = 0;
    ms_charCode = rub;
    j_nominal = 1;
    ms_name = "аѓсыќ ад";
    md_value = 1.0;
  }

  @Element(name = "Value")
  public void setValue (String value) {
    md_value = Double.parseDouble(value.replaceAll (",", "."));
  }

  @Element(name = "Value")
  public String getValue () {
    return "" + md_value;
  }

  public Valute () { }
}
