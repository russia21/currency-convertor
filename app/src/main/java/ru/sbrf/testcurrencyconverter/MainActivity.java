package ru.sbrf.testcurrencyconverter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import ru.sbrf.testcurrencyconverter.model.AllCurrencies;
import ru.sbrf.testcurrencyconverter.model.Valute;
import ru.sbrf.testcurrencyconverter.ui.CurrencySelectorActivity;
import ru.sbrf.testcurrencyconverter.ui.CustomDigitsKeyListener;
import ru.sbrf.testcurrencyconverter.ui.RefreshInterface;
import ru.sbrf.testcurrencyconverter.utils.InetHelper;
import ru.sbrf.testcurrencyconverter.utils.PrefsHelper;

/**
 * Main activity, used for showing conversion rates.
 */
public class MainActivity extends AppCompatActivity implements RefreshInterface, SwipeRefreshLayout.OnRefreshListener {

  private static final String TAG = "exch";
  private static final int REQUEST_CODE_CHOOSE_VALUTE_FROM = 100;
  private static final int REQUEST_CODE_CHOOSE_VALUTE_TO   = 101;

  private String ms_valuteCodeFrom = "RUB";
  private String ms_valuteCodeTo = "USD";

  private boolean mb_refreshInProgress = true;

  private AllCurrencies allCurrencies;  // list of all currencies

/**********************************************************************************************************************
*  PURPOSE - onCreate
*/

@Override
protected void onCreate(Bundle savedInstanceState)
{
  super.onCreate(savedInstanceState);

  Log.d("exch", "MainActivity. onCreate");

  /* INTERFACE BLOCK */

  setContentView(R.layout.activity_main);

  Toolbar topToolBar = (Toolbar)findViewById(R.id.toolbar);
  topToolBar.setTitle(R.string.app_name);

  // set good keyboard for Value
  TextView o_valueTx = (TextView)findViewById(R.id.value);
  o_valueTx.setKeyListener(new CustomDigitsKeyListener(true,true));

  // set Swipe listener
  SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
  swipeRefreshLayout.setOnRefreshListener(this);

  /* DATA INITIALIZATION BLOCK */

  // refresh currencies
  Log.d("exch", "MainActivity. Init location manager");
  reloadCurrencies (false);

  // let's load cached currencies here
  refreshCurrencies (null);


  /* FILL SPINNERS */

  //load currencies from prefs
  ms_valuteCodeFrom = PrefsHelper.s_getDataFromPrefs (PrefsHelper.S__STG_CURRENCY_FROM, this, "RUB");
  ms_valuteCodeTo = PrefsHelper.s_getDataFromPrefs (PrefsHelper.S__STG_CURRENCY_TO, this, "USD");

  v_spinnerFill (R.id.valuteFrom, ms_valuteCodeFrom);
  v_spinnerFill (R.id.valuteTo, ms_valuteCodeTo);

  Spinner spinnerFrom = (Spinner) findViewById(R.id.valuteFrom);
  spinnerFrom.setOnTouchListener(new View.OnTouchListener()
  {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
      if (event.getAction() == MotionEvent.ACTION_UP && allCurrencies != null && allCurrencies.getList() != null && allCurrencies.getList().size() > 0) {
        Intent o_intentNew = new Intent (getApplicationContext (), CurrencySelectorActivity.class);
        Log.d("exch", "start intent on v_calcPushButtonHook");
        startActivityForResult (o_intentNew, REQUEST_CODE_CHOOSE_VALUTE_FROM);
      }
      return false;
    }
  });

  Spinner spinnerTo = (Spinner) findViewById(R.id.valuteTo);
  spinnerTo.setOnTouchListener(new View.OnTouchListener()
  {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
      if (event.getAction() == MotionEvent.ACTION_UP && allCurrencies != null && allCurrencies.getList() != null && allCurrencies.getList().size() > 0) {
        Intent o_intentNew = new Intent (getApplicationContext (), CurrencySelectorActivity.class);
        Log.d("exch", "start intent on v_calcPushButtonHook");
        startActivityForResult (o_intentNew, REQUEST_CODE_CHOOSE_VALUTE_TO);
      }
      return false;
    }
  });

  // show warning message if cache is empty and there is no connection
  areCurrenciesLoaded(true);

}

/**********************************************************************************************************************/

private BroadcastReceiver o_receiver = new BroadcastReceiver() {
  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d(TAG, "onReceive: started");
    if ((allCurrencies == null || allCurrencies.getList() == null || allCurrencies.getList().size() == 0) &&
    InetHelper.b_isOnline (context)) {
      reloadCurrencies (true);
    }
  }
};

/**********************************************************************************************************************/

