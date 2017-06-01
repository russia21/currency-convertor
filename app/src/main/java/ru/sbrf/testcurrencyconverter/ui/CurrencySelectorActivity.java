package ru.sbrf.testcurrencyconverter.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.util.ArrayList;
import java.util.List;

import ru.sbrf.testcurrencyconverter.CurrenciesManager;
import ru.sbrf.testcurrencyconverter.R;
import ru.sbrf.testcurrencyconverter.model.AllCurrencies;
import ru.sbrf.testcurrencyconverter.model.Currency;
import ru.sbrf.testcurrencyconverter.utils.PrefsHelper;


/** PURPOSE - Activity for currency selection from Grid.  */

public class CurrencySelectorActivity extends AppCompatActivity implements CardClickedInterface {

  private List<ItemObject> allItems = null;   // special list for visualizing the grid selector

  /**
   * Overriden onCreate method.
   * */

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_selector);
    setTitle(null);

    Toolbar topToolBar = (Toolbar)findViewById(R.id.toolbar);
    topToolBar.setTitle(R.string.choose_valute);

    /* Make sure that currencies are loaded. */
    if (!CurrenciesManager.areCurrenciesLoaded()) {
      Toast.makeText(this, R.string.currencies_not_loaded, Toast.LENGTH_SHORT).show();
      finish();
      return;
    }

    List<ItemObject> rowListItem = getAllItemList();
    GridLayoutManager lLayout = new GridLayoutManager(CurrencySelectorActivity.this, 3);

    RecyclerView rView = (RecyclerView)findViewById(R.id.recycler_view);
    rView.setHasFixedSize(true);
    rView.setLayoutManager(lLayout);

    RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(CurrencySelectorActivity.this, rowListItem, CurrencySelectorActivity.this);
    rView.setAdapter(rcAdapter);

  }

  /**
   * Method is called from Recycler View Holder, when user clicks on it.
   */

  @Override
  public void onCardClicked (int position)
  {
    /* Send result into calling Activity. Pass code. */
    Intent intent = new Intent();
    intent.putExtra("code", getAllItemList().get (position).getName());
    setResult(RESULT_OK, intent);
    finish();
  }

  /**
   * Method is responsible for building allItems - separate items, used for Grid building.
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

    /* Insert Rouble, USD, EUR first. */

    int resourceId = getResources().getIdentifier("rt_rub", "drawable", getPackageName());
    allItems.add(new ItemObject("RUB", resourceId));

    resourceId = getResources().getIdentifier("rt_usd", "drawable", getPackageName());
    allItems.add(new ItemObject("USD", resourceId));

    resourceId = getResources().getIdentifier("rt_eur", "drawable", getPackageName());
    allItems.add(new ItemObject("EUR", resourceId));

    /* Insert all other currencies. */

    for (Currency currency : CurrenciesManager.getList()) {

      /* Skip already added currencies. */
      if (currency.getCharCode().equals("USD") || currency.getCharCode().equals("EUR")) {
        continue;
      }
      resourceId = getResources().getIdentifier("rt_" + currency.getCharCode().toLowerCase(), "drawable", getPackageName());
      allItems.add(new ItemObject(currency.getCharCode(), resourceId));
    }

    return allItems;
  }

}