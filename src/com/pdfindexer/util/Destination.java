/*
 * Destination.java
 */
package com.pdfindexer.util;

/**
 * Class for destination reprezentation
 * @author Qpa
 */
public class Destination
{
  
  /** The page number.*/
  private int page;
  /** The bounding rectangles on the page.*/
  private java.util.Vector boundingRectangles;
  /** The page rendering prefix. */
  private java.lang.String prefix;
  /** The diplayed page number. */
  private int displayPage;
  /** The page rendering suffix. */
  private java.lang.String suffix;
  /** The numeral of page. */
  private boolean isRoman;
  /** The case of page.  */
  private boolean isUpperCase;

  /**
   * Constructor.
   * @param page the page number.
   * @param boundingRectangles the bounding rectangles on the page.
   * @param prefix the rendering prefix.
   * @param displayPage the diplayed page number.
   * @param suffix the rendering suffix.
   * @param isRoman if true, the page number is rendered as roman numeral.
   * @param isUpperCase if true, the page number is in uppercase, in lowercase otherwise.
   */
  public Destination(int page, java.util.Vector boundingRectangles, java.lang.String prefix, int displayPage, java.lang.String suffix,
    boolean isRoman, boolean isUpperCase)
  {
    this.page = page;
    this.boundingRectangles = boundingRectangles;
    this.prefix = prefix;
    this.displayPage = displayPage;
    this.suffix = suffix;
    this.isRoman = isRoman;
    this.isUpperCase = isUpperCase;
  }

  /**
   * Method for getting the page.
   * @return the page number (firt page is 1).
   */
  public int getPage()
  {
    return page;
  }

  /**
   * Method for getting the bounding rectangles on the destination page.
   * @return a vector of bounding rectangles.
   */
  public java.util.Vector getBoundingRectangles()
  {
    return boundingRectangles;
  }

  /**
   * Overridden method from <code>java.lang.Object</code>.
   * @return the string reprezentation of this destination.
   */
  public java.lang.String toString()
  {
    java.lang.String pageNumeral =  isRoman ? com.pdfindexer.util.Util.formatRoman(displayPage) : com.pdfindexer.util.Util.format(displayPage);
    pageNumeral = isUpperCase ? pageNumeral.toUpperCase(com.pdfindexer.util.Util.getActualLocale()) : pageNumeral.toLowerCase(com.pdfindexer.util.Util.getActualLocale());
    return prefix + pageNumeral + suffix;
  }

  /**
   * Method for line reprezentation.
   * @return the line reprezentation of this destination.
   * @throws <code>java.lang.Exception</code> if there is problem with parsing.
   */
  public java.lang.String toLine() throws java.lang.Exception
  {
    java.lang.String line = java.lang.Integer.toString(page);
    for(int i = 0; i < boundingRectangles.size(); i++)
    {
      java.awt.Rectangle.Float iteratorRectangle = (java.awt.Rectangle.Float)boundingRectangles.get(i);
      line += " " + iteratorRectangle.getX() + "," + iteratorRectangle.getY() + "," + iteratorRectangle.getWidth() + "," + iteratorRectangle.getHeight();
    }
    return line;
  }
  
  /**
   * Method for HTML line reprezentation.
   * @return the HTML line reprezentation of this destination.
   * @throws <code>java.lang.Exception</code> if there is problem with parsing.
   */
  public java.lang.String toHTMLLine() throws java.lang.Exception
  {
    java.lang.String line = "";
    if(boundingRectangles == null || boundingRectangles.size() == 0)
    {
      line = toString();
    }
    else
    {
      line = " <a href=\"" + toLine() + "\">" + toString() + "</a>";
    }
    return line;
  }
  
  /**
   * Method for parsing the line reprezentation of a destination.
   * @param line the line reprezentation of a destination.
   * @return the created destination.
   * @throws <code>java.lang.Exception</code> if there is problem with parsing.
   */
  public static Destination parse(java.lang.String line) throws java.lang.Exception
  {
    // line: page x1,y1,width1,height1 x2,y2,width2,height2 ...
    java.util.StringTokenizer sT = new java.util.StringTokenizer(line, " ");
    int page = java.lang.Integer.parseInt(sT.nextToken());
    java.util.Vector boundingRectangles = new java.util.Vector();
    while(sT.hasMoreTokens())
    {
      java.util.StringTokenizer rectangleTokenizer = new java.util.StringTokenizer(sT.nextToken(), ",");
      float x = java.lang.Float.parseFloat(rectangleTokenizer.nextToken());
      float y = java.lang.Float.parseFloat(rectangleTokenizer.nextToken());
      float width = java.lang.Float.parseFloat(rectangleTokenizer.nextToken());
      float height = java.lang.Float.parseFloat(rectangleTokenizer.nextToken());
      boundingRectangles.add(new java.awt.Rectangle.Float(x, y, width, height));
    }
    return new Destination(page, boundingRectangles, "", 0, "", false, false);
  }

}
