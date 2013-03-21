/*
 * Indexer.java
 */
package com.pdfindexer.util;

/**
 * Class for finding the page numbers of the given entries.
 * @author Qpa
 */
public class Indexer_4 extends java.util.TimerTask
{
  /**
   * Inner class for searching one entry.
   */
  private class EntryStripper extends org.pdfbox.util.PDFTextStripper
  {
    /** The document page.*/
    private org.pdfbox.pdmodel.PDPage pdPage;
    /** The searched entry. */
    private java.lang.String entry;
    /** The bounding rectangles on this page. */
    private java.util.Vector<java.awt.Rectangle.Float> boundingRectangles;
    /** Counter of matches. */
    private int matchCount;
    /** The page text. */
    private java.lang.String pageCharacter = "";
    
    /**
     * Constructor.
     * @param entry the searched entry.
     * @param boundingRectangles the bounding rectangles on this page.
     */
    public EntryStripper() throws java.io.IOException
    {
      super();
    }
    
    /**
     * Overridden method from <code>org.pdfbox.util.PDFTextStripper</code>.
     */
    protected void showCharacter(org.pdfbox.util.TextPosition text)
    {
      try
      {
        if(roughSearch)
        {
          pageCharacter += text.getCharacter();
          return;
        }
        if(!findAll && matchCount > 0)
        {
          return;
        }
        java.lang.String textCharacter = text.getCharacter();
        java.lang.String textToken = text.getCharacter();
        java.lang.String entryToken = entry;
        if(!matchCase)
        {
          textToken = text.getCharacter().toLowerCase();
          entryToken = entry.toLowerCase();
        }
        if(!regEx)
        {
          entryToken = java.util.regex.Pattern.quote(entryToken);
        }
        if(matchWord)
        {
          java.lang.String notWordCaracter = "\\b";
          entryToken = notWordCaracter + entryToken + notWordCaracter;
        }
        org.pdfbox.pdmodel.font.PDFont font = text.getFont();
        float y = pdPage.findMediaBox().getHeight() - text.getY() - text.getFontSize() / 4 * text.getYScale();
        float height = text.getFontSize() * text.getYScale();
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(entryToken);
        java.util.regex.Matcher matcher = pattern.matcher(textToken);
        while(matcher.find())
        {
          matchCount ++;
          java.lang.String preCharacter = textCharacter.substring(0, matcher.start());
          float x = text.getX() + font.getStringWidth(preCharacter) / 1000 * text.getFontSize() * text.getXScale();
          java.lang.String entryCharacter = textCharacter.substring(matcher.start(), matcher.end());
          float width = font.getStringWidth(entryCharacter) / 1000 * text.getFontSize() * text.getXScale();
          boundingRectangles.add(new java.awt.Rectangle.Float(x, y, width, height));
          if(!findAll)
          {
            return;
          }
          java.lang.Thread.yield();
        }
      }
      catch(java.io.IOException exc)
      {
        exc.printStackTrace();
        com.pdfindexer.util.Messages.showException(parentComponent, exc);
      }
    }

    /**
     * Method for processing one page.
     * @param pdPage the processable PDF page.
     * @param entry the searched entry.
     * @param boundingRectangles the bounding rectangles on this page.
     * @return the count of matches.
     */
    public int processStream(org.pdfbox.pdmodel.PDPage pdPage, java.lang.String entry,
      java.util.Vector<java.awt.Rectangle.Float> boundingRectangles)  throws java.io.IOException
    {
      this.pdPage = pdPage;
      this.entry = entry;
      this.boundingRectangles = boundingRectangles;
      this.matchCount = 0;
      super.processStream(pdPage, pdPage.findResources(), pdPage.getContents().getStream());
//System.out.println(pageCharacter);
      return matchCount;
    }
    
