package ru.sbrf.testcurrencyconverter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.sbrf.testcurrencyconverter.model.AllCurrencies;
import ru.sbrf.testcurrencyconverter.model.Currency;
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

  private String currencyCodeFrom = "RUB";
  private String currencyCodeTo = "USD";

  private boolean refreshInProgress = true;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "MainActivity. onCreate started");

    /* Interface block. */

    setContentView(R.layout.activity_main);

    Toolbar topToolBar = (Toolbar)findViewById(R.id.toolbar);
    topToolBar.setTitle(R.string.app_name);

    /* Set good keyboard for Amount. */
    TextView valueText = (TextView)findViewById(R.id.amount);
    valueText.setKeyListener(new CustomDigitsKeyListener(true,true));

    /* Set Swipe listener. */
    SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
    swipeRefreshLayout.setOnRefreshListener(this);

    /* DATA INITIALIZATION BLOCK */

    /* Refresh currencies. */
    reloadCurrenciesFromWeb(false);

    /* Let's load cached currencies here. */
    refreshCurrenciesFromCache(null);

    /* FILL SPINNERS */

    /* Load currencies from prefs. */
    currencyCodeFrom = PrefsHelper.getDataFromPrefs(PrefsHelper.SETTINGS_CURRENCY_FROM, this, "RUB");
    currencyCodeTo = PrefsHelper.getDataFromPrefs(PrefsHelper.SETTINGS_CURRENCY_TO, this, "USD");

    populateSpinner(R.id.valuteFrom, currencyCodeFrom);
    addSpinnerListener (R.id.valuteFrom, REQUEST_CODE_CHOOSE_VALUTE_FROM);

    populateSpinner(R.id.valuteTo, currencyCodeTo);
    addSpinnerListener (R.id.valuteTo, REQUEST_CODE_CHOOSE_VALUTE_TO);

    /* Show warning message if cache is empty and there is no connection. */
    warnWhenCurrenciesNotLoaded();

  }

  /**
   * Receiver, which starts working when new internet connection is started.
   */

  private BroadcastReceiver receiverOnNewConnection = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      Log.d(TAG, "onReceive: started");
      if (!CurrenciesManager.areCurrenciesLoaded() && InetHelper.isOnline(context)) {
        reloadCurrenciesFromWeb(true);
      }
    }
  };


  @Override
  protected void onResume ()
  {
    super.onResume();

    if (!CurrenciesManager.areCurrenciesLoaded()) {
      // subscribe for internet activation
      this.registerReceiver(receiverOnNewConnection,  new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    Log.d(TAG, "MainActivity. onResume.");
  }


  @Override
  protected void onPause ()
  {
    super.onPause();

    try
    {
      /* Unregister receiver which works only when time required sync. */
      unregisterReceiver (receiverOnNewConnection);
    }
    catch(IllegalArgumentException e) {
      Log.d ("exch", "onPause. Expected exception, do nothing: " + e.getMessage ());
    }

  }

  /**
   * Save and recall global variables when Activity is killed.
   */

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState)
  {
    super.onSaveInstanceState(savedInstanceState);

    /* Save UI state changes to the savedInstanceState. */
    savedInstanceState.putString("currencyCodeFrom", currencyCodeFrom);
    savedInstanceState.putString("currencyCodeTo", currencyCodeTo);
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState)
  {
    super.onRestoreInstanceState(savedInstanceState);

    /* Restore UI state from the savedInstanceState. */
    currencyCodeFrom = savedInstanceState.getString("currencyCodeFrom");
    currencyCodeTo = savedInstanceState.getString("currencyCodeTo");

    /* Check that main currencies are still alive. */
    if (!CurrenciesManager.areCurrenciesLoaded()) {
      refreshCurrenciesFromCache(null);
    }
  }


  /**
   * Controls refreshing progress.
   */

  @Override
  public void setProgressFlag () {
    /* We're in refreshing process. */
    refreshInProgress = true;
  }

  @Override
  public void removeProgressFlag () {
    /* We're not more in refreshing process any more. */
    refreshInProgress = false;
  }

  /**
   * Method is used for refreshing currencies from information stored in prefs.
   *
   * @param xml -- xml with currencies list. It can be null, then we take it from prefs.
   */

  @Override
  public void refreshCurrenciesFromCache(String xml)
  {
    boolean refreshed = false;
    Log.d(TAG, "MainActivity. refreshCurrenciesFromCache started");

    /* Stop showing progress. */
    SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
    if (swipeRefreshLayout != null) {
      swipeRefreshLayout.setRefreshing(false);
    }

    /* Get XML if not passed. */
    if (xml == null) {
      xml = PrefsHelper.getDataFromPrefs(PrefsHelper.SETTINGS_CURRENCIES_XML, this, null);
    }

    /* Build list with all currencies. */
    try {
      refreshed = CurrenciesManager.buildAllCurrenciesList (xml);
    } catch (IllegalArgumentException e) {
      /* Show error message. */
      Toast.makeText(this, getResources().getString (R.string.refresh_failed), Toast.LENGTH_SHORT).show();
      return;
    }

    if (refreshed) {
      /* Show actual date for Currencies. */
      setActionBarSubtitle(CurrenciesManager.getDateRefreshed());
    }
  }

  /**
   * Method is called on swipe refresh.
   */

  @Override
  public void onRefresh()
  {
    Log.d(TAG, "onRefresh: started");

    /* Show "Refresh started" message. */
    Toast.makeText(this, getResources().getString (R.string.refresh_started), Toast.LENGTH_SHORT).show();

    /* Refresh the Currencies. */
    reloadCurrenciesFromWeb(true);

  }

  /**
   * PURPOSE -- reloads currencies from web.
   *
   * @param showProgress       -- true if we need to show swipe progress layout
   */

  private void reloadCurrenciesFromWeb(boolean showProgress)
  {
    /* Find resfresh progress circle. */
    SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

    /* Check that internet is available. */
    if (!InetHelper.isOnline(this)) {
      /* Stop swipe progress bar. */
      swipeRefreshLayout.setRefreshing (false);
      return;
    }

    CurrenciesManager.loadCurrenciesFromWeb (this, this, showProgress, swipeRefreshLayout);
  }


  /**
   * Method is called, when user presses "Calculate" button.
   *
   * @param view -- the Calculate button.
   */

  public void onCalculateButtonPressed(View view)
  {

    /* Show warning message if cache is empty and there is no connection. */
    if (!CurrenciesManager.areCurrenciesLoaded()) {
      warnWhenCurrenciesNotLoaded();
      return;
    }

    TextView resultText = (TextView) findViewById(R.id.result);

    /* Search for input Currency. */
    Currency inputCurrency = CurrenciesManager.getCurrencyByCode(currencyCodeFrom);
    Currency outputCurrency = CurrenciesManager.getCurrencyByCode(currencyCodeTo);

    /* Get input value. Quit when not filled. */
    EditText editText = (EditText) findViewById(R.id.amount);
    if (editText == null || editText.getText() == null || editText.getText().toString().length() == 0) {
      resultText.setText ("");
      return;
    }
    double value = Double.parseDouble(editText.getText().toString().replaceAll(",", "."));

    /* Calculate it! */
    double result = AllCurrencies.convert (value, inputCurrency, outputCurrency);

    /* Put result into output text. */
    if (resultText != null) {
      resultText.setText (String.format("%.2f", value) + " " + inputCurrency.getCharCode() + " = " + String.format("%.2f", result) + " " + outputCurrency.getCharCode());
    }

    /* Save currencies into prefs. */
    PrefsHelper.saveDataIntoPrefs(PrefsHelper.SETTINGS_CURRENCY_FROM, currencyCodeFrom, this);
    PrefsHelper.saveDataIntoPrefs(PrefsHelper.SETTINGS_CURRENCY_TO, currencyCodeTo, this);
  }

  /**
   * Method processes responce from currency selector.
   */

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    String s_code;

    /* When Ok arrived... */
    if (resultCode == RESULT_OK) {

      switch (requestCode) {

        case REQUEST_CODE_CHOOSE_VALUTE_FROM:
          s_code = data.getStringExtra("code");
          currencyCodeFrom = s_code;
          populateSpinner(R.id.valuteFrom, s_code);
          break;

        case REQUEST_CODE_CHOOSE_VALUTE_TO:
          s_code = data.getStringExtra("code");
          currencyCodeTo = s_code;
          populateSpinner(R.id.valuteTo, s_code);
          break;

      }
      /* Do nothing if not-Ok arrived. */
    }
  }

  /**
   * Fills Spinner with selected currency. Use built-in packages for getting Currency name in singular format.
   */

  public void populateSpinner(int spinnerId, String currencyCode)
  {
    List<String> spinnerArray =  new ArrayList<String>();
    String displayName = null;

    /* Build item from russian currency name + currency code. */

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && !currencyCode.equals("CNY")  && !currencyCode.equals("XDR"))
    {
      try {
        java.util.Currency curr = java.util.Currency.getInstance(currencyCode);
        Locale myLocale = new Locale("ru","RU");
        displayName = curr.getDisplayName (myLocale) + " (" + currencyCode + ")";
      }
      catch (IllegalArgumentException e) {
        Log.d(TAG, "populateSpinner: Exception while trying to get name for currency " + currencyCode);
      }
    }
    else if (currencyCode.equals("CNY")) {
      displayName = getResources().getString (R.string.name_CNY) + " (" + currencyCode + ")";
    }

    if (displayName == null || displayName.length() == 0) {
      displayName = CurrenciesManager.getCurrencyByCode(currencyCode).getName() + " (" + currencyCode + ")";
    }

    spinnerArray.add (displayName);

    /* Create adapter - we use GUI here just for logical reasons. */

    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
      this, android.R.layout.simple_spinner_item, spinnerArray);

    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    Spinner sItems = (Spinner) findViewById(spinnerId);
    sItems.setAdapter(adapter);
  }

  /**
   * When touch spinner - start CurrencySelectorActivity for result.
   */

  private void addSpinnerListener(int spinnerId, final int requestCode)
  {
    Spinner spinnerFrom = (Spinner) findViewById(spinnerId);
    spinnerFrom.setOnTouchListener(new View.OnTouchListener()
    {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && CurrenciesManager.areCurrenciesLoaded()) {
          Intent intentNew = new Intent (getApplicationContext (), CurrencySelectorActivity.class);
          startActivityForResult (intentNew, requestCode);
        }
        return false;
      }
    });
  }

  /**
   * Fills subtitle with refresh date.
   */

  public void setActionBarSubtitle(String currencyRefreshDate)
  {
    Toolbar topToolBar = (Toolbar)findViewById(R.id.toolbar);
    topToolBar.setSubtitle(getResources().getString (R.string.curs_CB) + " " + currencyRefreshDate);
  }

  /**
   * Method warns when currencies are not loaded
   */

  private void warnWhenCurrenciesNotLoaded()
  {
    /* Do nothing when currencies are loaded. */
    if (CurrenciesManager.areCurrenciesLoaded()) {
      return;
    }

    if (!InetHelper.isOnline(this)) {
      InetHelper.askTurnInternetOn(getResources().getString (R.string.turn_on_internet), this);
    } else if (refreshInProgress) {
      InetHelper.askTurnInternetOn(getResources().getString (R.string.please_wait_refresh), this);
    }
  }

}

