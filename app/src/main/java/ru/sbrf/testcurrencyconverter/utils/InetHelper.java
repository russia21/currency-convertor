package ru.sbrf.testcurrencyconverter.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import ru.sbrf.testcurrencyconverter.R;

/**
 * Class contains useful static methods, which help to work without internet connection.
 */

public class InetHelper
{


  /**
   * Method checks whether software is online. Requires ACCESS_NETWORK_STATE
   *
   * @param context -- input -- context of the current operation
   */

  public static boolean isOnline(Context context)
  {
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo ();
    return (netInfo != null) && (netInfo.isConnected());
  }

  /**
   * Method asks user to turn internet connection on.
   *
   * @param msg       -- input -- message for turning internet on
   * @param ok        --  input -- positive button
   * @param cancel  -- input -- cancel button
   * @param context -- input -- context
   *
   */

  private static void askTurnInternetOn(String msg, String ok, String cancel, final Context context)
  {
    new AlertDialog.Builder (context)
      .setTitle(R.string.title_web_access)
      .setMessage (msg)
      .setPositiveButton(ok, new DialogInterface.OnClickListener()
      {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
          context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        }
      })
      .setNegativeButton(cancel, new DialogInterface.OnClickListener()
      {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
        }
      })
      .show();
  }

  /**
   * PURPOSE -- asks user to turn internet connection on.
   *
   * @param msg -- input -- message for turning internet on
   * @param context -- input -- context
   *
   * @return RETURNS -- true always.
   */

  public static boolean askTurnInternetOn(String msg, Context context)
  {
    askTurnInternetOn(msg, context.getResources().getString(R.string.turn_on),
      context.getResources().getString(R.string.cancel), context);
    return true;
  }

}  /* End of InetHelper class */
