package ru.sbrf.testcurrencyconverter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

import ru.sbrf.testcurrencyconverter.ui.RefreshInterface;
import ru.sbrf.testcurrencyconverter.utils.PrefsHelper;

/**
 * GetCurrenciesAsyncTask class is used to get XML with currencies from http://www.cbr.ru/scripts/XML_daily.asp
 */

class GetCurrenciesAsyncTask extends AsyncTask<Object, Void, String>

{

  private final static String CBR_CURRENCIES_URL = "http://www.cbr.ru/scripts/XML_daily.asp";

  private Context storedContext;                 /* context for remembering caller's context */
  private RefreshInterface refreshInterface;     /* link to activity's functions for refreshing */
  private SwipeRefreshLayout swipeProgressCircle; /* link to Progress Circle */

  /**
   * Constructor.
   *
   * @param context             -- calling context
   * @param activity            -- activity where resides interface elements, which require refresh after load
   * @param swipeProgressCircle -- swipe progress circle
   */

  GetCurrenciesAsyncTask(Context context, RefreshInterface activity, SwipeRefreshLayout swipeProgressCircle) {
    Log.d ("exch","GetCurrenciesAsyncTask constructor");
    storedContext = context;
    refreshInterface = activity;
    this.swipeProgressCircle = swipeProgressCircle;
    Log.d ("exch","GetCurrenciesAsyncTask finished");
  }

  /**
   *  Pre-processing method. Works with GUI.
   */

  @Override
  protected void onPreExecute() {
    super.onPreExecute();

    /* Start showing progress circle. */
    if (swipeProgressCircle != null) {
      swipeProgressCircle.setRefreshing(true);
    }

    /* Set flag that we're in progress. */
    if (refreshInterface != null) {
      refreshInterface.setProgressFlag();
    }
  }

  /**
   *  Post-processing method. Works with GUI.
   */

  @Override
  protected void onPostExecute(String result) {
    super.onPostExecute(result);

    /* Stop showing progress circle. */
    if (swipeProgressCircle != null) {
      swipeProgressCircle.setRefreshing(false);
    }

    /* We've got error while refreshing. Do nothing. */
    if (result == null || result.length() == 0) {
      Toast.makeText(storedContext, R.string.refresh_failed, Toast.LENGTH_SHORT).show();
      return;
    }

    /* Put found result into preferences. */
    PrefsHelper.saveDataIntoPrefs(PrefsHelper.SETTINGS_CURRENCIES_XML, result, storedContext);

    /* Mark in activity that we've finished refresh. Show refreshed currencies in interface. */
    if (refreshInterface != null) {
      refreshInterface.removeProgressFlag();
      refreshInterface.refreshCurrenciesFromCache(result);
    }

    Toast.makeText(storedContext, R.string.refresh_finished, Toast.LENGTH_SHORT).show();

  }

  /**
   *  The processing by itself. Works in deep background.
   */

  @Override
  protected String doInBackground(Object... params)
  {
    return getCurrenciesXml(CBR_CURRENCIES_URL);
  }


  /**
   * Method is used to get currencies from http in xml. Works in background.
   *
   * @param url -- link to the script which generates XML with currencies.
   */

  private String getCurrenciesXml(String url) {

    Log.d("exch","AsyncTask. getCurrenciesXml started.");

    String responseBody = "";

    /* Call http get, obtain xml with currencies list. */

    try
    {
      HttpClient httpClient = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet (url);

      HttpResponse response;
      try
      {
        response = httpClient.execute (httpGet);
      }
      catch (Exception e)
      {
        Log.d ("exch", "AsyncTask. getCurrenciesXml connection error = " + e.getMessage ());
        return null;
      }

      HttpEntity entity = response.getEntity();

      byte buffer[] = new byte[51200];
      InputStream inputStream = entity.getContent() ;
      int numBytes;
      while ((numBytes = inputStream.read (buffer)) != -1)
      {
        Log.d ("exch", "AsyncTask. getCurrenciesXml read " + numBytes + " bytes.");
        String s_outBody = new String (buffer, 0, numBytes, "cp1251");
        responseBody = responseBody + s_outBody;
      }
      inputStream.close();

      Log.d("exch","AsyncTask. getCurrenciesXml bytes = " + numBytes  + ", s_responseBody = " + responseBody);
    }
    catch (ClientProtocolException e)
    {
      Log.d ("exch", "AsyncTask. getCurrenciesXml ClientProtocolException = " + e.getMessage ());
      return null;
    }
    catch (IOException e)
    {
      Log.d ("exch", "AsyncTask. getCurrenciesXml IOException = " + e.getMessage ());
      return null;
    }

    /* Trim leading/trailing junk. */
    return responseBody.trim();
  }

}  /* End OF GetCurrenciesAsyncTask */
