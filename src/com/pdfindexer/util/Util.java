/*
 * Util.java
 */
package com.pdfindexer.util;

/**
 * Class for utility methods.
 * @author Qpa
 */
public class Util
{
  // Constant.
  /**  The number of fraction digits.*/
  private final static int MAXIMUM_FRACTION_DIGITS = 3;
  // Variable.
  /** The default locale. */
  private static java.util.Locale locale = new java.util.Locale("hu");
  
  /**
   * Method for getting the actual locale.
   * @return the actual locale object.
   */
  public static java.util.Locale getActualLocale()
  {
    return locale;
  }
  
  /**
   * Method for setting the actual locale.
   * @param a new locale object.
   */
  public static void setActualLocale(java.util.Locale aLocale)
  {
    locale = aLocale;
  }
  
  /**
   * Method for translating a string to the actual language.
   * @param entry the translatable string.
   * @return the translated string.
   */
  public static java.lang.String translate(java.lang.String entry)
  {
    java.lang.String transletedEntry = null;
    try
    {
      transletedEntry = java.util.ResourceBundle.getBundle("resources.dictionary", getActualLocale()).getString(entry);
    }
    catch(java.lang.Exception exc)
    {
      transletedEntry = "Missing entry: <" + entry + ">" + "in <" + getActualLocale().getDisplayName(getActualLocale()) + "> dictionary!";
    }
    return transletedEntry;
  }
  
  /**
   * Method for translating a string to the given language.
   * @param entry the translatable string.
   * @param locale the locale of language.
   * @return the translated string.
   */
  public static java.lang.String translate(java.lang.String entry, java.util.Locale locale)
  {
    java.lang.String transletedEntry = null;
    try
    {
      transletedEntry = java.util.ResourceBundle.getBundle("resources.dictionary", locale).getString(entry);
    }
    catch(java.lang.Exception exc)
    {
      transletedEntry = "Missing entry: <" + entry + ">" + "in <" + locale.getDisplayName(locale) + "> dictionary!";
    }
    return transletedEntry;
  }
  
  /**
   * Method for getting the extension part of a file name.
   * @param fileName the file name.
   * @return the extension.
   */
  public static java.lang.String getFileExtension(java.lang.String fileName)
  {
    java.lang.String extension = null;
    if(fileName != null)
    {
      int i = fileName.lastIndexOf('.');
      if(i > 0 && i < fileName.length() - 1)
      {
        extension = fileName.substring(i + 1).toLowerCase();
      }
    }
    return extension;
  }
  
  /**
   * Method for parsing the string reprezentation of a point.
   * @param string the string reprezentation of a point.
   * @return the created point object.
   * @throws java.lang.Exception if there is any problem with parsing.
   */
  public static java.awt.Point parsePoint(java.lang.String string) throws java.lang.Exception
  {
    int x = 0;
    int y = 0;
    java.util.StringTokenizer sT = new java.util.StringTokenizer(string, ",");
    x = java.lang.Integer.parseInt(sT.nextToken().trim());
    y = java.lang.Integer.parseInt(sT.nextToken().trim());
    return new java.awt.Point(x, y);
  }
  
  /**
   * Method for parsing the string reprezentation of a dimension.
   * @param string the string reprezentation of a dimension.
   * @return the created dimension object.
   * @throws java.lang.Exception if there is any problem with parsing.
   */
  public static java.awt.Dimension parseDimension(java.lang.String string) throws java.lang.Exception
  {
    int x = 0;
    int y = 0;
    java.util.StringTokenizer sT = new java.util.StringTokenizer(string, ",");
    x = java.lang.Integer.parseInt(sT.nextToken().trim());
    y = java.lang.Integer.parseInt(sT.nextToken().trim());
    return new java.awt.Dimension(x, y);
  }
  
