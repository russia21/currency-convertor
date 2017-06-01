package ru.sbrf.testcurrencyconverter.utils;

import android.content.Context;
import android.content.SharedPreferences;

/** PURPOSE -- class for save/recall preferences. */

public class PrefsHelper
{

  /** Preferences settings names. */
  private static final String SETTINGS_PREFERENCES = "ExchPrefs";

  public static final String SETTINGS_CURRENCIES_XML = "CurrenciesXML";
  public static final String SETTINGS_CURRENCY_FROM = "CurrencyFrom";
  public static final String SETTINGS_CURRENCY_TO = "CurrencyTo";

  /**
   * PURPOSE - get data from preferences.
   *
   * @param name          -- input -- name for data to save (SETTINGS_PREFERENCES_ constant)
   * @param context       --  input -- context
   * @param defaultValue  -- input -- default value
   *
   * @return  Stored data or null if preferences not created yet.
   */

  public static String getDataFromPrefs(String name, Context context, String  defaultValue) {

    SharedPreferences sharedPreferences  = context.getSharedPreferences(SETTINGS_PREFERENCES, Context.MODE_PRIVATE);

    if (sharedPreferences == null) {
      return null;
    }

    return sharedPreferences.getString(name, defaultValue);
  }

  /**
   * PURPOSE - stores data into private shared preferences.
   *
   * @param name    -- input -- name for data to save (S__STG_PREFERENCES_ constant)
   * @param value   -- input -- data to save
   * @param context -- input -- context
   *
   */

  public static void saveDataIntoPrefs(String name, String value, Context context)
  {
    SharedPreferences sharedPreferences  = context.getSharedPreferences(SETTINGS_PREFERENCES, Context.MODE_PRIVATE);

    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(name, value);

    editor.commit();
  }

}
