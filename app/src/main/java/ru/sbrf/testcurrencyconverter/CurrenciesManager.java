package ru.sbrf.testcurrencyconverter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.util.ArrayList;
import java.util.List;

import ru.sbrf.testcurrencyconverter.model.AllCurrencies;
import ru.sbrf.testcurrencyconverter.model.Currency;
import ru.sbrf.testcurrencyconverter.ui.RefreshInterface;

/**
 * Class performs controlling operations between GUI and model.
 */

public class CurrenciesManager
{
  private static final String TAG = "exch";

  private static AllCurrencies allCurrencies;  /* List of all currencies. */

  /**
   * Method is used to load currencies from web.
   *
   * @param context            -- input and output - context
   * @param activity           -- the activity which we need to refresh after currencies load
   * @param showProgress       -- true if we need to show swipe progress layout
   * @param swipeRefreshLayout -- the swipe progress circle
   */

  static public void loadCurrenciesFromWeb (Context context, RefreshInterface activity,
                                            boolean showProgress, SwipeRefreshLayout swipeRefreshLayout) {


    Log.d(TAG,"loadCurrenciesFromWeb started.");

    GetCurrenciesAsyncTask getCurrenciesAsyncTask = new GetCurrenciesAsyncTask (context, activity, showProgress ? swipeRefreshLayout : null);

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
    {
      getCurrenciesAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
      Log.d(TAG,"loadCurrenciesFromWeb launched ON EXECUTOR.");
    }
    else
    {
      getCurrenciesAsyncTask.execute ();
      Log.d(TAG,"loadCurrenciesFromWeb launched.");
    }
  }


  /**
   * Method is used to fill currencies list.
   *
   * @param xml -- xml with currencies
   */

  static boolean buildAllCurrenciesList (String xml) {

    /* Check xml. */
    if (xml == null || xml.length() < 100) {
      /* Show error message. */
      throw new IllegalArgumentException();
    }


    /* Clear previous array first. */
    if (allCurrencies != null) {
      allCurrencies.clear();
    }

    /* Populate allCurrencies with new data. */

    try
    {
      Serializer ser = new Persister();
      allCurrencies = ser.read(AllCurrencies.class, xml);

      // purpose - debug only

      //Log.d(TAG, "MainActivity. refreshCurrenciesFromCache list all: ================================");
      //for (Currency valute : allCurrencies.getList()) {
      //  Log.d(TAG, "refreshCurrenciesFromCache: " + valute.getCharCode() + ", value = " + valute.getValue() +
      //    ", name = " + valute.getName());
      //}

      return true;

    } catch (Exception e)
    {
      Log.d(TAG, "buildAllCurrenciesList: failed to build allCurrencies from xml. " + e.getMessage());
    }
    return false;
  }


  /**
   * Method checks whether currencies are loaded.
   *
   * @return true if all currencies are loaded.
   */

  public static boolean areCurrenciesLoaded () {
    return ((allCurrencies != null) && (allCurrencies.getList() != null) && (allCurrencies.getList().size() > 0));
  }

  /**
   * Method returns date, when the currencies are refreshed.
   *
   * @return - refresh date
   */

  public static String getDateRefreshed () {

    if (!areCurrenciesLoaded()) {
      return null;
    }

    return allCurrencies.getDateRefreshed();
  }

  /**
   * PURPOSE -- function is used to get currency by string code like "USD" or "RUB". Function builds
   * Rouble currency because it's absent in CBR XML.
   *
   * @param code -- input -- currency code like "USD" or "RUB"
   *
   * @return -- built or found Currency object.
   */

  public static Currency getCurrencyByCode(String code) {

    if (!areCurrenciesLoaded()) {
      return null;
    }

    return allCurrencies.getCurrencyByCode(code);
  }


  /**
   * PURPOSE -- method is used to return list of all currencies.
   *
   * @return -- List of Currencies
   */

  public static List<Currency> getList() {
    return allCurrencies.getList();
  }
}
