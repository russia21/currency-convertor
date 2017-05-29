package ru.sbrf.testcurrencyconverter.ui;

/**
 * PURPOSE -- this class is used for ergonomic keyboard for Amount population.
 */

import android.text.method.DigitsKeyListener;
import android.text.InputType;
public class CustomDigitsKeyListener extends DigitsKeyListener
{
  public CustomDigitsKeyListener() {
    super(false, false);
  }

  public CustomDigitsKeyListener(boolean sign, boolean decimal) {
    super(sign, decimal);
  }

  public int getInputType() {
    return InputType.TYPE_CLASS_PHONE;
  }
}