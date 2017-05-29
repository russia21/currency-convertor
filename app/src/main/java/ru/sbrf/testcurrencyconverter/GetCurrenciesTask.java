package ru.sbrf.testcurrencyconverter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
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
 * PURPOSE -- class is used to get XML with currencies from http://www.cbr.ru/scripts/XML_daily.asp
 */

class GetCurrenciesTask extends AsyncTask<Object, Void, String>

{

  private final static String S_URL = "http://www.cbr.ru/scripts/XML_daily.asp";

  private Context mo_context;  // context for remembering activities' context
  private RefreshInterface mo_refreshInterface;     // link to activity's functions for refreshing
  private SwipeRefreshLayout mo_swipeRefreshLayout; // link to Progress Circle

  /*****************************************************************************/

  GetCurrenciesTask(
    Context o_context,            // входной и выходной -- контекст
    RefreshInterface o_activity,  // активность, в которой надо прорефрешить интерфейсные элементы после загрузки
    SwipeRefreshLayout o_swipeRefreshLayout // ссылка на прогресс
  )
  {
    Log.d ("exch","GetCurrenciesTask constructor");
    mo_context = o_context;
    mo_refreshInterface = o_activity;
    mo_swipeRefreshLayout = o_swipeRefreshLayout;
    Log.d ("exch","GetCurrenciesTask finished");
  }


  /*****************************************************************************/

  @Override
  protected void onPreExecute() {
    super.onPreExecute();

    // начинаем показывать прогресс
    if (mo_swipeRefreshLayout != null) {
      mo_swipeRefreshLayout.setRefreshing(true);
    }

    // даем понять приложению, что мы находимся в состоянии прогресса
    if (mo_refreshInterface != null) {
      mo_refreshInterface.setProgressFlag();
    }
  }

  /*****************************************************************************/

  @Override
  protected void onPostExecute(String result) {
    super.onPostExecute(result);

    // выключаем прогресс
    if (mo_swipeRefreshLayout != null) {
      mo_swipeRefreshLayout.setRefreshing(false);
    }

    if (result == null || result.length() == 0) {
      return;
    }

    // put found result into preferencies
    PrefsHelper.v_saveDataIntoPrefs(PrefsHelper.S__STG_CURRENCIES_XML, result, mo_context);

    // в активности отметим, что мы больше не в состоянии рефреша, и обновим валюты
    if (mo_refreshInterface != null) {
      mo_refreshInterface.removeProgressFlag();
      mo_refreshInterface.refreshCurrencies(result);
    }

    Toast.makeText(mo_context, R.string.refresh_finished, Toast.LENGTH_SHORT).show();

  }

  /*****************************************************************************/

  @Override
  protected String doInBackground(Object... params)
  {
    try
    {
      return s_getXmlWithCurrencies (S_URL);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  /*******************************************************************************/

  private String s_getXmlWithCurrencies(String s_url) {

    Log.d("exch","AsyncTask. s_getXmlWithCurrencies started.");

    String s_responseBody = "";

    /* разрешим выполнить запрос в UI режиме - надо убрать */
    StrictMode.ThreadPolicy o_policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    Log.d ("exch", "AsyncTask. s_getXmlWithCurrencies before set thread policy");
    StrictMode.setThreadPolicy (o_policy);

    /* ВЫЗЫВАЕМ HTTP. ПОЛУЧАЕМ XML. */

    try
    {
      HttpClient o_httpClient = new DefaultHttpClient();
      HttpGet o_httpGet = new HttpGet (s_url);

      HttpResponse o_response;
      try
      {
        o_response = o_httpClient.execute (o_httpGet);
      }
      catch (Exception e)
      {
        Log.d ("exch", "AsyncTask. s_getXmlWithCurrencies connection error = " + e.getMessage ());
        return null;
      }

      HttpEntity entity = o_response.getEntity();

      byte buffer[] = new byte[51200];
      InputStream o_inputStream = entity.getContent() ;
      int j_numBytes;
      while ((j_numBytes = o_inputStream.read (buffer)) != -1)
      {
        Log.d ("exch", "AsyncTask. s_getXmlWithCurrencies read " + j_numBytes + " bytes.");
        String s_outBody = new String (buffer, 0, j_numBytes, "cp1251");
        s_responseBody = s_responseBody + s_outBody;
      }
      o_inputStream.close();

      Log.d("exch","AsyncTask. s_getXmlWithCurrencies bytes = " + j_numBytes  + ", s_responseBody = " + s_responseBody);
    }
    catch (ClientProtocolException e)
    {
      Log.d ("exch", "AsyncTask. s_getXmlWithCurrencies ClientProtocolException = " + e.getMessage ());
      return null;
    }
    catch (IOException e)
    {
      Log.d ("exch", "AsyncTask. s_getXmlWithCurrencies IOException = " + e.getMessage ());
      return null;
    }

    // trim leading/trailing junk
    return s_responseBody.trim();
  }


  /*******************************************************************************/

}  /* End OF GetCurrenciesTask */
