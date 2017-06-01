package ru.sbrf.testcurrencyconverter.ui;

/**
 * PURPOSE -- Interface used for calling Activity from AsyncTask
 */

public interface RefreshInterface
{
  /** PURPOSE -- function is used to refresh currencies in Activity GUI.
   * @param s_xml -- extracted xml from CBR server, or null - if we need to extract the XML from cache.
   */

  void refreshCurrenciesFromCache(String s_xml);

  /**
   * PURPOSE -- sets the informational flag which shows that we're in refreshing process.
   */

  public void setProgressFlag ();

  /**
   * PURPOSE -- clears the informational flag which showed that we were in refreshing process.
   */

  public void removeProgressFlag ();
}
