package ru.sbrf.testcurrencyconverter.model;

import org.junit.Test;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.junit.Assert.*;


public class CurrencyTest
{

  @Test
  public void getDoubleValue() throws Exception
  {
    Currency currency = new Currency();
    currency.setDoubleValue(0.25);
    assertEquals(0.25, currency.getDoubleValue(), 0.000001);

    currency.setDoubleValue(10.123333);
    assertEquals(10.123333, currency.getDoubleValue(), 0.000001);

    currency.setDoubleValue(10);
    assertEquals(10, currency.getDoubleValue(), 0.000001);
  }


  @Test
  public void setCurrencyId() throws Exception
  {
    Currency currency = new Currency();
    currency.setCurrencyId("R01500");
    assertEquals("R01500", currency.getCurrencyId());
  }

  @Test
  public void setCurrencyCode() throws Exception
  {
    Currency currency = new Currency();
    currency.setCurrencyCode(498);
    assertEquals(498, currency.getCurrencyCode());
  }

  @Test
  public void setCharCode() throws Exception
  {
    Currency currency = new Currency();
    currency.setCharCode("MDL");
    assertEquals("MDL", currency.getCharCode());
  }

  @Test
  public void setNominal() throws Exception
  {
    Currency currency = new Currency();
    currency.setNominal(10);
    assertEquals(10, currency.getNominal());
  }

  @Test
  public void setName() throws Exception
  {
    Currency currency = new Currency();
    currency.setName("Moldavian lei");
    assertEquals("Moldavian lei", currency.getName());
  }

  @Test
  public void setValue() throws Exception
  {
    Currency currency = new Currency();
    currency.setValue("3");
    assertEquals (3, currency.getDoubleValue(), 0.000001);
    currency.setValue("3.01");
    assertEquals (3.01, currency.getDoubleValue(), 0.000001);
    currency.setValue("-.12");
    assertEquals (-0.12, currency.getDoubleValue(), 0.000001);
    currency.setValue("3,12");
    assertEquals (3.12, currency.getDoubleValue(), 0.000001);
  }

  @Test
  public void getValue() throws Exception
  {
    Currency currency = new Currency();
    currency.setDoubleValue(0.12);
    assertEquals(0.12, currency.getDoubleValue(), 0.000001);

    currency.setDoubleValue(10.12);
    assertEquals(10.12, currency.getDoubleValue(), 0.000001);

    currency.setDoubleValue(10.0);
    assertEquals("10.0", currency.getValue());
  }


}