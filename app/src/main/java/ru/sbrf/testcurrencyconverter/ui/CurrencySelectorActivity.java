package ru.sbrf.testcurrencyconverter.ui;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.util.ArrayList;
import java.util.List;

import ru.sbrf.testcurrencyconverter.R;
import ru.sbrf.testcurrencyconverter.model.AllCurrencies;
import ru.sbrf.testcurrencyconverter.model.Valute;
import ru.sbrf.testcurrencyconverter.utils.PrefsHelper;


/** PURPOSE - Activity for currency selection from Grid.  */

public class CurrencySelectorActivity extends ActionBarActivity implements CardClickedInterface {

  private AllCurrencies allCurrencies;        // private list of all currencies
  private List<ItemObject> allItems = null;   // special list for visualizing the grid selector

  /**********************************************************************************************************************/

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_selector);
    setTitle(null);

    Toolbar topToolBar = (Toolbar)findViewById(R.id.toolbar);
    topToolBar.setTitle(R.string.choose_valute);

    /* Get currencies list */
    try
    {
      Serializer ser = new Persister();
      allCurrencies = ser.read(AllCurrencies.class, PrefsHelper.s_getDataFromPrefs(PrefsHelper.S__STG_CURRENCIES_XML, this));

    } catch (Exception e)
    {
      e.printStackTrace();
    }

    List<ItemObject> rowListItem = getAllItemList();
    GridLayoutManager lLayout = new GridLayoutManager(CurrencySelectorActivity.this, 3);

    RecyclerView rView = (RecyclerView)findViewById(R.id.recycler_view);
    rView.setHasFixedSize(true);
    rView.setLayoutManager(lLayout);

    RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(CurrencySelectorActivity.this, rowListItem, CurrencySelectorActivity.this);
    rView.setAdapter(rcAdapter);

  }

  /**********************************************************************************************************************
   * PURPOSE -- function is responsible for building allItems - separate items, used for Grid building.
   * Builds allItems only once.
   *
   * @return Returns allItems.
   */

  private List<ItemObject> getAllItemList(){

    if (allItems != null) {
      return allItems;
    }

    /* Build all items. */

    allItems = new ArrayList<ItemObject>();

    // Insert Rouble, USD, EUR first

    int resourceId = getResources().getIdentifier("rt_rub", "drawable", getPackageName());
    allItems.add(new ItemObject("RUB", resourceId));

    resourceId = getResources().getIdentifier("rt_usd", "drawable", getPackageName());
    allItems.add(new ItemObject("USD", resourceId));

    resourceId = getResources().getIdentifier("rt_eur", "drawable", getPackageName());
    allItems.add(new ItemObject("EUR", resourceId));


    for (Valute valute : allCurrencies.getList()) {

      // skip already added currencies
      if (valute.ms_charCode.equals("USD") || valute.ms_charCode.equals("EUR")) {
        continue;
      }
      resourceId = getResources().getIdentifier("rt_" + valute.ms_charCode.toLowerCase(), "drawable", getPackageName());
      allItems.add(new ItemObject(valute.ms_charCode, resourceId));
    }

    return allItems;
  }

  /**********************************************************************************************************************/

  @Override
  public void v_cardClicked(int position)
  {
    // send result into calling Activity. Pass code.

    Intent intent = new Intent();
    intent.putExtra("code", getAllItemList().get (position).getName());
    setResult(RESULT_OK, intent);
    finish();
  }
}