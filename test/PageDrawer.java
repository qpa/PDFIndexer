/*
 * PageDrawer.java
 */

package com.pdfindexer.gui;

/**
 * Class for drawing a PDF page.
 * @author Qpa
 */
public class PageDrawer extends org.pdfbox.pdfviewer.PageDrawer
{
  /** The graphics where to draw.*/
  private java.awt.Graphics2D graphics;
  /** The drawable page.*/
  private org.pdfbox.pdmodel.PDPage pdPage;
  /** The page dimension.*/
  private java.awt.Dimension pageDimension;
  /** The scaling factor.*/
  private float scale;
  /** The drawing transform. */
  java.awt.geom.AffineTransform drawTransform;
  /** The highlight color. */
  private java.awt.Color highlightColor = java.awt.Color.BLUE;
  /** The string to highlight. */
  private java.lang.String highlightableString;
  /** The shapes to highlight. */
  private java.util.Vector<java.awt.Shape> highlightableShapes;
  /** The highlighting case sensitivity. */
  private boolean matchCase = false;
  
  /**
   * Constructor.
   * @throws IOException if there is an error loading properties from the file.
   */
  public PageDrawer() throws java.io.IOException
  {
    super();
  }
  
  /**
   * Method for drawing a page.
   * @param g the graphics context to draw onto.
   * @param pdPage the page to draw.
   * @param pageDimension the size of the page to draw.
   * @throws IOException if there is an IO error while drawing the page.
   */
  public void drawPage(java.awt.Graphics g, org.pdfbox.pdmodel.PDPage pdPage, java.awt.Dimension pageDimension) throws java.io.IOException
  {
    graphics = (java.awt.Graphics2D)g;
    this.pdPage = pdPage;
    this.pageDimension = pageDimension;
    if(highlightableShapes != null)
    {
      java.awt.Color baseColor = graphics.getColor();
      for(java.awt.Shape iteratorShape : highlightableShapes)
      {
        graphics.setColor(highlightColor);
        graphics.fill(iteratorShape);
      }
      graphics.setColor(baseColor);
    }
    graphics.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 11));
    super.drawPage(g, pdPage, pageDimension);
        
