/*
 * Index.java
 */
package com.pdfindexer.util;

/**
 * Class for reprezenting one index entry.
 * @author Qpa
 */
public class Index
{

  /** The searched entry.*/
  private java.lang.String entry;
  /** The existing destinations.*/
  private java.util.Vector destinations;

  /**
   * Constructor.
   * @param entry the searched entry.
   * @param destinations the existing destinations.
   */
  public Index(java.lang.String entry, java.util.Vector destinations)
  {
    this.entry = entry;
    this.destinations = destinations;
  }

  /**
   * Method for getting the entry.
   * @return the entry string.
   */
  public java.lang.String getEntry()
  {
    return entry;
  }

  /**
   * Method for getting the destinations.
   * @return a vector of destinations.
   */
  public java.util.Vector getDestinations()
  {
    return destinations;
  }

  /**
   * Overridden method from <code>java.lang.Object</code>.
   * @return the string reprezentation of this index.
   */
  public java.lang.String toString()
  {
    return entry;
  }

  /**
   * Method for line reprezentation.
   * @return the line reprezentation of this index.
   */
  public java.lang.String toLine()
  {
    java.lang.String line = entry;
    for(int i = 0; i < destinations.size(); i++)
    {
      Destination iteratorDestination = (Destination)destinations.get(i);
      if(i == 0)
      {
        line += " " + iteratorDestination;
      }
      else
      {
        line += ", " + iteratorDestination;
      }
    }
    return line;
  }

  /**
   * Method for HTML line reprezentation.
   * @return the HTML line reprezentation of this index.
   * @throws <code>java.lang.Exception</code> if there is problem with parsing.
   */
  public java.lang.String toHTMLLine() throws java.lang.Exception
  {
    java.lang.String line = "<p>" + entry;
    for(int i = 0; i < destinations.size(); i++)
    {
      Destination iteratorDestination = (Destination)destinations.get(i);
      if(i == 0)
      {
        line += iteratorDestination.toHTMLLine();
      }
      else
      {
        line += ", " + iteratorDestination.toHTMLLine();
      }
    }
    return line + "</p>";
  }
  
}
