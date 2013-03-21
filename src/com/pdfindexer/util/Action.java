/*
 * Action.java
 */
package com.pdfindexer.util;

/**
 * Class for reprezenting an action.
 * @author Qpa
 */
public class Action implements java.io.Serializable
{
  
  // Constants.
  /** The action names. */
  public final static java.lang.String[] NAMES =
  {
    "Action.File",
      "Action.Open",
        "Action.Open.File", "Action.Open.URL",
      "Action.Save", "Action.Close", "Action.Exit",
    "Action.View",
      "Action.Zoom",
        "Action.Zoom.Page", "Action.Zoom.Size", "Action.Zoom.Width",
      "Action.Go",
        "Action.Go.First", "Action.Go.Previous" , "Action.Go.Next", "Action.Go.Last", "Action.Go.Page",
      "Action.Rotate",
      "Action.Language",
    "Action.Help",
      "Action.Contents", "Action.About",
    "Action.Bookmarks", "Action.Thumbnails", "Action.Indexer",
    "Action.Add", "Action.Remove", "Action.New",
    "Action.Ok", "Action.Cancel", "Action.Yes", "Action.No",
    "Action.Import", "Action.Export", "Action.Start", "Action.Stop", "Action.Insert"
  };
  /** The action icons. */
  private final static java.lang.String[] ICONS =
  {
    null,
      "/resources/images/open.gif",
      null, null,
      "/resources/images/save.gif", null, null,
    null,
      null,
        null, null, null,
    null,
      "/resources/images/first.gif", "/resources/images/previous.gif", "/resources/images/next.gif", "/resources/images/last.gif", "/resources/images/empty.gif",
      "/resources/images/empty.gif",
      "/resources/images/empty.gif",
    null,
      null, null,
    null, null, null,
    null, null, null,
    null, null, null, null,
    null, null, null, null, null
  };
  // The indices in the above arrays.
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int FILE = 0;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int OPEN = 1;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int OPEN_FILE = 2;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int OPEN_URL = 3;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int SAVE = 4;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int CLOSE = 5;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int EXIT = 6;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int VIEW = 7;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int ZOOM = 8;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int PAGE_ZOOM = 9;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int SIZE_ZOOM = 10;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int WIDTH_ZOOM = 11;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int GO = 12;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int FIRST_PAGE = 13;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int PREVIOUS_PAGE = 14;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int NEXT_PAGE = 15;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int LAST_PAGE = 16;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int PAGE = 17;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int ROTATE = 18;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int LANGUAGE = 19;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int HELP = 20;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int CONTENTS = 21;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int ABOUT = 22;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int BOOKMARKS = 23;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int THUMBNAILS = 24;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int INDEXER = 25;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int ADD = 26;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int REMOVE = 27;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int NEW = 28;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int OK = 29;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int CANCEL = 30;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int YES = 31;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int NO = 32;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int IMPORT = 33;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int EXPORT = 34;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int START = 35;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int STOP = 36;
  /** Index in the <code>NAMES</code> and <code>ICONS</code>. */
  public final static int INSERT = 37;
  
  /** The index of action. */
  private int index = -1;
   
  /**
   * Constructor.
   * @param index the index in the NAMES array.
   */
  public Action(int index)
  {
    if(index < 0 || index >= NAMES.length)
    {
      this.index = 0;
    }
    this.index = index;
  }
  
  /**
   * Method for getting the name of the action.
   * @return the name of this action.
   */
  public java.lang.String getName()
  {
    return NAMES[index];
  }
  
  /**
   * Method for getting the icon of the action.
   * @return the icon of this action.
   */
  public javax.swing.ImageIcon getIcon()
  {
    return new javax.swing.ImageIcon(getClass().getResource(ICONS[index]));
  }
  
  /**
   * Method for getting the all existing actions.
   */
  public static java.util.Vector getAllActions()
  {
    java.util.Vector actions = new java.util.Vector();
    for(int i = 0; i < NAMES.length; i++)
    {
      actions.add(new Action(i));
    }
    return actions;
  }
  
  /**
   * Overridden method from <CODE>java.lang.Object</CODE>.
   * @param action An <CODE>elv.util.Action</CODE> object.
   * @return true, if this action equals with the given action.
   */
  public boolean equals(java.lang.Object action)
  {
    boolean isEqual = false;
    if(action != null && action instanceof Action)
    {
      isEqual = getName().equals(((Action)action).getName());
    }
    return isEqual;
  }
  
  /**
   * Overridden method from <CODE>java.lang.Object</CODE>.
   * @return the string reprezentation of this action.
   */
  public java.lang.String toString()
  {
    return com.pdfindexer.util.Util.translate(getName());
  }
  
}