//        java.awt.geom.AffineTransform rotation = graphics.getTransform();
//        rotation.rotate((pdPage.findRotation() * Math.PI) / 180d );
//        graphics.setTransform(rotation);
//    float mediaX = pdPage.findMediaBox().getLowerLeftX() * scaling;
//    float mediaY = pdPage.findMediaBox().getLowerLeftY() * scaling;
//    float mediaW = pdPage.findMediaBox().getWidth() * scaling;
//    float mediaH = pdPage.findMediaBox().getHeight() * scaling;
//    float cropX = pdPage.findCropBox().getLowerLeftX() * scaling;
//    float cropY = pdPage.findCropBox().getLowerLeftY() * scaling;
//    float cropW = pdPage.findCropBox().getWidth() * scaling;
//    float cropH = pdPage.findCropBox().getHeight() * scaling;
//    int x_size = (int)(cropW + (cropX - mediaX));
//    int y_size = (int)(cropH + (cropY - mediaY));
//    drawTransform = new java.awt.geom.AffineTransform();
//    if(pdPage.findRotation() == 270)
//    {
//      drawTransform.rotate(-java.lang.Math.PI / 2.0, x_size/ 2, y_size / 2);
//      double x_change = (drawTransform.getTranslateX());
//      double y_change = (drawTransform.getTranslateY());
//      drawTransform.translate((y_size - y_change), -x_change);
//      drawTransform.translate(0, y_size);
//      drawTransform.scale(1, -1);
//      drawTransform.translate(-(cropX + mediaX), -(mediaH - cropH - (cropY - mediaY)));
//    }
//    else if(pdPage.findRotation() == 180)
//    {
//      drawTransform.rotate(java.lang.Math.PI, x_size / 2, y_size / 2);
//      drawTransform.translate(-(cropX + mediaX), y_size + (cropY + mediaY) - (mediaH - cropH - (cropY - mediaY)));
//      drawTransform.scale(1, -1);
//    }
//    else if(pdPage.findRotation() == 90)
//    {
//      drawTransform.rotate(Math.PI / 2.0);
//      drawTransform.translate(0, (cropY + mediaY) - (mediaH - cropH - (cropY - mediaY)));
//      drawTransform.scale(1, -1);
//    }
//    else
//    {
//      drawTransform.translate(0, y_size);
//      drawTransform.scale(1, -1);
//      drawTransform.translate(0, -(mediaH - cropH - (cropY - mediaY)));
//    }
//    drawTransform.scale(scaling,scaling);
//    java.awt.geom.AffineTransform currentTransform = graphics.getTransform();
//    
//    
//    graphics.transform(drawTransform);
//    graphics.setTransform(currentTransform);
  }

  /**
   * Overridden method from <code>org.pdfbox.pdfviewer.PageDrawer</code>.
   * @param text the string to display.
   */
  protected void showCharacter(org.pdfbox.util.TextPosition text)
  {
    try
    {
      org.pdfbox.pdmodel.font.PDFont font = text.getFont();
      if(highlightableString != null && !highlightableString.equals(""))
      {
        java.awt.Color baseColor = graphics.getColor();
        graphics.setColor(highlightColor);
        
        java.lang.String textCharacter = text.getCharacter();
        java.lang.String textString = matchCase ? text.getCharacter() : text.getCharacter().toLowerCase();
        highlightableString = matchCase ? highlightableString : highlightableString.toLowerCase();
        float x = text.getX();
        while(textString.contains(highlightableString))
        {
          int index = textString.indexOf(highlightableString);
          java.lang.String preCharacter = textCharacter.substring(0, index);
          x += font.getStringWidth(preCharacter) / 1000 * text.getFontSize() * text.getXScale();
          float y = text.getY() - (text.getFontSize() - text.getFontSize() / 4) * text.getYScale();
          java.lang.String highligtableCharacter = textCharacter.substring(index, index + highlightableString.length());
          float width = font.getStringWidth(highligtableCharacter) / 1000 * text.getFontSize() * text.getXScale();
          float height = text.getFontSize() * text.getYScale();
          graphics.fill(new java.awt.Rectangle.Float(x, y, width, height));
          x += width;
          textCharacter = textCharacter.substring(index + highlightableString.length());
          textString = textString.substring(index + highlightableString.length());
        }
        graphics.setColor(baseColor);
      }
      graphics.setColor(java.awt.Color.BLACK);
      font.drawString( text.getCharacter(), graphics, text.getFontSize(), text.getXScale(), text.getYScale(), text.getX(), text.getY() );
    }
    catch(java.io.IOException exc)
    {
      exc.printStackTrace();
    }
  }
  
  /**
   * Method for highlighting a string an a page.
   * @param highlightableString the highlightable string; <code>null</code> or <code>""</code> means highlight nothing.
   * @param match case if true, case sensitive highligth is done.
   * @throws IOException if there is an IO error while drawing the page.
   */
  public void highlight(java.lang.String highlightableString, boolean matchCase) throws java.io.IOException
  {
    this.highlightableString = highlightableString;
    this. matchCase = matchCase;
//    drawPage(graphics, pdPage, pageDimension);
  }
  
  /**
   * Method for highlighting shapes an a page.
   * @param shapes the highlightable shapes; <code>null</code> or zero size means highlight nothing.
   * @throws IOException if there is an IO error while drawing the page.
   */
  public void highlight(java.util.Vector<java.awt.Shape> highlightableShapes) throws java.io.IOException
  {
    this.highlightableShapes = highlightableShapes;
//    drawPage(graphics, pdPage, pageDimension);
  }
  
  /**
   * Method for setting the highlighting color.
   * @param color the new highlight color.
   * @throws IOException if there is an IO error while drawing the page.
   */
  public void setHighlightColor(java.awt.Color highlightColor) throws java.io.IOException
  {
    this.highlightColor = highlightColor;
//    drawPage(graphics, pdPage, pageDimension);
  }
  
}
