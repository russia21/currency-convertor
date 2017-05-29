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
  public void getValuteByCode() throws Exception
  {
    AllCurrencies allCurrencies = getAllCurrencies ();
    Valute valute = allCurrencies.getValuteByCode("AZN");
    assertEquals(valute.ms_valuteId, "R01020A");
  }

  @Test
  public void convert() throws Exception
  {
    AllCurrencies allCurrencies = getAllCurrencies ();
    Valute valute1 = allCurrencies.getValuteByCode("AZN");
    Valute valute2 = allCurrencies.getValuteByCode("AUD");
    assertEquals(799400.2245338791, AllCurrencies.convert(100500, valute1,valute2), 0.00001);

    assertEquals(799.4002245338791, AllCurrencies.convert(100.50, valute1,valute2), 0.00001);

    assertEquals(12634.785042610337, AllCurrencies.convert(100500, valute2,valute1), 0.00001);

    assertEquals(0.06285962, AllCurrencies.convert(0.50, valute2,valute1), 0.00001);
  }

}