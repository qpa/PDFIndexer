/*
 * PdfBoxReader.java
 */

package com.pdfindexer.gui;

/**
 *
 * @author Qpa
 */
public class PdfBoxReader extends org.pdfbox.PDFReader
{
  
  /** Creates a new instance of PdfBoxReader */
  public PdfBoxReader()
  {
    super();
  }
  
  public static void main(String[] args) throws Exception
  {
    // Set the logging for the stripper.
    java.lang.System.setProperty("log4j.configuration", "resources/log4j.xml");
    PdfBoxReader viewer = new PdfBoxReader();
    viewer.setVisible(true);
  }
  
}
