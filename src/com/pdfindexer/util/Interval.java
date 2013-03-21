/*
 * Interval.java
 */
package com.pdfindexer.util;

/**
 * Class for reprazenting an interval of pages.
 * @author Qpa
 */
public class Interval
{
 
  /** The starting page. */
  private int from ;
  /** The ending page. */
  private int to;
  /** The paging start value. */
  private int startPaging;
  /** The page rendering prefix. */
  private java.lang.String prefix;
  /** The page rendering suffix. */
  private java.lang.String suffix;
  /** The numeral of interval. */
  private boolean isRoman;
  /** The case of page. */
  private boolean isUpperCase;
  
  /**
   * Constructor.
   * @param from the begining of interval.
   * @param to the end of interval.
   */
  public Interval(int from, int to)
  {
    this(from, to, 1, "", "", false, false);
  }
  
  /**
   * Constructor.
   * @param from the begining of interval.
   * @param to the end of interval.
   * @param startPaging the paging start value.
   * @param prefix the rendering prefix.
   * @param suffix the rendering suffix.
   * @param isRoman the rendering numeral type.
   * @param isUpperCase if true, the page number is in uppercase, in lowercase otherwise.
   */
  public Interval(int from, int to, int startPaging, java.lang.String prefix, java.lang.String suffix, boolean isRoman, boolean isUpperCase)
  {
    this.from = from;
    this.to = to;
    this.startPaging = startPaging;
    this.prefix = prefix;
    this.suffix = suffix;
    this.isRoman = isRoman;
    this.isUpperCase = isUpperCase;
  }
    
  /**
   * Method for getting the "from" page of this interval.
   * @return the "from" page of this interval.
   */
  public int getFromPage()
  {
    return from;
  }
  
  /**
   * Method for setting the "from" page of this interval.
   * @param from the new "from" page.
   */
  public void setFromPage(int from)
  {
    this.from = from;
  }
  
  /**
   * Method for getting the "to" page of this interval.
   * @return the "to" page of this interval.
   */
  public int getToPage()
  {
    return to;
  }
  
  /**
   * Method for setting the "to" page of this interval.
   * @param to the new "to" page.
   */
  public void setToPage(int to)
  {
    this.to = to;
  }
  
  /**
   * Method for getting the paging start value.
   * @return the start value of paging.
   */
  public int getStartPaging()
  {
    return startPaging;
  }
  
  /**
   * Method for setting the paging start value.
   * @param to the new start value of paging.
   */
  public void setStartPaging(int startPaging)
  {
    this.startPaging = startPaging;
  }
  
  /**
   * Method for getting the prefix.
   * @return the prefix text.
   */
  public java.lang.String getPrefix()
  {
    return prefix;
  }
  
  /**
   * Method for setting the prefix.
   * @param prefix the new prefix.
   */
  public void setPrefix(java.lang.String prefix)
  {
    this.prefix = prefix;
  }
  
  /**
   * Method for getting the suffix.
   * @return the suffix text.
   */
  public java.lang.String getSuffix()
  {
    return suffix;
  }
  
  /**
   * Method for setting the suffix.
   * @param suffix the new suffix.
   */
  public void setSuffix(java.lang.String suffix)
  {
    this.suffix = suffix;
  }
  
  /**
   * Method for testing the page numeral rendering type.
   * @return true, if the page numbers are rendered in roman numerals.
   */
  public boolean isRoman()
  {
    return isRoman;
  }
  
  /**
   * Method for setting the page numeral rendering type.
   * @param isRoman the new value.
   */
  public void setRoman(boolean isRoman)
  {
    this.isRoman = isRoman;
  }
  
  /**
   * Method for testing the page numeral case type.
   * @return true, if the page numbers are rendered in upper case.
   */
  public boolean isUpperCase()
  {
    return isUpperCase;
  }
  
  /**
   * Method for setting the page numeral case type.
   * @param isUpperCase the new value.
   */
  public void setUpperCase(boolean isUpperCase)
  {
    this.isUpperCase = isUpperCase;
  }
  
  /**
   * Method for testing the inclusion.
   * @return true, if this interval includes the given page.
   */
  public boolean includes(int page)
  {
    return (from <= page && page <= to);
  }
  
  /**
   * Overridden method from <CODE>java.lang.Object</CODE>.
   * @param interval an <CODE>elv.util.proprties.Interval</CODE> object.
   * @return true if the given interval equals with this interval.
   */
  public boolean equals(java.lang.Object interval)
  {
    boolean isEqual = false;
    if(interval != null && interval instanceof Interval)
    {
      isEqual = (from == ((Interval)interval).from && to == ((Interval)interval).to);
    }
    return isEqual;
  }
  
}
