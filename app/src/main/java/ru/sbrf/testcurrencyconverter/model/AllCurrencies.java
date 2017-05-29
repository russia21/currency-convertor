package ru.sbrf.testcurrencyconverter.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/** PURPOSE -- this class is required for collecting all information about current currencies. */

@Root(name = "ValCurs")

public class AllCurrencies
{
  @ElementList(required = true, inline = true)
  private List<Valute> list;

  @Attribute(required = false, name = "Date")
  private String dateRefreshed;

  @Attribute(required = false)
  private String name;

  /**
   * PURPOSE - the Constructor
   */

  public AllCurrencies()
  {
    this.list = new ArrayList<>();
  }

  /** Getters and misc **/

  public List<Valute> getList()
  {
    return list;
  }

  public String getDateRefreshed () { return dateRefreshed; }

  @Override
  public String toString()
  {
    return "AllCurrencies{" + "list=" + list + '}';
  }

  /**
   * PURPOSE -- function is used to get currency by string code like "USD" or "RUB". Function builds
   * Rouble currency because it's absent in CBR XML.
   *
   * @param code -- input -- currency code like "USD" or "RUB"
   *
   * @return -- built or found Valute object.
    */

  public Valute getValuteByCode (String code) {

    // for rouble - build the currency from the scratch
    if (code.equals("RUB")) {
      return new Valute ("RUB");
    }

    // try to find the currency in the list
    for (Valute valute : getList()) {
      if (valute.ms_charCode.equals (code)) {
        return valute;
      }
    }

    return null;
  }


  /**
   * PURPOSE -- function is used for conversion of money amount from input currency into the output
   *
   * @param value -- input -- the value to convert
   * @param inputValute  -- input -- currency From
   * @param outputValute -- input -- currency To
   *
   * @return -- RETURNS converted amount
   */

  public static double convert(double value, Valute inputValute, Valute outputValute)
  {
    // calculate
    double cursFromInToRouble = inputValute.md_value / ((double) inputValute.j_nominal);
    double cursFromRoubleToOut = ((double) outputValute.j_nominal) / outputValute.md_value;

    return value * cursFromInToRouble * cursFromRoubleToOut;
  }
}
