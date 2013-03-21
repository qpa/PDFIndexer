/*
 * PDFPagePanel.java
 */

package com.pdfindexer.gui;

/**
 * Class for displaying a PDF page in a panel.
 * @author Qpa
 */
public class PDFPagePanel extends javax.swing.JPanel
{
  
  private org.pdfbox.pdmodel.PDPage pdPage;
  private PageDrawer drawer;
  private java.awt.Dimension pageDimension;
    
  /**
   * Constructor.
   * @throws java.io.IOException if there is an error creating the Page drawing objects.
   */
  public PDFPagePanel() throws java.io.IOException
  {
    drawer = new PageDrawer();
  }
  
  /**
   * Method for setting the page that should be displayed in this panel.
   * @param pdPage the PDF page.
   */
  public void setPage(org.pdfbox.pdmodel.PDPage pdPage)
  {
    this.pdPage = pdPage;
    org.pdfbox.pdmodel.common.PDRectangle pageSize = pdPage.findMediaBox();
    int rotation = pdPage.findRotation();
    pageDimension = pageSize.createDimension();
    if(rotation == 90 || rotation == 270)
    {
      pageDimension = new java.awt.Dimension(pageDimension.height, pageDimension.width);
    }
    setSize(pageDimension);
    setBackground(java.awt.Color.WHITE);
  }

  /**
   * Method for highlighting a string an this page.
   * @param highlightableString the highlightable string; <code>null</code> or <code>""</code> means highlight nothing.
   * @param match case if true, case sensitive highligth is done.
   * @throws IOException if there is an IO error while drawing the page.
   */
  public void highlight(java.lang.String highlightableString, boolean matchCase) throws java.io.IOException
  {
    drawer.highlight(highlightableString, matchCase);
    repaint();
  }
  
  /**
   * Method for highlighting shapes an a page.
   * @param shapes the highlightable shapes; <code>null</code> or zero size means highlight nothing.
   * @throws IOException if there is an IO error while drawing the page.
   */
  public void highlight(java.util.Vector<java.awt.Shape> highlightableShapes) throws java.io.IOException
  {
    drawer.highlight(highlightableShapes);
    repaint();
  }
  
  /**
   * Method for setting the highlighting color.
   * @param color the new highlight color.
   * @throws IOException if there is an IO error while drawing the page.
   */
  public void setHighlightColor(java.awt.Color highlightColor) throws java.io.IOException
  {
    drawer.setHighlightColor(highlightColor);
    repaint();
  }
  
  /**
   * @see JPanel#paint( Graphics )
   */
  public void paint(java.awt.Graphics g)
  {
    try
    {
      g.setColor(getBackground());
      g.fillRect(0, 0, getWidth(), getHeight());
      drawer.drawPage(g, pdPage, pageDimension);
    }
    catch(java.io.IOException exc)
    {
      exc.printStackTrace();
    }
  }
  
}
