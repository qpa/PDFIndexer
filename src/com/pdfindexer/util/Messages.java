/*
 * Messages.java
 */

package com.pdfindexer.util;

/**
 * Class for showing the various messages.
 * @author Qpa
 */
public class Messages
{
  
  /**
   * Method for showing a Yes-No-Cancel dialog.
   * @param parentComponent the parant of this dialog.
   * @param message the message object.
   * @param title the tile of dialog.
   * @return the choosen button action.
   */
  public static int showYesNoCancel(java.awt.Component parentComponent, java.lang.Object message, java.lang.String title)
  {
    java.lang.String[] buttons = new java.lang.String[3];
    buttons[0] = new com.pdfindexer.util.Action(com.pdfindexer.util.Action.YES).toString();
    buttons[1] = new com.pdfindexer.util.Action(com.pdfindexer.util.Action.NO).toString();
    buttons[2] = new com.pdfindexer.util.Action(com.pdfindexer.util.Action.CANCEL).toString();
    return javax.swing.JOptionPane.showOptionDialog(parentComponent, message, title, javax.swing.JOptionPane.YES_NO_CANCEL_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);
  }
  
  /**
   * Method for showing a Ok-Cancel dialog.
   * @param parentComponent the parant of this dialog.
   * @param message the message object.
   * @param title the tile of dialog.
   * @return the choosen button action.
   */
  public static int showOkCancel(java.awt.Component parentComponent, java.lang.Object message, java.lang.String title)
  {
    java.lang.String[] buttons = new java.lang.String[2];
    buttons[0] = new com.pdfindexer.util.Action(com.pdfindexer.util.Action.OK).toString();
    buttons[1] = new com.pdfindexer.util.Action(com.pdfindexer.util.Action.CANCEL).toString();
    return javax.swing.JOptionPane.showOptionDialog(parentComponent, message, title, javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);
  }
  
  /**
   * Method for showing an Ok dialog.
   * @param parentComponent the parant of this dialog.
   * @param message the message object.
   * @param title the tile of dialog.
   * @return the choosen button action.
   */
  public static int showOk(java.awt.Component parentComponent, java.lang.Object message, java.lang.String title)
  {
    java.lang.String[] buttons = new java.lang.String[1];
    buttons[0] = new com.pdfindexer.util.Action(com.pdfindexer.util.Action.OK).toString();
    return javax.swing.JOptionPane.showOptionDialog(parentComponent, message, title, javax.swing.JOptionPane.OK_OPTION, javax.swing.JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);
  }
  
  /**
   * Method for showing an exception.
   * @param parentComponent the parant of this dialog.
   * @param exception the showable exception.
   */
  public static void showException(java.awt.Component parentComponent, java.lang.Exception exception)
  {
    exception.printStackTrace();
    java.lang.String exceptionText = "<html><head><style type=\"text/css\">body {font-size: 11pt; font-family: Dialog; margin-top: 1}</style></head><body>";
    exceptionText += "<b color=red>" + exception.toString() + "</b>";
    java.lang.StackTraceElement[] exceptionElements = exception.getStackTrace();
    for(int i = 0; i < exceptionElements.length ; i++)
    {
      java.lang.StackTraceElement iteratorElement = exceptionElements[i];
      exceptionText += "<br>-- at " + iteratorElement;
    }
    exceptionText += "</body></html>";
    java.lang.String paneTitle = "Exception";
    javax.swing.JEditorPane exceptionEditorPane = new javax.swing.JEditorPane();
    exceptionEditorPane.setEditable(false);
//    exceptionEditorPane.putClientProperty(javax.swing.JEditorPane.HONOR_DISPLAY_PROPERTIES, java.lang.Boolean.TRUE);
    exceptionEditorPane.setContentType("text/html");
    exceptionEditorPane.setText(exceptionText);
    javax.swing.JScrollPane exceptionScrollPane = new javax.swing.JScrollPane(exceptionEditorPane);
    exceptionEditorPane.scrollRectToVisible(new java.awt.Rectangle(0, 0, 1, 1));
    javax.swing.JOptionPane.showMessageDialog(parentComponent, exceptionScrollPane, paneTitle, javax.swing.JOptionPane.ERROR_MESSAGE);
  }
  
}