  /**
   * Method for parsing the string reprezentation of a date.
   * @param string the string reprezentation of a date.
   * @return the created date object.
   * @throws java.lang.Exception if there is any problem with parsing.
   */
  public static java.util.Date parseDate(java.lang.String string) throws java.lang.Exception
  {
    java.text.DateFormat dateFormat = java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT, java.text.DateFormat.SHORT, new java.util.Locale("en"));
    return dateFormat.parse(string);
  }
  
  /**
   * Method for parsing the string reprezentation of a locale.
   * @param string the string reprezentation of a locale.
   * @return the created locale object.
   * @throws java.lang.Exception if there is any problem with parsing.
   */
  public static java.util.Locale parseLocale(java.lang.String string) throws java.lang.Exception
  {
    java.util.Locale locale = null;
    java.lang.String[] localeParameters = string.split("_");
    if(localeParameters.length == 1)
    {
      locale = new java.util.Locale(localeParameters[0]);
    }
    else if(localeParameters.length == 2)
    {
      locale = new java.util.Locale(localeParameters[0], localeParameters[1]);
    }
    else if(localeParameters.length == 3)
    {
      locale = new java.util.Locale(localeParameters[0], localeParameters[1], localeParameters[2]);
    }
    return locale;
  }
  
  /**
   * Method for string reprezentation of double.
   * @param aDouble a double value.
   * @return the string reprezentation of the double.
   */
  public static java.lang.String toString(double aDouble)
  {
    java.text.NumberFormat numberFormat = java.text.NumberFormat.getInstance(new java.util.Locale("en"));
    numberFormat.setGroupingUsed(false);
    numberFormat.setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS);
    return numberFormat.format(aDouble);
  }
  
  /**
   * Method for string reprezentation of a point.
   * @param point a point object.
   * @return the string reprezentation of the point.
   */
  public static java.lang.String toString(java.awt.Point point)
  {
    return java.lang.String.valueOf(point.x) + "," + java.lang.String.valueOf(point.y);
  }
  
  /**
   * Method for string reprezentation of a dimension.
   * @param dimension a dimension object.
   * @return the string reprezentation of the dimension.
   */
  public static java.lang.String toString(java.awt.Dimension dimension)
  {
    return java.lang.String.valueOf(dimension.width) + "," + java.lang.String.valueOf(dimension.height);
  }
  
  /**
   * Method for string reprezentation of a date.
   * @param date a date object.
   * @return the string reprezentation of the date.
   */
  public static java.lang.String toString(java.util.Date date)
  {
    java.text.DateFormat dateFormat = java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT, java.text.DateFormat.SHORT, new java.util.Locale("en"));
    return dateFormat.format(date);
  }
  
  /**
   * Method for formatting int, by the actual locale.
   * @param anInt an int value.
   * @return the formatted string reprezentation of the int.
   */
  public static java.lang.String format(int anInt)
  {
    java.text.NumberFormat numberFormat = java.text.NumberFormat.getInstance(getActualLocale());
    numberFormat.setGroupingUsed(false);
    return numberFormat.format(anInt);
  }
  
  /**
   * Method for formatting int, by the given locale.
   * @param anInt an int value.
   * @param locale the locale of language.
   * @return the formatted string reprezentation of the int.
   */
  public static java.lang.String format(int anInt, java.util.Locale locale)
  {
    java.text.NumberFormat numberFormat = java.text.NumberFormat.getInstance(locale);
    numberFormat.setGroupingUsed(false);
    return numberFormat.format(anInt);
  }
  
  /**
   * Method for formatting int to roman numerals.
   * @param anInt an int value.
   * @return the formatted roman muneral reprezentation of the int.
   */
  public static java.lang.String formatRoman(int anInt)
  {
    java.lang.String romanString = "";
    java.lang.String intString = java.lang.Integer.toString(anInt);
    java.lang.String[][] levels = new java.lang.String[][]{{"i", "v", "x"}, {"x", "l", "c"}, {"c", "d", "m"}};
    for(int i = 0; i < intString.length(); i++)
    {
      char digit = intString.charAt(i);
      int level = intString.length() - i - 1;
      if(level > 2)
      {
        for(int m = 1; m <= digit * java.lang.Math.pow(10, i - 3); m++)
        {
          romanString += "m";
        }
      }
      else
      {
        switch(digit)
        {
          case '1':
            romanString += levels[level][0];
            break;
          case '2':
            romanString += levels[level][0] + levels[level][0];
            break;
          case '3':
            romanString += levels[level][0] + levels[level][0] + levels[level][0];
            break;
          case '4':
            romanString += levels[level][0] + levels[level][1];
            break;
          case '5':
            romanString += levels[level][1];
            break;
          case '6':
            romanString += levels[level][1] + levels[level][0];
            break;
          case '7':
            romanString += levels[level][1] + levels[level][0] + levels[level][0];
            break;
          case '8':
            romanString += levels[level][1] + levels[level][0] + levels[level][0] + levels[level][0];
            break;
          case '9':
            romanString += levels[level][0] + levels[level][2];
            break;
          case '0':
            break;
        }
      }
    }
    return romanString;
  }
  
  /**
   * Method for formatting double, by the actual locale.
   * @param aDouble a double value.
   * @return the formatted string reprezentation of the double.
   */
  public static java.lang.String format(double aDouble)
  {
    java.text.NumberFormat numberFormat = java.text.NumberFormat.getInstance(getActualLocale());
    numberFormat.setGroupingUsed(false);
    numberFormat.setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS);
    return numberFormat.format(aDouble);
  }
  
  /**
   * Method for formatting double, by the given locale.
   * @param aDouble a double value.
   * @param locale the locale of language.
   * @return the formatted string reprezentation of the double.
   */
  public static java.lang.String format(double aDouble, java.util.Locale locale)
  {
    java.text.NumberFormat numberFormat = java.text.NumberFormat.getInstance(locale);
    numberFormat.setGroupingUsed(false);
    numberFormat.setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS);
    return numberFormat.format(aDouble);
  }
  
  /**
   * Method for formatting date, by the actual locale.
   * @param date a date value.
   * @return the formatted string reprezentation of the date.
   */
  public static java.lang.String format(java.util.Date date)
  {
    java.text.DateFormat dateFormat = java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT, java.text.DateFormat.SHORT, getActualLocale());
    return dateFormat.format(date);
  }
  
  /**
   * Method for getting the installed dictonary locales.
   * @return a vector of locales.
   * @throws java.lang.Exception if there is any problem with getting.
   */
  public static java.util.Vector getAvailableDictionaries() throws java.lang.Exception
  {
    java.util.Vector dictionaryLocales = new java.util.Vector();
    java.util.Locale defaultLocale = java.util.ResourceBundle.getBundle("resources.dictionary").getLocale();
    java.util.Locale[] locales = java.util.Locale.getAvailableLocales();
    dictionaryLocales.add(defaultLocale);
    for(int i = 1; i < locales.length; i++)
    {
      java.util.ResourceBundle resourceBundle = null;
      try
      {
        resourceBundle = java.util.ResourceBundle.getBundle("resources.dictionary", locales[i]);
      }
      catch(java.lang.Exception exc)
      {
      }
      if(resourceBundle != null && !resourceBundle.getLocale().equals(defaultLocale) && resourceBundle.getLocale().equals(locales[i]))
      {
        dictionaryLocales.add(locales[i]);
      }
    }
    return dictionaryLocales;
  }
  
  /**
   * Returns a literal pattern <code>String</code> for the specified <code>String</code>.
   * @param  s The string to be literalized
   * @return  A literal string replacement
   */
  public static String quote(java.lang.String s)
  {
    int slashEIndex = s.indexOf("\\E");
    if (slashEIndex == -1)
    {
      return "\\Q" + s + "\\E";
    }
    StringBuilder sb = new StringBuilder(s.length() * 2);
    sb.append("\\Q");
    slashEIndex = 0;
    int current = 0;
    while ((slashEIndex = s.indexOf("\\E", current)) != -1)
    {
      sb.append(s.substring(current, slashEIndex));
      current = slashEIndex + 2;
      sb.append("\\E\\\\E\\Q");
    }
    sb.append(s.substring(current, s.length()));
    sb.append("\\E");
    return sb.toString();
  }
  
  /**
   * Inner class for quoting.
   */
  public static class StringBuilder
  {
    /** The value is used for character storage. */
    char value[];
    /** The count is the number of characters used. */
    int count;

    /**
     * Constructs a string builder with no characters in it and an 
     * initial capacity specified by the <code>capacity</code> argument. 
     * @param capacity  the initial capacity.
     * @throws NegativeArraySizeException  if the <code>capacity</code> argument is less than <code>0</code>.
     */
    public StringBuilder(int capacity)
    {
      value = new char[capacity];
    }
    
    /**
     * Appends the specified string to this character sequence.
     * @param str a string.
     * @return a reference to this object.
     */
    public StringBuilder append(java.lang.String str)
    {
      if (str == null) str = "null";
      int len = str.length();
      if (len == 0) return this;
      int newCount = count + len;
      if (newCount > value.length)
        expandCapacity(newCount);
      str.getChars(0, len, value, count);
      count = newCount;
      return this;
    }
    
    /**
     * This implements the expansion semantics of ensureCapacity with no
     * size check or synchronization.
     */
    void expandCapacity(int minimumCapacity)
    {
      int newCapacity = (value.length + 1) * 2;
      if (newCapacity < 0)
      {
        newCapacity = Integer.MAX_VALUE;
      }
      else if (minimumCapacity > newCapacity)
      {
        newCapacity = minimumCapacity;
      }	
      char newValue[] = new char[newCapacity];
      java.lang.System.arraycopy(value, 0, newValue, 0, count);
      value = newValue;
    }
  
  }
  
}
