package ru.sbrf.testcurrencyconverter.utils;

import android.content.Context;
import android.content.SharedPreferences;

/** PURPOSE -- class for save/recall preferencies. */

public class PrefsHelper
{

  /** Preferences settings names. */
  private static final String S__STG_PREFERENCES = "ExchPrefs";

  public static final String S__STG_CURRENCIES_XML = "CurrenciesXML";
  public static final String S__STG_CURRENCY_FROM = "CurrencyFrom";
  public static final String S__STG_CURRENCY_TO = "CurrencyTo";

/*************************************************************************************************************
 * PURPOSE - get data from preferencies.
 *
 * @param s_name -- input -- name for data to save (S__STG_PREFERENCES_ constant)
 * @param o_context --  input -- context
 * @param defaultValue -- input -- default value
 *
 * @return  Stored token or empty string if nothing found.
 */

public static String s_getDataFromPrefs (String s_name, Context o_context, String  defaultValue)
{
  String s_tokenStored = "";

  SharedPreferences o_sharedPreferences  = o_context.getSharedPreferences(S__STG_PREFERENCES, Context.MODE_PRIVATE);

  if (o_sharedPreferences != null)
  {
    s_tokenStored = o_sharedPreferences.getString(s_name, defaultValue);
  }

  return s_tokenStored;
}

/*************************************************************************************************************
 * PURPOSE - get data from preferencies.
 *
 * @param s_name -- input -- name for data to save (S__STG_PREFERENCES_ constant)
 * @param o_context --  input -- context
 *
 * @return  Stored token or empty string if nothing found.
 */

public static String s_getDataFromPrefs (String s_name, Context o_context)
{
  return s_getDataFromPrefs (s_name, o_context, "");
}


/*************************************************************************************************************
 * PURPOSE - stores data into shared prefs.
 *
 * @param s_name -- input -- name for data to save (S__STG_PREFERENCES_ constant)
 * @param s_value -- input -- data to save
 * @param o_context -- input -- context
 *
 */

public static void v_saveDataIntoPrefs (String s_name, String s_value, Context o_context)
{
  SharedPreferences o_sharedPreferences  = o_context.getSharedPreferences(S__STG_PREFERENCES, Context.MODE_PRIVATE);

/* Save token and synchronization flag. */

  SharedPreferences.Editor o_editor = o_sharedPreferences.edit();
  o_editor.putString(s_name, s_value);
  o_editor.commit();
}

}