    /**
     * Method for calculating the bounding rectangle.
     */
    private java.awt.Rectangle.Float getBounds(org.pdfbox.util.TextPosition text, float textX,
      java.lang.String textCharacter, java.lang.String textToken, java.lang.String entryToken)
    {
      java.awt.Rectangle.Float rectangle = new java.awt.Rectangle.Float(0, 0, 0, 0);
      try
      {
        org.pdfbox.pdmodel.font.PDFont font = text.getFont();
//        int index = textToken.indexOf(entryToken);
//        java.lang.String preCharacter = textCharacter.substring(0, index);
        java.lang.String preCharacter = textCharacter.split(entryToken, -1)[0];
        int index = preCharacter.length();
        float x = textX + font.getStringWidth(preCharacter) / 1000 * text.getFontSize() * text.getXScale();
        float y = pdPage.findMediaBox().getHeight() - text.getY() - text.getFontSize() / 4 * text.getYScale();
System.out.println(textCharacter);
        java.lang.String entryCharacter = textCharacter.substring(index, index + entryToken.length());
        float width = font.getStringWidth(entryCharacter) / 1000 * text.getFontSize() * text.getXScale();
        float height = text.getFontSize() * text.getYScale();
        rectangle = new java.awt.Rectangle.Float(x, y, width, height);
      }
      catch(java.io.IOException exc)
      {
        exc.printStackTrace();
        com.pdfindexer.util.Messages.showException(parentComponent, exc);
      }
      return rectangle;
    }
  }
  
  /** The password of the document, if needed. */
  private java.lang.String password;
  /** The searchable entries. */
  private java.util.Vector<java.lang.String> entries;
  /** The URL of the searched PDF file. */
  private java.net.URL pdfURL;
  /** Shows if the search is case sensitive. */
  private boolean matchCase;
  /** Shows if the search is word sensitive. */
  private boolean matchWord;
  /** Shows if the entries are regular expressions. */
  private boolean regEx;
  /** Shows if the first match or all matches has to be retreaved per page. */
  private boolean findAll;
  /** Shows if the search has to be done on the cummulated text per page. */
  private boolean roughSearch;
  /** The intervals of the indexing. */
  private java.util.Vector<Interval> intervals;
  /** The visualising progress bar or null of there is no. */
  private javax.swing.JProgressBar progressBar;
  /** The visualising editor pane or null of there is no. */
  private javax.swing.JEditorPane editorPane;
  /** The parent component or null of there is no. */
  private java.awt.Component parentComponent;
  /** The results of indexing. */
  private final java.util.Vector<Index> indexes;
  /** The copletion indicator. */
  private boolean isCompleted = false;
    
  /**
   * Constructor.
   * @param pdfURL the URL of the searched PDF file.
   * @param password the encryption password, if there is.
   * @param entries a vector of searchable entries.
   * @param matchCase if true, the search is case sensitive.
   * @param matchWord if true, the search is word sensitive.
   * @param regEx if true, the entries are regular expressions.
   * @param findAll if true, the search retrieves all matches on one page; the first match otherwise.
   * @param roughSearch if true, the search has to be done on the cummulated texts per page.
   * @param intervals the intervals of the document.
   * @param progressBar a progress bar for visualizing the search in progress.
   * @param editorPane the result visualizing pane.
   * @param parentComponent the parent component of this finder.
   */
  public Indexer_4(java.net.URL pdfURL, java.lang.String password, java.util.Vector<java.lang.String> entries,
    boolean matchCase, boolean matchWord, boolean regEx, boolean findAll, boolean roughSearch, java.util.Vector<Interval> intervals,
    javax.swing.JProgressBar progressBar, javax.swing.JEditorPane editorPane, java.awt.Component parentComponent)
  {
    this.pdfURL = pdfURL;
    this.password = password;
    this.entries = entries;
    this.matchCase = matchCase;
    this.matchWord = matchWord;
    this.regEx = regEx;
    this.roughSearch = roughSearch;
    this.findAll = findAll;
    this.intervals = intervals;
    this.progressBar = progressBar;
    this.editorPane = editorPane;
    this.parentComponent = parentComponent;
    indexes = new java.util.Vector<Index>();
    // Set the logging for the stripper.
    java.lang.System.setProperty("log4j.configuration", "resources/log4j.xml");
  }

  /**
   * Method for starting the indexer.
   */
  public void start()
  {
    new java.util.Timer().schedule(this, new java.util.Date());
  }
  
  /**
   * Method for stopping the indexer.
   */
  public void stop()
  {
    isCompleted = true;
  }
  
  /**
   * Method for getting the comleted state.
   * @return true, if the find was comleted.
   */
  public boolean isCompleted()
  {
    return isCompleted;
  }
  
  /**
   * Method for getting the indexes.
   * @return the results of the indexing (partial or complete results).
   */
  public java.util.Vector<Index> getIndexes()
  {
    return indexes;
  }
  
