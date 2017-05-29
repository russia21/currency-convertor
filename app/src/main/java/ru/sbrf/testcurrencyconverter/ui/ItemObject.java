package ru.sbrf.testcurrencyconverter.ui;

/** PURPOSE -- class used for GridView building. */

class ItemObject {

  private String name;   // name of the currency
  private int flag;      // resource id for the flag

  ItemObject(String name, int flag) {
    this.name = name;
    this.flag = flag;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getFlag() {
    return flag;
  }

  public void setFlag(int flag) {
    this.flag = flag;
  }
}