package ru.sbrf.testcurrencyconverter.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.sbrf.testcurrencyconverter.R;

/** PURPOSE -- adapter for Recycle View */

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {

  private List<ItemObject> itemList;
  private CardClickedInterface mo_cardClickedInterface;

  RecyclerViewAdapter(Context context, List<ItemObject> itemList, CardClickedInterface cardClickedInterface) {
    this.itemList = itemList;
    mo_cardClickedInterface = cardClickedInterface;
  }

  @Override
  public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

    View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_list, null);
    RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView, mo_cardClickedInterface);
    return rcv;
  }

  @Override
  public void onBindViewHolder(RecyclerViewHolders holder, int position) {
    holder.currencyName.setText(itemList.get(position).getName());
    holder.countryFlag.setImageResource(itemList.get(position).getFlag());
  }

  @Override
  public int getItemCount() {
    return this.itemList.size();
  }
}