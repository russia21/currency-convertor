package ru.sbrf.testcurrencyconverter.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/** PURPOSE -- this class is required for collecting all information about current currencies. */

@Root(name = "ValCurs")

public class AllCurrencies
{
  @ElementList(required = true, inline = true)
  private List<Currency> list;

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

  public List<Currency> getList()
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
   * @return -- built or found Currency object.
    */

  public Currency getCurrencyByCode(String code) {

    /* For rouble - build the currency from the scratch. */
    if ("RUB".equals(code)) {
      return new Currency(true);
    }

    /* Try to find the currency in the list. */
    for (Currency currency : getList()) {
      if (currency.getCharCode().equals (code)) {
        return currency;
      }
    }

    return null;
  }


  /**
   * PURPOSE -- function is used for conversion of money amount from input currency into the output
   *
   * @param value -- input -- the value to convert
   * @param inputCurrency  -- input -- currency From
   * @param outputCurrency -- input -- currency To
   *
   * @return -- RETURNS converted amount
   */

  public static double convert(double value, Currency inputCurrency, Currency outputCurrency)
  {
    // calculate
    double cursFromInToRouble = inputCurrency.getDoubleValue() / ((double) inputCurrency.getNominal());
    double cursFromRoubleToOut = ((double) outputCurrency.getNominal()) / outputCurrency.getDoubleValue();

    return value * cursFromInToRouble * cursFromRoubleToOut;
  }

  /**
   * PURPOSE -- method is used to clear list of currencies.
   */

  public void clear () {
    if (getList() != null && getList().size() > 0) {
      getList().clear();
    }
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AllCurrencies that = (AllCurrencies) o;

    if (list != null ? !list.equals(that.list) : that.list != null) return false;
    if (dateRefreshed != null ? !dateRefreshed.equals(that.dateRefreshed) : that.dateRefreshed != null)
      return false;
    return name != null ? name.equals(that.name) : that.name == null;

  }

  @Override
  public int hashCode()
  {
    int result = list != null ? list.hashCode() : 0;
    result = 31 * result + (dateRefreshed != null ? dateRefreshed.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    return result;
  }
}
