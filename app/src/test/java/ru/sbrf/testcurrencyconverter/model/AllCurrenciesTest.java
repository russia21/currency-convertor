package ru.sbrf.testcurrencyconverter.model;

import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import static org.junit.Assert.*;

/**
 * Created by Rem2 on 26.05.2017.
 */
public class AllCurrenciesTest
{

  static final String XML_EXAMPLE = "<ValCurs Date=\"27.05.2017\" name=\"Foreign Currency Market\">\n" +
    "        <Valute ID=\"R01010\">\n" +
    "        \t<NumCode>036</NumCode>\n" +
    "        \t<CharCode>AUD</CharCode>\n" +
    "        \t<Nominal>10</Nominal>\n" +
    "        \t<Name>Австралийский доллар</Name>\n" +
    "        \t<Value>42,2208</Value>\n" +
    "        </Valute>\n" +
    "        <Valute ID=\"R01020A\">\n" +
    "        \t<NumCode>944</NumCode>\n" +
    "        \t<CharCode>AZN</CharCode>\n" +
    "        \t<Nominal>1</Nominal>\n" +
    "        \t<Name>Азербайджанский манат</Name>\n" +
    "        \t<Value>33,5834</Value>\n" +
    "        </Valute> </ValCurs>";

  private AllCurrencies getAllCurrencies () throws Exception
  {
    Serializer ser = new Persister();
    AllCurrencies allCurrencies = ser.read(AllCurrencies.class, XML_EXAMPLE);
    return allCurrencies;
  }

  @Test
  public void getList() throws Exception
  {
    AllCurrencies allCurrencies = getAllCurrencies ();

    // check we have 2 currencies
    assertEquals(allCurrencies.getList().size(), 2);

  }

  @Test
  public void getDateRefreshed() throws Exception
  {
    AllCurrencies allCurrencies = getAllCurrencies ();

    assertEquals(allCurrencies.getDateRefreshed(), "27.05.2017");
  }

  @Test
  public void getCurrencyByCode() throws Exception
  {
    AllCurrencies allCurrencies = getAllCurrencies ();
    Currency currency = allCurrencies.getCurrencyByCode("AZN");
    assertEquals(currency.getCurrencyId(), "R01020A");
  }

  @Test
  public void convert() throws Exception
  {
    AllCurrencies allCurrencies = getAllCurrencies ();
    Currency currency1 = allCurrencies.getCurrencyByCode("AZN");
    Currency currency2 = allCurrencies.getCurrencyByCode("AUD");
    assertEquals(799400.2245338791, AllCurrencies.convert(100500, currency1, currency2), 0.00001);

    assertEquals(799.4002245338791, AllCurrencies.convert(100.50, currency1, currency2), 0.00001);

    assertEquals(12634.785042610337, AllCurrencies.convert(100500, currency2, currency1), 0.00001);

    assertEquals(0.06285962, AllCurrencies.convert(0.50, currency2, currency1), 0.00001);
  }


  @Test
  public void clear() throws Exception
  {
    AllCurrencies allCurrencies = getAllCurrencies ();
    allCurrencies.clear();
    assertEquals(allCurrencies.getList().size(), 0);
  }

}