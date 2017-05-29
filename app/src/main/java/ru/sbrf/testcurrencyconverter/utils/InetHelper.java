package ru.sbrf.testcurrencyconverter.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

/**
 * PURPOSE -- useful utils, which helps to work without internet connection.
 */

public class InetHelper
{


/******************************************************************************************************
 * PURPOSE -- checks whether software is online. Requires ACCESS_NETWORK_STATE
 *
 * @param o_context -- input -- context of the current operation
 */

public static boolean b_isOnline (Context o_context)
{
  ConnectivityManager o_cm = (ConnectivityManager) o_context.getSystemService(Context.CONNECTIVITY_SERVICE);
  NetworkInfo o_netInfo = o_cm.getActiveNetworkInfo ();
  if (o_netInfo != null && o_netInfo.isConnected ())
  {
    return true;
  }
  return false;
}

/******************************************************************************************************
 * PURPOSE -- asks user to turn internet connection on.
 *
 * @param s_msg -- input -- message for turning internet on
 * @param s_ok --  input -- positive button
 * @param s_cancel -- input -- cancel button
 * @param o_context -- input -- context
 *
 */

private static void v_turnInternetOnBtns(String s_msg, String s_ok, String s_cancel, final Context o_context)
{
  new AlertDialog.Builder (o_context)
    .setTitle("Доступ к сети интернет")
    .setMessage (s_msg)
    .setPositiveButton(s_ok, new DialogInterface.OnClickListener()
    {
      @Override
      public void onClick(DialogInterface dialog, int which)
      {
        o_context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
      }
    })
    .setNegativeButton(s_cancel, new DialogInterface.OnClickListener()
    {
      @Override
      public void onClick(DialogInterface dialog, int which)
      {
      }
    })
    .show();
}

/******************************************************************************************************
 * PURPOSE -- asks user to turn internet connection on.
 *
 * @param s_msg -- input -- message for turning internet on
 * @param o_context -- input -- context
 *
 * @return RETURNS -- true always.
 */

public static boolean b_turnInternetOn (String s_msg, Context o_context)
{
  v_turnInternetOnBtns (s_msg, "Включить", "Отмена", o_context);
  return true;
}

}  /* End of InetHelper class */