@Override
protected void onResume ()
{
  super.onResume();

  if (!areCurrenciesLoaded(false)) {
    // subscribe for internet activation
    this.registerReceiver(o_receiver,  new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
  }

  Log.d("exch", "MainActivity. onResume.");
}

/**********************************************************************************************************************/

@Override
protected void onPause ()
{
  super.onPause();

  try
  {
    /* Unregister receiver which works only when time required sync. */
    unregisterReceiver (o_receiver);
  }
  catch(IllegalArgumentException e) { }

}

/**********************************************************************************************************************/

@Override
public void onSaveInstanceState(Bundle savedInstanceState)
{
  Log.d("exch", "onSaveInstanceState started");
  super.onSaveInstanceState(savedInstanceState);
  // Save UI state changes to the savedInstanceState.
  // This bundle will be passed to onCreate if the process is
  // killed and restarted.
  savedInstanceState.putString("ms_valuteCodeFrom", ms_valuteCodeFrom);
  savedInstanceState.putString("ms_valuteCodeTo", ms_valuteCodeTo);

  Log.d("exch", "onSaveInstanceState finished");
}


/**********************************************************************************************************************/

@Override
public void onRestoreInstanceState(Bundle savedInstanceState)
{
  Log.d("exch", "onRestoreInstanceState started");
  super.onRestoreInstanceState(savedInstanceState);
  // Restore UI state from the savedInstanceState.
  // This bundle has also been passed to onCreate.
  ms_valuteCodeFrom = savedInstanceState.getString("ms_valuteCodeFrom");
  ms_valuteCodeTo = savedInstanceState.getString("ms_valuteCodeTo");

  // check that main currencies are still alive
  if (allCurrencies == null || allCurrencies.getList() == null || allCurrencies.getList().size() == 0) {
    refreshCurrencies (null);
  }

  Log.d("exch", "onRestoreInstanceState finished");
}


/**********************************************************************************************************************/

@Override
public void setProgressFlag () {
  // we're in refreshing process
  mb_refreshInProgress = true;
}

/**********************************************************************************************************************/

@Override
public void removeProgressFlag () {
  // we're not more in refreshing process any more
  mb_refreshInProgress = false;
}

/**********************************************************************************************************************/

@Override
public void refreshCurrencies(String s_xml)
{
  Log.d("exch", "MainActivity. refreshCurrencies started");

  // заканчиваем показывать прогресс
  SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
  if (swipeRefreshLayout != null) {
    swipeRefreshLayout.setRefreshing(false);
  }

  /* Get XML if not passed. */

  if (s_xml == null) {
    s_xml = PrefsHelper.s_getDataFromPrefs(PrefsHelper.S__STG_CURRENCIES_XML, this);
  }

  /* Check xml. */

  if (s_xml == null || s_xml.length() < 100) {
    // show error message
    Toast.makeText(this, R.string.refresh_failed, Toast.LENGTH_SHORT).show();
    return;
  }

  /* Clear previous array first. */
  if (allCurrencies != null && allCurrencies.getList() != null && allCurrencies.getList().size() > 0) {
    allCurrencies.getList().clear();
  }

  /* Populate allCurrencies with new data. */

  Serializer ser = new Persister();
  try
  {
    allCurrencies = ser.read(AllCurrencies.class, s_xml);

    // show actual date for Currencies
    v_setSubtitle (allCurrencies.getDateRefreshed());

    // purpose - debug only

    //Log.d("exch", "MainActivity. refreshCurrencies list all: ================================");
    //for (Valute valute : allCurrencies.getList()) {
    //  Log.d(TAG, "refreshCurrencies: " + valute.ms_charCode + ", value = " + valute.md_value + ", name = " + valute.ms_name);
    //}

  } catch (Exception e)
  {
    e.printStackTrace();
  }
}

/**********************************************************************************************************************
 * PURPOSE -- reloads currencies from web.
 *
 */

private void reloadCurrencies(boolean showProgress)
{

  Log.d("exch","reloadCurrencies mo_asyncTask start +++++++++++++++++++++++++++++++++++++++++++++.");

  /* Find resfresh. */
  SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

  // check that internet is available
  if (!InetHelper.b_isOnline(this)) {

    // stop swipe progress bar
    swipeRefreshLayout.setRefreshing (false);
    return;
  }

  GetCurrenciesTask mo_asyncTask = new GetCurrenciesTask (this, this, showProgress? swipeRefreshLayout : null);
  Log.d("exch","reloadCurrencies mo_asyncTask initialzed.");

  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
  {
    mo_asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    Log.d("exch","reloadCurrencies mo_asyncTask launched ON EXECUTOR.");
  }
  else
  {
    mo_asyncTask.execute ();
    Log.d("exch","reloadCurrencies mo_asyncTask launched.");
  }

}
/**********************************************************************************************************************/

@Override
public void onRefresh()
{
  Log.d(TAG, "onRefresh: started");

  // говорим о том, что собираемся начать
  Toast.makeText(this, R.string.refresh_started, Toast.LENGTH_SHORT).show();

  // refresh the Currencies
  reloadCurrencies (true);

}
/**********************************************************************************************************************/

public void v_calcPushButtonHook(View view)
{

  /* Show warning message if cache is empty and there is no connection. */
  if (!areCurrenciesLoaded(true)) {
    return;
  }

  TextView o_resultText = (TextView) findViewById(R.id.result);

  Log.d("exch", "start intent on v_calcPushButtonHook");

  /* Search for input Valute. */

  Valute inputValute = allCurrencies.getValuteByCode(ms_valuteCodeFrom);
  Valute outputValute = allCurrencies.getValuteByCode(ms_valuteCodeTo);

  /* Get input value. */

  EditText o_editText = (EditText) findViewById(R.id.value);
  if (o_editText == null || o_editText.getText() == null || o_editText.getText().toString().length() == 0) {
    o_resultText.setText ("");
    return;
  }
  double value = Double.parseDouble(o_editText.getText().toString().replaceAll(",", "."));

  /* Calculate it! */

  double result = AllCurrencies.convert (value, inputValute, outputValute);

  // put result into output text
  if (o_resultText != null) {
    o_resultText.setText (String.format("%.2f", value) + " " + inputValute.ms_charCode + " = " + String.format("%.2f", result) + " " + outputValute.ms_charCode);
  }

  //save currencies into prefs
  PrefsHelper.v_saveDataIntoPrefs(PrefsHelper.S__STG_CURRENCY_FROM, ms_valuteCodeFrom, this);
  PrefsHelper.v_saveDataIntoPrefs(PrefsHelper.S__STG_CURRENCY_TO, ms_valuteCodeTo, this);
}

/**********************************************************************************************************************/

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {

  Log.d("exch", "requestCode = " + requestCode + ", resultCode = " + resultCode);

  String s_code;

  // если пришло ОК
  if (resultCode == RESULT_OK) {

    switch (requestCode) {

      case REQUEST_CODE_CHOOSE_VALUTE_FROM:
        s_code = data.getStringExtra("code");
        ms_valuteCodeFrom = s_code;
        v_spinnerFill (R.id.valuteFrom, s_code);
        break;

      case REQUEST_CODE_CHOOSE_VALUTE_TO:
        s_code = data.getStringExtra("code");
        ms_valuteCodeTo = s_code;
        v_spinnerFill (R.id.valuteTo, s_code);
        break;

    }
    // если вернулось не ОК, то ничего не делаем
  }
}

/**********************************************************************************************************************/

public void v_spinnerFill(int spinnerId, String valuteCode)
{
  List<String> spinnerArray =  new ArrayList<String>();
  String displayName = null;

  // build item from russian currency name + currency code

  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && !valuteCode.equals("CNY")  && !valuteCode.equals("XDR"))
  {
    try {
      Currency curr = Currency.getInstance(valuteCode);
      Locale myLocale = new Locale("ru","RU");
      displayName = curr.getDisplayName (myLocale) + " (" + valuteCode + ")";
    }
    catch (IllegalArgumentException e) {
      Log.d(TAG, "v_spinnerFill: Exception while trying to get name for valute " + valuteCode);
    }
  }
  else if (valuteCode.equals("CNY")) {
    displayName = getResources().getString (R.string.name_CNY) + " (" + valuteCode + ")";
  }

  if (displayName == null || displayName.length() == 0) {
    displayName = allCurrencies.getValuteByCode(valuteCode).ms_name + " (" + valuteCode + ")";
  }

  spinnerArray.add (displayName);

  // create adapter - we use GUI here just for logical reasons

  ArrayAdapter<String> adapter = new ArrayAdapter<String>(
    this, android.R.layout.simple_spinner_item, spinnerArray);

  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
  Spinner sItems = (Spinner) findViewById(spinnerId);
  sItems.setAdapter(adapter);
}
/**********************************************************************************************************************/

