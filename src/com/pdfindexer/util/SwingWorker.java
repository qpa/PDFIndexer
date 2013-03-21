/*
 * SwingWorker.java
 */

package com.pdfindexer.util;

/**
 * Class for background working.
 * @author Qpa
 */
public abstract class SwingWorker
{
  private java.lang.Object value;  // see getValue(), setValue()
  private java.lang.Integer threadPriority;

  /** 
   * Inner class to maintain reference to current worker thread under separate synchronization control.
   */
  private static class ThreadVar
  {
    private java.lang.Thread thread;
    
    ThreadVar(java.lang.Thread t)
    {
      thread = t;
    }
    
    synchronized Thread get()
    {
      return thread;
    }
    
    synchronized void clear()
    {
      thread = null;
    }
  }

  private ThreadVar threadVar;

  /**
   * Constructor.
   * Start a thread that will call the <code>construct</code> method
   * and then exit.
   */
  public SwingWorker()
  {
    final java.lang.Runnable doFinished = new java.lang.Runnable()
    {
      public void run()
      {
        finished();
      }
    };

    java.lang.Runnable doConstruct = new java.lang.Runnable()
    { 
      public void run()
      {
        try
        {
          setValue(construct());
        }
        finally
        {
          threadVar.clear();
        }
        javax.swing.SwingUtilities.invokeLater(doFinished);
      }
    };

    java.lang.Thread t = new java.lang.Thread(doConstruct);
    threadVar = new ThreadVar(t);
  }

  /** 
   * Get the value produced by the worker thread, or null if it hasn't been constructed yet.
   */
  protected synchronized java.lang.Object getValue()
  { 
    return value; 
  }

  /** 
   * Set the value produced by worker thread 
   */
  private synchronized void setValue(java.lang.Object x)
  { 
    value = x; 
  }

  /** 
   * Compute the value to be returned by the <code>get</code> method. 
   */
  public abstract java.lang.Object construct();

  /**
   * Called on the event dispatching thread (not on the worker thread)
   * after the <code>construct</code> method has returned.
   */
  public void finished() {
  }

  /**
   * A new method that interrupts the worker thread.  Call this method
   * to force the worker to stop what it's doing.
   */
  public void interrupt()
  {
    java.lang.Thread t = threadVar.get();
    if (t != null)
    {
      t.interrupt();
    }
    threadVar.clear();
  }

  /**
   * Return the value created by the <code>construct</code> method.  
   * Returns null if either the constructing thread or the current
   * thread was interrupted before a value was produced.
   * 
   * @return the value created by the <code>construct</code> method
   */
  public java.lang.Object get()
  {
    while (true)
    {  
      java.lang.Thread t = threadVar.get();
      if (t == null)
      {
        return getValue();
      }
      try
      {
        t.join();
      }
      catch (java.lang.InterruptedException e)
      {
        java.lang.Thread.currentThread().interrupt(); // propagate
        return null;
      }
    }
  }

  /**
   * Start the worker thread.
   */
  public void start()
  {
    java.lang.Thread t = threadVar.get();
    if (t != null)
    {
      if( threadPriority != null )
      {
        t.setPriority( threadPriority.intValue() );
      }
      t.start();
    }
  }
  
  /**
   * Set thread prority.
   */
  public void setThreadPriority(int priority)
  {
    threadPriority = new java.lang.Integer(priority);
  }
  
}

