package ru.sbrf.testcurrencyconverter.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Rem2 on 26.05.2017.
 */
public class ValuteTest
{
  @Test
  public void setValue() throws Exception
  {
    Valute valute = new Valute();
    valute.setValue("3");
    assertEquals (3, valute.md_value, 0.000001);
    valute.setValue("3.01");
    assertEquals (3.01, valute.md_value, 0.000001);
    valute.setValue("-.12");
    assertEquals (-0.12, valute.md_value, 0.000001);
    valute.setValue("3,12");
    assertEquals (3.12, valute.md_value, 0.000001);
  }

  @Test
  public void getValue() throws Exception
  {
    Valute valute = new Valute();
    valute.md_value = 0.12;
    assertEquals("0.12", valute.getValue());

    valute.md_value = 10.12;
    assertEquals("10.12", valute.getValue());

    valute.md_value = 10.0;
    assertEquals("10.0", valute.getValue());
  }

}