public void v_setSubtitle (String valuteRefreshDate)
{
  Toolbar topToolBar = (Toolbar)findViewById(R.id.toolbar);
  topToolBar.setSubtitle(getResources().getString (R.string.curs_CB) + " " + valuteRefreshDate);
}

/**********************************************************************************************************************
 * PURPOSE -- function checks whether all currencies are loaded
 *
 * @param showMessage -- set to true when you want to show messages
 *
 * @return -- RETURNS true when there are some loaded currencies
 */

private boolean areCurrenciesLoaded (boolean showMessage)
{
  Log.d(TAG, "areCurrenciesLoaded: started");

  if (allCurrencies != null && allCurrencies.getList() != null && allCurrencies.getList().size() > 0) {
    Log.d(TAG, "areCurrenciesLoaded: true");
    return true;
  }

  // exit if we don't wanna to show messages
  if (!showMessage) {
    return false;
  }

  if (!InetHelper.b_isOnline(this)) {
    InetHelper.b_turnInternetOn (getResources().getString (R.string.turn_on_internet), this);
    return false;
  }

  if (mb_refreshInProgress) {
    InetHelper.b_turnInternetOn (getResources().getString (R.string.please_wait_refresh), this);
  }

  Log.d(TAG, "areCurrenciesLoaded: false");

  return false;
}

}

