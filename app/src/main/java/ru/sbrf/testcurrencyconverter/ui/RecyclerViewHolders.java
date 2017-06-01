package ru.sbrf.testcurrencyconverter.ui;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ru.sbrf.testcurrencyconverter.R;


class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

  TextView currencyName;   /* currency name */
  ImageView countryFlag;   /* country flag  */
  private CardClickedInterface cardClickedInterface; /* interface, used for transmitting information about parent activity */

  /** Constructor. */

  RecyclerViewHolders(View itemView, CardClickedInterface cardClickedInterface) {
    super(itemView);
    itemView.setOnClickListener(this);
    currencyName = (TextView)itemView.findViewById(R.id.country_name);
    countryFlag = (ImageView)itemView.findViewById(R.id.country_flag);
    this.cardClickedInterface = cardClickedInterface;
  }

  @Override
  public void onClick(View view) {
    /* Call method from parent activity via interface. */
    cardClickedInterface.onCardClicked(getPosition());
  }

}