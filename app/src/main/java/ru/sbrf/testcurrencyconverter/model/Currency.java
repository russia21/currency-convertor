package ru.sbrf.testcurrencyconverter.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.Objects;

/**
 * Class Currency contains information about Currency. It used to be loaded by SimpleXML,
 * so contains this framework's annotations.
 */

@Root(name = "Valute")
public class Currency
{
  private String currencyId;   // ex. ID="R01500"

  private int currencyCode;    // ex. 498

  private String charCode;   // ex. MDL

  private int nominal;        // ex. 10

  private String name;       // ex. Moldavian lei

  private double value;      // ex. 30,7762

  /** Constructors. */

  Currency() { }

  /**
   * Used for building Russian Rouble currency. This currency is absent in CBR currencies list.
   *
   * @param isRouble -- pass true to return rouble.
   */

  Currency(boolean isRouble)
  {
    if (isRouble) {
      // create rouble
      currencyId ="R0000";
      currencyCode = 0;
      charCode = "RUB";
      nominal = 1;
      name = "Ğóáëü ĞÔ";
      value = 1.0;
    }
  }

  /** Getters and setters. */

  @Element(name = "Value")
  public void setValue (String value) {
    this.value = Double.parseDouble(value.replaceAll (",", "."));
  }

  @Element(name = "Value")
  public String getValue () {
    return "" + value;
  }

  double getDoubleValue() {
    return value;
  }

  void setDoubleValue(double value) {
    this.value = value;
  }

  @Attribute(name = "ID")
  public void setCurrencyId (String currencyId) {
    this.currencyId = currencyId;   // ex. ID="R01500"
  }

  @Attribute(name = "ID")
  public String getCurrencyId () {
    return currencyId;
  }

  @Element(name = "NumCode")
  public void setCurrencyCode(int currencyCode) {
    this.currencyCode = currencyCode;   // ex. 498
  }

  @Element(name = "NumCode")
  public int getCurrencyCode() {
    return currencyCode;
  }

  @Element(name = "CharCode")
  public void setCharCode (String charCode) {
    this.charCode = charCode;   // ex. MDL
  }

  @Element(name = "CharCode")
  public String getCharCode () {
    return charCode;
  }

  @Element(name = "Nominal")
  public void setNominal (int nominal) {
    this.nominal = nominal;   // ex. 10
  }

  @Element(name = "Nominal")
  int getNominal() {
    return nominal;
  }

  @Element(name = "Name")
  public void setName (String name) {
    this.name = name;   // ex. Moldavian lei
  }

  @Element(name = "Name")
  public String getName () {
    return name;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Currency currency = (Currency) o;

    if (currencyCode != currency.currencyCode) return false;
    if (nominal != currency.nominal) return false;
    if (Double.compare(currency.value, value) != 0) return false;
    if (!currencyId.equals(currency.currencyId)) return false;
    if (!charCode.equals(currency.charCode)) return false;
    return name.equals(currency.name);

  }

  @Override
  public int hashCode()
  {
    int result;
    long temp;
    result = currencyId.hashCode();
    result = 31 * result + currencyCode;
    result = 31 * result + charCode.hashCode();
    result = 31 * result + nominal;
    result = 31 * result + name.hashCode();
    temp = Double.doubleToLongBits(value);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }
}