  /**
   * Implemented method from interface <CODE>java.util.TimerTask</CODE>.
   */
  public void run()
  {
    isCompleted = false;
    java.io.InputStream inputStream = null;
    org.pdfbox.pdmodel.PDDocument pdDocument = null;
    try
    {
      if(editorPane != null)
      {
        editorPane.setText("<html><head><style type=\"text/css\">p {margin-top: 1}</style></head><body></body></html>");
      }
      inputStream = pdfURL.openStream();
      org.pdfbox.pdfparser.PDFParser pdfParser = new org.pdfbox.pdfparser.PDFParser(inputStream);
      pdfParser.parse();
      pdDocument = pdfParser.getPDDocument();
      if (pdDocument.isEncrypted())
      {
        pdDocument.decrypt(password);
      }
      java.util.List<org.pdfbox.pdmodel.PDPage> pdPages = pdDocument.getDocumentCatalog().getAllPages();
      if(intervals.size() == 0)
      {
        intervals.add(new Interval(1, pdPages.size()));
      }
      if(progressBar != null)
      {
        // Count number of processable pages.
        int pageMax = 0;
        for(Interval iteratorInterval : intervals)
        {
          pageMax += iteratorInterval.getToPage() - iteratorInterval.getFromPage() + 1;
        }
        progressBar.setMinimum(0);
        progressBar.setMaximum(pageMax * entries.size());
      }
      for(java.lang.String iteratorEntry : entries)
      {
        java.util.Vector<Destination> destinations = new java.util.Vector<Destination>();
        for(Interval iteratorInterval : intervals)
        {
          for(int page = iteratorInterval.getFromPage(); page <= iteratorInterval.getToPage() ; page++ )
          {
            org.pdfbox.pdmodel.PDPage pdPage = pdPages.get(page - 1);
            java.util.Vector<java.awt.Rectangle.Float> boundingRectangles = new java.util.Vector<java.awt.Rectangle.Float>();
            EntryStripper entryStripper = new EntryStripper();
            if(entryStripper.processStream(pdPage, iteratorEntry, boundingRectangles) > 0)
            {
              destinations.add(new Destination(page, boundingRectangles, iteratorInterval.getPrefix(),
                page - iteratorInterval.getFromPage() + iteratorInterval.getStartPaging(), iteratorInterval.getSuffix(),
                iteratorInterval.isRoman(), iteratorInterval.isUpperCase()));
            }
            progressBar.setValue(progressBar.getValue() + 1);
            // Return, if stopped.
            if(isCompleted)
            {
              pdDocument.close();
              inputStream.close();
              return;
            }
            java.lang.Thread.yield();
          }
        }
        final Index index = new Index(iteratorEntry, destinations);
        indexes.add(index);
        if(editorPane != null)
        {
          com.pdfindexer.util.SwingWorker editorWorker = new com.pdfindexer.util.SwingWorker()
          {
            /**
             * Overridden method from <code>com.pdfindexer.util.SwingWorker</code>.
             */
            public java.lang.Object construct()
            {
              try
              {
                javax.swing.text.html.HTMLDocument htmlDocument = (javax.swing.text.html.HTMLDocument)editorPane.getDocument();
                javax.swing.text.Element bodyElement = htmlDocument.getDefaultRootElement().getElement(1);
                htmlDocument.insertBeforeEnd(bodyElement, index.toHTMLLine());
                editorPane.scrollRectToVisible(new java.awt.Rectangle(0, editorPane.getHeight(), 0 , 0));
              }
              catch(java.lang.Exception exc)
              {
                exc.printStackTrace();
                com.pdfindexer.util.Messages.showException(parentComponent, exc);
              }
              return null;
            }
          };
          editorWorker.setThreadPriority(java.lang.Thread.MIN_PRIORITY); 
          editorWorker.start();
        }
      }
      pdDocument.close();
      inputStream.close();
      isCompleted = true;
    }
    catch(java.lang.Exception exc)
    {
      exc.printStackTrace();
      com.pdfindexer.util.Messages.showException(parentComponent, exc);
    }
    finally
    {
      if(inputStream != null)
      {
        try
        {
          inputStream.close();
        }
        catch(java.lang.Exception exc)
        {
        }
      }
      if(pdDocument != null)
      {
        try
        {
          pdDocument.close();
        }
        catch(java.lang.Exception exc)
        {
        }
      }
    }
  }
  
}
