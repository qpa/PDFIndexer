/*
 * Indexer.java
 */
package com.pdfindexer.util;

/**
 * Class for finding the page numbers of the given entries.
 * @author Qpa
 */
public class Indexer extends java.util.TimerTask
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
    private java.util.Vector boundingRectangles;
    /** Counter of matches. */
    private int matchCount;
    /** The text positions. */
    private java.util.Vector textPositions;
    
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
    protected void showCharacter(org.pdfbox.util.TextPosition textPosition)
    {
      // Trim the splitting "-" and put a space (if needed) at the line end.
      if(textPositions.size() > 0 &&
        ((org.pdfbox.util.TextPosition)textPositions.get(textPositions.size() - 1)).getY() != textPosition.getY())
      {
        org.pdfbox.util.TextPosition previousPosition = (org.pdfbox.util.TextPosition)textPositions.get(textPositions.size() - 1);
        java.lang.String previousCharacter = previousPosition.getCharacter();
        if(previousCharacter.length() > 0 && previousCharacter.charAt(previousCharacter.length() - 1) == '-')
        {
          previousCharacter = previousCharacter.substring(0, previousCharacter.length() - 1);
        }
        else
        {
          previousCharacter = previousCharacter.trim() + " ";
        }
        previousPosition = new org.pdfbox.util.TextPosition(previousPosition.getX(), previousPosition.getY(),
          previousPosition.getXScale(), previousPosition.getYScale(), previousPosition.getWidth(), previousPosition.getWidthOfSpace(),
          previousCharacter, previousPosition.getFont(), previousPosition.getFontSize(), previousPosition.getWordSpacing());
        textPositions.set(textPositions.size() - 1, previousPosition);
      }
      textPositions.add(textPosition);
    }

    /**
     * Method for processing one page.
     * @param pdPage the processable PDF page.
     * @param entry the searched entry.
     * @param boundingRectangles the bounding rectangles on this page.
     * @return the count of matches.
     */
    public int processStream(org.pdfbox.pdmodel.PDPage pdPage, java.lang.String entry, java.util.Vector boundingRectangles)  throws java.io.IOException
    {
      try
      {
        this.pdPage = pdPage;
        this.entry = entry;
        this.boundingRectangles = boundingRectangles;
        this.matchCount = 0;
        textPositions = new java.util.Vector();
        
        super.processStream(pdPage, pdPage.findResources(), pdPage.getContents().getStream());
        
        java.lang.String pageToken = "";        
        for(int i = 0; i < textPositions.size(); i++)
        {
          org.pdfbox.util.TextPosition iteratorPosition = (org.pdfbox.util.TextPosition)textPositions.get(i);
          java.lang.String characterToken = "";
          for(int j = 0; j < iteratorPosition.getCharacter().length(); j++)
          {
            char character = iteratorPosition.getCharacter().charAt(j);
          }
          pageToken += matchStrict ? iteratorPosition.getCharacter() : decode(iteratorPosition.getCharacter());
        }
        java.lang.String entryToken = matchStrict ? entry : decode(entry);
        if(!matchCase)
        {
          pageToken = pageToken.toLowerCase();
          entryToken = entryToken.toLowerCase();
        }
        if(!regEx)
        {
          entryToken = com.pdfindexer.util.Util.quote(entryToken);
        }
        if(matchWord)
        {
          java.lang.String notWordCaracter = "\\b";
          entryToken = notWordCaracter + entryToken + notWordCaracter;
        }
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(entryToken);
        java.util.regex.Matcher matcher = pattern.matcher(pageToken);
        while(matcher.find())
        {
          matchCount ++;
          boolean startFound = false;
          boolean endFound = false;
          java.lang.String preCharacter = "";
          for(int i = 0; i < textPositions.size(); i++)
          {
            org.pdfbox.util.TextPosition iteratorPosition = (org.pdfbox.util.TextPosition)textPositions.get(i);
            java.lang.String iteratorCharacter = iteratorPosition.getCharacter();
            org.pdfbox.pdmodel.font.PDFont font = iteratorPosition.getFont();
            float y = pdPage.findMediaBox().getHeight() - iteratorPosition.getY() - iteratorPosition.getFontSize() / 4 * iteratorPosition.getYScale();
            float height = iteratorPosition.getFontSize() * iteratorPosition.getYScale();
            int start = 0;
            int end = iteratorCharacter.length();
            if(matcher.start() < preCharacter.length() + iteratorCharacter.length())
            {
              if(!startFound)
              {
                startFound = true;
                start = matcher.start() - preCharacter.length();
              }
              if(matcher.end() <= preCharacter.length() + iteratorCharacter.length())
              {
                if(!endFound)
                {
                  endFound = true;
                  end = matcher.end() - preCharacter.length();
                }
              }
              java.lang.String iteratorPreCharacter = iteratorCharacter.substring(0, start);
              float x = iteratorPosition.getX() + font.getStringWidth(iteratorPreCharacter) / 1000 * iteratorPosition.getFontSize() * iteratorPosition.getXScale();
              java.lang.String entryCharacter = iteratorCharacter.substring(start, end);
              float width = font.getStringWidth(entryCharacter) / 1000 * iteratorPosition.getFontSize() * iteratorPosition.getXScale();
              boundingRectangles.add(new java.awt.Rectangle.Float(x, y, width, height));
              if(endFound)
              {
                break;
              }
            }
            preCharacter += iteratorCharacter;
          }
          if(!findAll)
          {
            return matchCount;
          }
          java.lang.Thread.yield();
        }
      }
      catch(java.io.IOException exc)
      {
        exc.printStackTrace();
        com.pdfindexer.util.Messages.showException(parentComponent, exc);
      }
      return matchCount;
    }
    
    /**
     * Method for decoding the various accented characters.
     * @param sString the decodable string.
     * @return the decoded string.
     */
    public java.lang.String decode(java.lang.String aString)
    {
      java.lang.String decodedString = "";
      for(int j = 0; j < aString.length(); j++)
      {
        char character = aString.charAt(j);
        switch(character)
        {
          case '\u00E0':
            decodedString += "á";
            break;
          case '\u00C0':
            decodedString += "Á";
            break;
          case '\u00E8':
            decodedString += "é";
            break;
          case '\u00C8':
            decodedString += "É";
            break;
          case '\u00EC':
            decodedString += "í";
            break;
          case '\u00CC':
            decodedString += "Í";
            break;
          case '\u00F2':
            decodedString += "ó";
            break;
          case '\u00D2':
            decodedString += "Ó";
            break;
          case '\u00F4':
          case '\u00F5':
          case '\u014F':
          case '\u014D':
            decodedString += "ő";
            break;
          case '\u00D4':
          case '\u00D5':
          case '\u014E':
          case '\u014C':
            decodedString += "Ő";
            break;
          case '\u00F9':
            decodedString += "ú";
            break;
          case '\u00D9':
            decodedString += "Ú";
            break;
          case '\u00FB':
          case '\u016D':
          case '\u016B':
          case '\u0169':
            decodedString += "ű";
            break;
          case '\u00DB':
          case '\u016C':
          case '\u016A':
          case '\u0168':
            decodedString += "Ű";
            break;
          default:
            decodedString += character;
        }
      }
      return decodedString;
    }
  }
  
  /** The password of the document, if needed. */
  private java.lang.String password;
  /** The searchable entries. */
  private java.util.Vector entries;
  /** The URL of the searched PDF file. */
  private java.net.URL pdfURL;
  /** Shows if the search is case sensitive. */
  private boolean matchCase;
  /** Shows if the search is word sensitive. */
  private boolean matchWord;
  /** Shows if the search is strictly with the given characters, otherwise the same accentuated characters are decoded in one character. */
  private boolean matchStrict;
  /** Shows if the entries are regular expressions. */
  private boolean regEx;
  /** Shows if the first match or all matches has to be retreaved per page. */
  private boolean findAll;
  /** The intervals of the indexing. */
  private java.util.Vector intervals;
  /** The visualising progress bar or null of there is no. */
  private javax.swing.JProgressBar progressBar;
  /** The visualising editor pane or null of there is no. */
  private javax.swing.JEditorPane editorPane;
  /** The parent component or null of there is no. */
  private java.awt.Component parentComponent;
  /** The results of indexing. */
  private final java.util.Vector indexes;
  /** The copletion indicator. */
  private boolean isCompleted = false;
    
  /**
   * Constructor.
   * @param pdfURL the URL of the searched PDF file.
   * @param password the encryption password, if there is.
   * @param entries a vector of searchable entries.
   * @param matchCase if true, the search is case sensitive.
   * @param matchWord if true, the search is word sensitive.
   * @param matchStrict if true, the search is strict with given characters, otherwise the same accentuated caracters are decoded in one character.
   * @param regEx if true, the entries are regular expressions.
   * @param findAll if true, the search retrieves all matches on one page; the first match otherwise.
   * @param intervals the intervals of the document.
   * @param progressBar a progress bar for visualizing the search in progress.
   * @param editorPane the result visualizing pane.
   * @param parentComponent the parent component of this finder.
   */
  public Indexer(java.net.URL pdfURL, java.lang.String password, java.util.Vector entries,
    boolean matchCase, boolean matchWord, boolean matchStrict, boolean regEx, boolean findAll, java.util.Vector intervals,
    javax.swing.JProgressBar progressBar, javax.swing.JEditorPane editorPane, java.awt.Component parentComponent)
  {
    this.pdfURL = pdfURL;
    this.password = password;
    this.entries = entries;
    this.matchCase = matchCase;
    this.matchWord = matchWord;
    this.matchStrict = matchStrict;
    this.regEx = regEx;
    this.findAll = findAll;
    this.intervals = intervals;
    this.progressBar = progressBar;
    this.editorPane = editorPane;
    this.parentComponent = parentComponent;
    indexes = new java.util.Vector();
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
  public java.util.Vector getIndexes()
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
        editorPane.setText("<html><head><style type=\"text/css\">p {font-size: 11pt; font-family: Dialog; margin-top: 1}</style></head><body> </body></html>");
      }
      inputStream = pdfURL.openStream();
      org.pdfbox.pdfparser.PDFParser pdfParser = new org.pdfbox.pdfparser.PDFParser(inputStream);
      pdfParser.parse();
      pdDocument = pdfParser.getPDDocument();
      if (pdDocument.isEncrypted())
      {
        pdDocument.decrypt(password);
      }
      java.util.List pdPages = pdDocument.getDocumentCatalog().getAllPages();
      if(intervals.size() == 0)
      {
        intervals.add(new Interval(1, pdPages.size()));
      }
      if(progressBar != null)
      {
        // Count number of processable pages.
        int pageMax = 0;
        for(int i = 0; i < intervals.size(); i++)
        {
          Interval iteratorInterval = (Interval)intervals.get(i);
          pageMax += iteratorInterval.getToPage() - iteratorInterval.getFromPage() + 1;
        }
        progressBar.setMinimum(0);
        progressBar.setMaximum(pageMax * entries.size());
      }
      for(int i = 0; i < entries.size(); i++)
      {
        java.lang.String iteratorEntry = (java.lang.String)entries.get(i);
        java.util.Vector destinations = new java.util.Vector();
        for(int j = 0; j < intervals.size(); j++)
        {
          Interval iteratorInterval = (Interval)intervals.get(j);
          for(int page = iteratorInterval.getFromPage(); page <= iteratorInterval.getToPage() ; page++ )
          {
            org.pdfbox.pdmodel.PDPage pdPage = (org.pdfbox.pdmodel.PDPage)pdPages.get(page - 1);
            java.util.Vector boundingRectangles = new java.util.Vector();
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
                javax.swing.text.Element rootElement = htmlDocument.getDefaultRootElement();
                javax.swing.text.Element bodyElement = htmlDocument.getDefaultRootElement().getElement(1);
                if(indexes.size() == 1)
                {
                  htmlDocument.insertAfterStart(bodyElement, index.toHTMLLine());
                }
                else
                {
                  javax.swing.text.Element paragraphElement = bodyElement.getElement(bodyElement.getElementCount() - 1);
                  htmlDocument.insertAfterEnd(paragraphElement, index.toHTMLLine());
                }
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
