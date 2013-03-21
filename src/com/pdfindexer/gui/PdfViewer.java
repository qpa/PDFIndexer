/*
 * PdfViewer.java
 */
package com.pdfindexer.gui;

/**
 * Class for PDF document viewing.
 * @author Qpa
 */
public class PdfViewer extends javax.swing.JFrame
{
  // Constants.
  /** The application name. */
  private final static java.lang.String NAME = "PDFIndexer";
  /** The application title. */
  private final static java.lang.String TITLE = "Title";
  /** The password tile. */
  public final static java.lang.String PASSWORD = "Title.Password";
  /** The properties tile. */
  public final static java.lang.String PROPERTIES = "Title.Properties";
  /** The change message. */
  public final static java.lang.String CHANGE_MESSAGE = "Message.Change";
  /** Horizontal gap between the pane and the page. */
  private final static int HORIZONTAL_INSET = 10;
  /** Vertical gap between the pane and the page. */
  private final static int VERTICAL_INSET = 10;
  /** Page thumbnail height. */
  private final static int THUMBNAIL_HEIGHT = 80;
  /** Page thumbnail width. */
  private final static int THUMBNAIL_WIDTH = 60;
  /** Predefined zoom values. */
  public final static java.lang.String[] ZOOM_VALUES = {"25%", "50%", "75%", "100%", "125%", "150%", "200%", "250%", "500%", "750%", "1000%",
    com.pdfindexer.util.Action.NAMES[com.pdfindexer.util.Action.PAGE_ZOOM],
    com.pdfindexer.util.Action.NAMES[com.pdfindexer.util.Action.SIZE_ZOOM],
    com.pdfindexer.util.Action.NAMES[com.pdfindexer.util.Action.WIDTH_ZOOM]};
  // Indices in the above array.
  /** Index in the <code>ZOOM_VALUES</code>. */
  public final static int PAGE = 11;
  /** Index in the <code>ZOOM_VALUES</code>. */
  public final static int SIZE = 12;
  /** Index in the <code>ZOOM_VALUES</code>. */
  public final static int WIDTH = 13;
   /** Predefined rotation values. */
  private final static java.lang.String[] ROTATION_VALUES = {"0°", "90°", "180°", "270°"};
  
  // Variables.
  /** The decoder of the document. */
  private org.jpedal.PdfDecoder pdfDecoder;
  /** The password of the document, if needed. */
  private java.lang.String password;
  /** The document URL.*/
  java.net.URL documentURL;
  /** The current page. */
  private int currentPage = 0;
  /** The current zoom value. */
  private float zoom = 1; // 1 = 100%.
  /** The localized predefined zoom values. */
  private java.lang.String[] zoomValues;
  /** The current rotation value. */
  private int rotation = 0; // 0 = 0 degree.
  /** The page decoding status bar. */
  private org.jpedal.io.StatusBar statusBar;
  /** The current crop box. */
  private java.awt.Rectangle cropBox;
  /** The current media box. */
  private java.awt.Rectangle mediaBox;
  /** The annotations. */
  private org.jpedal.objects.PdfAnnots annotations;
  /** The application properties file. */
  private java.io.File propertiesFile;
  /** The application properties. */
  private java.util.Properties applicationProperties;
  /** The new language locale. */
  private java.util.Locale newLocale;
  /** This component: for inner class usage. */
  private java.awt.Component thisComponent;
  /** The wheel moving counter. */
  private int wheelMovedCount = 1;
  
  /**
   * Constructor.
   */
  public PdfViewer()
  {
    try
    {
      thisComponent = this;
      zoomValues = ZOOM_VALUES;
      zoomValues[PAGE] = com.pdfindexer.util.Util.translate(zoomValues[PAGE]);
      zoomValues[SIZE] = com.pdfindexer.util.Util.translate(zoomValues[SIZE]);
      zoomValues[WIDTH] = com.pdfindexer.util.Util.translate(zoomValues[WIDTH]);
      try
      {
        javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
      }
       catch(java.lang.Exception ex)
      {
        javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getCrossPlatformLookAndFeelClassName());
      }
      // Load properties.
      java.io.File homeDir = new java.io.File(java.lang.System.getProperty("user.home"));
      java.io.File propertiesDir = new java.io.File(homeDir, "." + NAME);
      propertiesFile = new java.io.File(propertiesDir, NAME + ".properties");
      applicationProperties = new java.util.Properties();
      if(propertiesFile.exists())
      {
        applicationProperties.load(new java.io.FileInputStream(propertiesFile));
        com.pdfindexer.util.Util.setActualLocale(com.pdfindexer.util.Util.parseLocale(applicationProperties.getProperty("Language.Locale", "hu")));
        zoom = java.lang.Float.parseFloat(applicationProperties.getProperty("Zoom.Value", "1"));
      }
      else
      {
        propertiesDir.mkdir();
        propertiesFile.createNewFile();
        com.pdfindexer.util.Messages.showOk(thisComponent, com.pdfindexer.util.Util.translate(PROPERTIES) + ":\n" + propertiesFile.toString(), "");
      }
      setIconImage(new javax.swing.ImageIcon(getClass().getResource("/resources/images/book_open.gif")).getImage());
      setTitle(com.pdfindexer.util.Util.translate(TITLE));
      initComponents();
      initPdf();
      // Set help broker.
      java.lang.String helpSetName = "resources/help/help.hs";
      java.net.URL helpSetURL = javax.help.HelpSet.findHelpSet(getClass().getClassLoader(), helpSetName, com.pdfindexer.util.Util.getActualLocale());
      javax.help.HelpSet helpSet = new javax.help.HelpSet(null, helpSetURL);
      javax.help.HelpBroker helpBroker = helpSet.createHelpBroker();
      contentsMenuItem.addActionListener(new javax.help.CSH.DisplayHelpFromSource(helpBroker));
      // Set dimension and locations.
      setLocation(com.pdfindexer.util.Util.parsePoint(applicationProperties.getProperty("Frame.Location", "0, 0")));
      setSize(com.pdfindexer.util.Util.parseDimension(applicationProperties.getProperty("Frame.Size", "800, 600")));
      pdfSplitPane.setDividerLocation(java.lang.Integer.parseInt(applicationProperties.getProperty("Divider.Location", "300")));
    }
    catch(java.lang.Exception exc)
    {
      com.pdfindexer.util.Messages.showException(thisComponent, exc);
    }
  }
  
  /**
   * This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the FormEditor.
   */
  // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
  private void initComponents()
  {
    thumbnailButtonGroup = new javax.swing.ButtonGroup();
    topToolBar = new javax.swing.JToolBar();
    fileToolBar = new javax.swing.JToolBar();
    openButton = new javax.swing.JButton();
    saveButton = new javax.swing.JButton();
    viewToolBar = new javax.swing.JToolBar();
    zoomComboBox = new javax.swing.JComboBox();
    rotationComboBox = new javax.swing.JComboBox();
    goToolBar = new javax.swing.JToolBar();
    firstButton = new javax.swing.JButton();
    previousButton = new javax.swing.JButton();
    pageCountTextField = new javax.swing.JTextField();
    nextButton = new javax.swing.JButton();
    lastButton = new javax.swing.JButton();
    pdfSplitPane = new javax.swing.JSplitPane();
    navigationTabbedPane = new javax.swing.JTabbedPane();
    bookmarkScrollPane = new javax.swing.JScrollPane();
    bookmarkTree = new javax.swing.JTree();
    thumbnailScrollPane = new javax.swing.JScrollPane();
    thumbnailToolBar = new javax.swing.JToolBar();
    indexPanel = new javax.swing.JPanel();
    pdfScrollPane = new javax.swing.JScrollPane();
    bottomPanel = new javax.swing.JPanel();
    statusLabel = new javax.swing.JLabel();
    progressPanel = new javax.swing.JPanel();
    menuBar = new javax.swing.JMenuBar();
    fileMenu = new javax.swing.JMenu();
    openMenuItem = new javax.swing.JMenuItem();
    saveMenuItem = new javax.swing.JMenuItem();
    closeMenuItem = new javax.swing.JMenuItem();
    fileSeparator = new javax.swing.JSeparator();
    exitMenuItem = new javax.swing.JMenuItem();
    viewMenu = new javax.swing.JMenu();
    zoomMenuItem = new javax.swing.JMenuItem();
    pageZoomMenuItem = new javax.swing.JMenuItem();
    sizeZoomMenuItem = new javax.swing.JMenuItem();
    widthZoomMenuItem = new javax.swing.JMenuItem();
    topViewSeparator = new javax.swing.JSeparator();
    goMenu = new javax.swing.JMenu();
    firstMenuItem = new javax.swing.JMenuItem();
    previousMenuItem = new javax.swing.JMenuItem();
    nextMenuItem = new javax.swing.JMenuItem();
    lastMenuItem = new javax.swing.JMenuItem();
    pageMenuItem = new javax.swing.JMenuItem();
    rotateMenu = new javax.swing.JMenu();
    zeroMenuItem = new javax.swing.JMenuItem();
    ninetyMenuItem = new javax.swing.JMenuItem();
    oneeightyMenuItem = new javax.swing.JMenuItem();
    twoseventyMenuItem = new javax.swing.JMenuItem();
    languageMenuItem = new javax.swing.JMenuItem();
    helpMenu = new javax.swing.JMenu();
    contentsMenuItem = new javax.swing.JMenuItem();
    helpSeparator = new javax.swing.JSeparator();
    aboutMenuItem = new javax.swing.JMenuItem();

    addWindowListener(new java.awt.event.WindowAdapter()
    {
      public void windowClosing(java.awt.event.WindowEvent evt)
      {
        formWindowClosing(evt);
      }
    });

    topToolBar.setFloatable(false);
    openButton.setIcon(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.OPEN).getIcon());
    openButton.setToolTipText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.OPEN).toString());
    openButton.setBorderPainted(false);
    openButton.setFocusable(false);
    openButton.setMargin(new java.awt.Insets(1, 1, 1, 1));
    openButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        openButtonActionPerformed(evt);
      }
    });

    fileToolBar.add(openButton);

    saveButton.setIcon(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.SAVE).getIcon());
    saveButton.setToolTipText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.SAVE).toString());
    saveButton.setBorderPainted(false);
    saveButton.setEnabled(false);
    saveButton.setFocusable(false);
    saveButton.setMargin(new java.awt.Insets(1, 1, 1, 1));
    saveButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        saveButtonActionPerformed(evt);
      }
    });

    fileToolBar.add(saveButton);

    topToolBar.add(fileToolBar);

    zoomComboBox.setEditable(true);
    zoomComboBox.setMaximumRowCount(ZOOM_VALUES.length);
    zoomComboBox.setModel(new javax.swing.DefaultComboBoxModel(zoomValues));
    zoomComboBox.setSelectedItem((int)(zoom * 100) + "%");
    zoomComboBox.setToolTipText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.ZOOM).toString());
    zoomComboBox.setEnabled(false);
    zoomComboBox.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        zoomComboBoxActionPerformed(evt);
      }
    });

    viewToolBar.add(zoomComboBox);

    rotationComboBox.setMaximumRowCount(ROTATION_VALUES.length);
    rotationComboBox.setModel(new javax.swing.DefaultComboBoxModel(ROTATION_VALUES));
    rotationComboBox.setSelectedItem(rotation + "°");
    rotationComboBox.setToolTipText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.ROTATE).toString());
    rotationComboBox.setEnabled(false);
    rotationComboBox.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        rotationComboBoxActionPerformed(evt);
      }
    });

    viewToolBar.add(rotationComboBox);

    topToolBar.add(viewToolBar);

    firstButton.setIcon(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.FIRST_PAGE).getIcon());
    firstButton.setToolTipText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.FIRST_PAGE).toString());
    firstButton.setBorderPainted(false);
    firstButton.setEnabled(false);
    firstButton.setMargin(new java.awt.Insets(1, 1, 1, 1));
    firstButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        firstButtonActionPerformed(evt);
      }
    });

    goToolBar.add(firstButton);

    previousButton.setIcon(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.PREVIOUS_PAGE).getIcon());
    previousButton.setToolTipText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.PREVIOUS_PAGE).toString());
    previousButton.setBorderPainted(false);
    previousButton.setEnabled(false);
    previousButton.setMargin(new java.awt.Insets(1, 1, 1, 1));
    previousButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        previousButtonActionPerformed(evt);
      }
    });

    goToolBar.add(previousButton);

    pageCountTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
    pageCountTextField.setEnabled(false);
    pageCountTextField.setPreferredSize(new java.awt.Dimension(100, 19));
    pageCountTextField.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        pageCountTextFieldActionPerformed(evt);
      }
    });

    goToolBar.add(pageCountTextField);

    nextButton.setIcon(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.NEXT_PAGE).getIcon());
    nextButton.setToolTipText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.NEXT_PAGE).toString());
    nextButton.setBorderPainted(false);
    nextButton.setEnabled(false);
    nextButton.setMargin(new java.awt.Insets(1, 1, 1, 1));
    nextButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        nextButtonActionPerformed(evt);
      }
    });

    goToolBar.add(nextButton);

    lastButton.setIcon(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.LAST_PAGE).getIcon());
    lastButton.setToolTipText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.LAST_PAGE).toString());
    lastButton.setBorderPainted(false);
    lastButton.setEnabled(false);
    lastButton.setMargin(new java.awt.Insets(1, 1, 1, 1));
    lastButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        lastButtonActionPerformed(evt);
      }
    });

    goToolBar.add(lastButton);

    topToolBar.add(goToolBar);

    getContentPane().add(topToolBar, java.awt.BorderLayout.NORTH);

    pdfSplitPane.setDividerLocation(300);
    pdfSplitPane.setOneTouchExpandable(true);
    pdfSplitPane.addPropertyChangeListener(new java.beans.PropertyChangeListener()
    {
      public void propertyChange(java.beans.PropertyChangeEvent evt)
      {
        pdfSplitPanePropertyChange(evt);
      }
    });

    bookmarkTree.setToolTipText("");
    bookmarkTree.setCellRenderer(new BookmarkTreeCellRenderer());
    bookmarkTree.setRootVisible(false);
    bookmarkTree.setShowsRootHandles(true);
    bookmarkTree.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseClicked(java.awt.event.MouseEvent evt)
      {
        bookmarkTreeMouseClicked(evt);
      }
    });

    bookmarkScrollPane.setViewportView(bookmarkTree);

    navigationTabbedPane.addTab(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.BOOKMARKS).toString(), bookmarkScrollPane);

    thumbnailScrollPane.addPropertyChangeListener(new java.beans.PropertyChangeListener()
    {
      public void propertyChange(java.beans.PropertyChangeEvent evt)
      {
        thumbnailScrollPanePropertyChange(evt);
      }
    });

    thumbnailToolBar.setFloatable(false);
    thumbnailScrollPane.setViewportView(thumbnailToolBar);

    navigationTabbedPane.addTab(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.THUMBNAILS).toString(), thumbnailScrollPane);

    indexPanel.setLayout(new java.awt.BorderLayout());

    navigationTabbedPane.addTab(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.INDEXER).toString(), indexPanel);

    pdfSplitPane.setLeftComponent(navigationTabbedPane);

    pdfScrollPane.addMouseWheelListener(new java.awt.event.MouseWheelListener()
    {
      public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt)
      {
        pdfScrollPaneMouseWheelMoved(evt);
      }
    });

    pdfSplitPane.setRightComponent(pdfScrollPane);

    getContentPane().add(pdfSplitPane, java.awt.BorderLayout.CENTER);

    bottomPanel.setLayout(new java.awt.BorderLayout());

    bottomPanel.add(statusLabel, java.awt.BorderLayout.CENTER);

    progressPanel.setLayout(new javax.swing.BoxLayout(progressPanel, javax.swing.BoxLayout.X_AXIS));

    progressPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5));
    progressPanel.setPreferredSize(new java.awt.Dimension(100, 18));
    bottomPanel.add(progressPanel, java.awt.BorderLayout.EAST);

    getContentPane().add(bottomPanel, java.awt.BorderLayout.SOUTH);

    fileMenu.setText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.FILE).toString());
    openMenuItem.setText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.OPEN).toString() + "...");
    openMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        openMenuItemActionPerformed(evt);
      }
    });

    fileMenu.add(openMenuItem);

    saveMenuItem.setText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.SAVE).toString() + "...");
    saveMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        saveMenuItemActionPerformed(evt);
      }
    });

    fileMenu.add(saveMenuItem);

    closeMenuItem.setText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.CLOSE).toString());
    closeMenuItem.setEnabled(false);
    closeMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        closeMenuItemActionPerformed(evt);
      }
    });

    fileMenu.add(closeMenuItem);

    fileMenu.add(fileSeparator);

    exitMenuItem.setText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.EXIT).toString());
    exitMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        exitMenuItemActionPerformed(evt);
      }
    });

    fileMenu.add(exitMenuItem);

    menuBar.add(fileMenu);

    viewMenu.setText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.VIEW).toString());
    viewMenu.setEnabled(false);
    zoomMenuItem.setText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.ZOOM).toString() + "...");
    zoomMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        zoomMenuItemActionPerformed(evt);
      }
    });

    viewMenu.add(zoomMenuItem);

    pageZoomMenuItem.setText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.PAGE_ZOOM).toString());
    pageZoomMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        pageZoomMenuItemActionPerformed(evt);
      }
    });

    viewMenu.add(pageZoomMenuItem);

    sizeZoomMenuItem.setText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.SIZE_ZOOM).toString());
    sizeZoomMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        sizeZoomMenuItemActionPerformed(evt);
      }
    });

    viewMenu.add(sizeZoomMenuItem);

    widthZoomMenuItem.setText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.WIDTH_ZOOM).toString());
    widthZoomMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        widthZoomMenuItemActionPerformed(evt);
      }
    });

    viewMenu.add(widthZoomMenuItem);

    viewMenu.add(topViewSeparator);

    goMenu.setText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.GO).toString());
    firstMenuItem.setIcon(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.FIRST_PAGE).getIcon());
    firstMenuItem.setText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.FIRST_PAGE).toString());
    firstMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        firstMenuItemActionPerformed(evt);
      }
    });

    goMenu.add(firstMenuItem);

    previousMenuItem.setIcon(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.PREVIOUS_PAGE).getIcon());
    previousMenuItem.setText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.PREVIOUS_PAGE).toString());
    previousMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        previousMenuItemActionPerformed(evt);
      }
    });

    goMenu.add(previousMenuItem);

    nextMenuItem.setIcon(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.NEXT_PAGE).getIcon());
    nextMenuItem.setText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.NEXT_PAGE).toString());
    nextMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        nextMenuItemActionPerformed(evt);
      }
    });

    goMenu.add(nextMenuItem);

    lastMenuItem.setIcon(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.LAST_PAGE).getIcon());
    lastMenuItem.setText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.LAST_PAGE).toString());
    lastMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        lastMenuItemActionPerformed(evt);
      }
    });

    goMenu.add(lastMenuItem);

    pageMenuItem.setIcon(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.PAGE).getIcon());
    pageMenuItem.setText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.PAGE).toString() + "...");
    pageMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        pageMenuItemActionPerformed(evt);
      }
    });

    goMenu.add(pageMenuItem);

    viewMenu.add(goMenu);

    rotateMenu.setText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.ROTATE).toString());
    zeroMenuItem.setText("0\u00b0");
    zeroMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        zeroMenuItemActionPerformed(evt);
      }
    });

    rotateMenu.add(zeroMenuItem);

    ninetyMenuItem.setText("90\u00b0");
    ninetyMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        ninetyMenuItemActionPerformed(evt);
      }
    });

    rotateMenu.add(ninetyMenuItem);

    oneeightyMenuItem.setText("180\u00b0");
    oneeightyMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        oneeightyMenuItemActionPerformed(evt);
      }
    });

    rotateMenu.add(oneeightyMenuItem);

    twoseventyMenuItem.setText("270\u00b0");
    twoseventyMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        twoseventyMenuItemActionPerformed(evt);
      }
    });

    rotateMenu.add(twoseventyMenuItem);

    viewMenu.add(rotateMenu);

    languageMenuItem.setText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.LANGUAGE).toString() + "...");
    languageMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        languageMenuItemActionPerformed(evt);
      }
    });

    viewMenu.add(languageMenuItem);

    menuBar.add(viewMenu);

    helpMenu.setText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.HELP).toString());
    contentsMenuItem.setText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.CONTENTS).toString());
    helpMenu.add(contentsMenuItem);

    helpMenu.add(helpSeparator);

    aboutMenuItem.setText(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.ABOUT).toString());
    aboutMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        aboutMenuItemActionPerformed(evt);
      }
    });

    helpMenu.add(aboutMenuItem);

    menuBar.add(helpMenu);

    setJMenuBar(menuBar);

    java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    setBounds((screenSize.width-800)/2, (screenSize.height-600)/2, 800, 600);
  }// </editor-fold>//GEN-END:initComponents

  private void languageMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_languageMenuItemActionPerformed
  {//GEN-HEADEREND:event_languageMenuItemActionPerformed
    try
    {
      java.util.Vector locales = com.pdfindexer.util.Util.getAvailableDictionaries();
      java.lang.String[] localeStrings = new java.lang.String[locales.size()];
      int selectedIndex = 0;
      for(int i = 0; i < locales.size(); i++)
      {
        localeStrings[i] = ((java.util.Locale)locales.get(i)).getDisplayName();
        if(com.pdfindexer.util.Util.getActualLocale().getDisplayName().equals(localeStrings[i]))
        {
          selectedIndex = i;
        }
      }
      javax.swing.JComboBox languageComboBox = new javax.swing.JComboBox(localeStrings);
      languageComboBox.setSelectedIndex(selectedIndex);
      javax.swing.JLabel warnigLabel = new javax.swing.JLabel(com.pdfindexer.util.Util.translate(CHANGE_MESSAGE));
      warnigLabel.setForeground(new java.awt.Color(128, 0, 0));
      javax.swing.JPanel languagePanel = new javax.swing.JPanel();
      languagePanel.setLayout(new java.awt.BorderLayout(0, 10));
      languagePanel.add(languageComboBox, java.awt.BorderLayout.NORTH);
      languagePanel.add(warnigLabel, java.awt.BorderLayout.CENTER);
      int returnOption = com.pdfindexer.util.Messages.showOkCancel(thisComponent, languagePanel, new com.pdfindexer.util.Action(com.pdfindexer.util.Action.LANGUAGE).toString());
      if(returnOption == javax.swing.JOptionPane.OK_OPTION)
      {
        java.lang.String localeString = (java.lang.String)languageComboBox.getSelectedItem();
        if(newLocale == null || !localeString.equals(newLocale.getDisplayName()))
        {
          newLocale = (java.util.Locale)locales.get(languageComboBox.getSelectedIndex());
        }
      }
    }
    catch(java.lang.Exception exc)
    {
      com.pdfindexer.util.Messages.showException(thisComponent, exc);
    }
  }//GEN-LAST:event_languageMenuItemActionPerformed

  private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_openMenuItemActionPerformed
  {//GEN-HEADEREND:event_openMenuItemActionPerformed
    openFile();
  }//GEN-LAST:event_openMenuItemActionPerformed

  private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveMenuItemActionPerformed
  {//GEN-HEADEREND:event_saveMenuItemActionPerformed
    saveFile();
  }//GEN-LAST:event_saveMenuItemActionPerformed

  private void saveButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveButtonActionPerformed
  {//GEN-HEADEREND:event_saveButtonActionPerformed
    saveFile();
  }//GEN-LAST:event_saveButtonActionPerformed

  private void openButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_openButtonActionPerformed
  {//GEN-HEADEREND:event_openButtonActionPerformed
    openFile();
  }//GEN-LAST:event_openButtonActionPerformed

  private void thumbnailScrollPanePropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_thumbnailScrollPanePropertyChange
  {//GEN-HEADEREND:event_thumbnailScrollPanePropertyChange
//    setThumbnails();
  }//GEN-LAST:event_thumbnailScrollPanePropertyChange

  private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_aboutMenuItemActionPerformed
  {//GEN-HEADEREND:event_aboutMenuItemActionPerformed
    javax.swing.JLabel iconLabel = new javax.swing.JLabel(new javax.swing.ImageIcon(getClass().getResource("/resources/images/splash.gif")));
    com.pdfindexer.util.Messages.showOk(thisComponent, iconLabel, new com.pdfindexer.util.Action(com.pdfindexer.util.Action.ABOUT).toString());
  }//GEN-LAST:event_aboutMenuItemActionPerformed

  private void twoseventyMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_twoseventyMenuItemActionPerformed
  {//GEN-HEADEREND:event_twoseventyMenuItemActionPerformed
    rotatePage(270);
  }//GEN-LAST:event_twoseventyMenuItemActionPerformed

  private void oneeightyMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_oneeightyMenuItemActionPerformed
  {//GEN-HEADEREND:event_oneeightyMenuItemActionPerformed
    rotatePage(180);
  }//GEN-LAST:event_oneeightyMenuItemActionPerformed

  private void ninetyMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ninetyMenuItemActionPerformed
  {//GEN-HEADEREND:event_ninetyMenuItemActionPerformed
    rotatePage(90);
  }//GEN-LAST:event_ninetyMenuItemActionPerformed

  private void zeroMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zeroMenuItemActionPerformed
  {//GEN-HEADEREND:event_zeroMenuItemActionPerformed
    rotatePage(0);
  }//GEN-LAST:event_zeroMenuItemActionPerformed

  private void lastMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_lastMenuItemActionPerformed
  {//GEN-HEADEREND:event_lastMenuItemActionPerformed
    decodePage(pdfDecoder.getPageCount());
  }//GEN-LAST:event_lastMenuItemActionPerformed

  private void pageMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pageMenuItemActionPerformed
  {//GEN-HEADEREND:event_pageMenuItemActionPerformed
    javax.swing.JSpinner pageSpinner = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(currentPage, 1, pdfDecoder.getPageCount(), 1));
    int returnOption = com.pdfindexer.util.Messages.showOkCancel(thisComponent, pageSpinner, new com.pdfindexer.util.Action(com.pdfindexer.util.Action.PAGE).toString());
    if(returnOption == javax.swing.JOptionPane.OK_OPTION)
    {
      decodePage(((java.lang.Integer)pageSpinner.getValue()).intValue());
    }
  }//GEN-LAST:event_pageMenuItemActionPerformed

  private void nextMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_nextMenuItemActionPerformed
  {//GEN-HEADEREND:event_nextMenuItemActionPerformed
    decodePage(currentPage + 1);
  }//GEN-LAST:event_nextMenuItemActionPerformed

  private void previousMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_previousMenuItemActionPerformed
  {//GEN-HEADEREND:event_previousMenuItemActionPerformed
    decodePage(currentPage - 1);
  }//GEN-LAST:event_previousMenuItemActionPerformed

  private void firstMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_firstMenuItemActionPerformed
  {//GEN-HEADEREND:event_firstMenuItemActionPerformed
    decodePage(1);
  }//GEN-LAST:event_firstMenuItemActionPerformed

  private void widthZoomMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_widthZoomMenuItemActionPerformed
  {//GEN-HEADEREND:event_widthZoomMenuItemActionPerformed
    zoomPage(zoomValues[WIDTH]);
  }//GEN-LAST:event_widthZoomMenuItemActionPerformed

  private void sizeZoomMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_sizeZoomMenuItemActionPerformed
  {//GEN-HEADEREND:event_sizeZoomMenuItemActionPerformed
    zoomPage(zoomValues[SIZE]);
  }//GEN-LAST:event_sizeZoomMenuItemActionPerformed

  private void pageZoomMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pageZoomMenuItemActionPerformed
  {//GEN-HEADEREND:event_pageZoomMenuItemActionPerformed
    zoomPage(zoomValues[PAGE]);
  }//GEN-LAST:event_pageZoomMenuItemActionPerformed

  private void zoomMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zoomMenuItemActionPerformed
  {//GEN-HEADEREND:event_zoomMenuItemActionPerformed
    javax.swing.JSpinner pageSpinner = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(zoom * 100, 1, 1000, 1));
    int returnOption = com.pdfindexer.util.Messages.showOkCancel(thisComponent, pageSpinner, new com.pdfindexer.util.Action(com.pdfindexer.util.Action.ZOOM).toString());
    if(returnOption == javax.swing.JOptionPane.OK_OPTION)
    {
      zoomPage(pageSpinner.getValue().toString());
    }
  }//GEN-LAST:event_zoomMenuItemActionPerformed

  private void closeMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_closeMenuItemActionPerformed
  {//GEN-HEADEREND:event_closeMenuItemActionPerformed
    try
    {
      closePdf();
    }
    catch(java.lang.Exception exc)
    {
      com.pdfindexer.util.Messages.showException(thisComponent, exc);
    }
  }//GEN-LAST:event_closeMenuItemActionPerformed

  private void pdfSplitPanePropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_pdfSplitPanePropertyChange
  {//GEN-HEADEREND:event_pdfSplitPanePropertyChange
    try
    {
      int columnCount = ((javax.swing.JSplitPane)evt.getSource()).getLeftComponent().getWidth() / THUMBNAIL_HEIGHT;
      columnCount = columnCount == 0 ? 1 : columnCount;
      thumbnailToolBar.setLayout(new java.awt.GridLayout(0, columnCount));
//      setThumbnails();
    }
    catch(java.lang.Exception exc)
    {
      com.pdfindexer.util.Messages.showException(thisComponent, exc);
    }
  }//GEN-LAST:event_pdfSplitPanePropertyChange

  private void rotationComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rotationComboBoxActionPerformed
  {//GEN-HEADEREND:event_rotationComboBoxActionPerformed
    try
    {
      java.lang.String rotationString = ((java.lang.String)((javax.swing.JComboBox)evt.getSource()).getSelectedItem()).split("°")[0].trim();
      int newRotationValue = java.lang.Integer.parseInt(rotationString);
      rotatePage(newRotationValue);
    }
    catch(java.lang.Exception exc)
    {
      com.pdfindexer.util.Messages.showException(thisComponent, exc);
    }
  }//GEN-LAST:event_rotationComboBoxActionPerformed

  private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_exitMenuItemActionPerformed
  {//GEN-HEADEREND:event_exitMenuItemActionPerformed
    exit();
  }//GEN-LAST:event_exitMenuItemActionPerformed

  private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
  {//GEN-HEADEREND:event_formWindowClosing
    exit();
  }//GEN-LAST:event_formWindowClosing

  private void pdfScrollPaneMouseWheelMoved(java.awt.event.MouseWheelEvent evt)//GEN-FIRST:event_pdfScrollPaneMouseWheelMoved
  {//GEN-HEADEREND:event_pdfScrollPaneMouseWheelMoved
    java.awt.Rectangle wiewportRect = ((javax.swing.JScrollPane)evt.getSource()).getViewport().getViewRect();
    int pdfHeight = ((javax.swing.JScrollPane)evt.getSource()).getViewport().getView().getSize().height;
    if(pdfHeight <= wiewportRect.height)
    {
      if(evt.getWheelRotation() < 0 && currentPage > 1)
      {
        decodePage(currentPage - 1);
      }
      else if(evt.getWheelRotation() > 0 && currentPage < pdfDecoder.getPageCount())
      {
        decodePage(currentPage + 1);
      }
    }
    else
    {
      if(wiewportRect.y == 0 && currentPage > 1)
      {
        if(wheelMovedCount == 2)
        {
          decodePage(currentPage - 1, new java.awt.Point(wiewportRect.x, wiewportRect.height));
          wheelMovedCount = 1;
        }
        else
        {
          wheelMovedCount++;
        }
      }
      else if(wiewportRect.y + wiewportRect.height == pdfHeight && currentPage < pdfDecoder.getPageCount())
      {
        if(wheelMovedCount == 2)
        {
          decodePage(currentPage + 1, new java.awt.Point(wiewportRect.x, pdfHeight));
          wheelMovedCount = 1;
        }
        else
        {
          wheelMovedCount++;
        }
      }
    }
  }//GEN-LAST:event_pdfScrollPaneMouseWheelMoved

  private void bookmarkTreeMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_bookmarkTreeMouseClicked
  {//GEN-HEADEREND:event_bookmarkTreeMouseClicked
    javax.swing.JTree tree = (javax.swing.JTree)evt.getSource();
    javax.swing.tree.TreePath treePath = tree.getClosestPathForLocation(evt.getX(), evt.getY());
    if(treePath != null && tree.getPathBounds(treePath).contains(evt.getPoint()))
    {
      Bookmark bookmark = (Bookmark)((javax.swing.tree.DefaultMutableTreeNode)treePath.getLastPathComponent()).getUserObject();
      decodePage(bookmark.getPage(), bookmark.getPoint());
      tree.setSelectionPath(treePath);
    }
  }//GEN-LAST:event_bookmarkTreeMouseClicked

  private void zoomComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zoomComboBoxActionPerformed
  {//GEN-HEADEREND:event_zoomComboBoxActionPerformed
    try
    {
      java.lang.String zoomString = ((java.lang.String)((javax.swing.JComboBox)evt.getSource()).getSelectedItem()).split("%")[0].trim();
      zoomPage(zoomString);
    }
    catch(java.lang.Exception exc)
    {
      com.pdfindexer.util.Messages.showException(thisComponent, exc);
    }
  }//GEN-LAST:event_zoomComboBoxActionPerformed

  private void lastButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_lastButtonActionPerformed
  {//GEN-HEADEREND:event_lastButtonActionPerformed
    decodePage(pdfDecoder.getPageCount());
  }//GEN-LAST:event_lastButtonActionPerformed

  private void nextButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_nextButtonActionPerformed
  {//GEN-HEADEREND:event_nextButtonActionPerformed
    decodePage(currentPage + 1);
  }//GEN-LAST:event_nextButtonActionPerformed

  private void pageCountTextFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pageCountTextFieldActionPerformed
  {//GEN-HEADEREND:event_pageCountTextFieldActionPerformed
    try
    {
      java.lang.String pageString = pageCountTextField.getText().split("/")[0].trim();
      int page = java.lang.Integer.parseInt(pageString);
      decodePage(page);
    }
    catch(java.lang.NumberFormatException exc)
    {
    }
    finally
    {
      pageCountTextField.setText(currentPage + " / " + pdfDecoder.getPageCount());
    }
  }//GEN-LAST:event_pageCountTextFieldActionPerformed

  private void previousButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_previousButtonActionPerformed
  {//GEN-HEADEREND:event_previousButtonActionPerformed
    decodePage(currentPage - 1);
  }//GEN-LAST:event_previousButtonActionPerformed

  private void firstButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_firstButtonActionPerformed
  {//GEN-HEADEREND:event_firstButtonActionPerformed
    decodePage(1);
  }//GEN-LAST:event_firstButtonActionPerformed
  
  /**
   * Method for initializing the PDF viewer.
   * @param documentURL the file URL.
   */
  private void initPdf() throws java.lang.Exception
  {
    topToolBar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 1, 1));
    fileToolBar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 1, 1));
    viewToolBar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 1, 1));
    goToolBar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 1, 1));
    // Document.
    pdfDecoder = new org.jpedal.PdfDecoder();
    pdfDecoder.setPDFBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK, 1));
    pdfDecoder.setInset(HORIZONTAL_INSET, VERTICAL_INSET);
    pdfDecoder.createPageHostspots(new java.lang.String[]{"Other", "Text", "FileAttachment"}, "org/jpedal/examples/simpleviewer/annots/");
    pdfDecoder.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseClicked(java.awt.event.MouseEvent evt)
      {
        highlightPage(null);
        decodeLink(evt.getPoint(), true);
      }
    });
    pdfDecoder.addMouseMotionListener(new java.awt.event.MouseMotionAdapter()
    {
      public void mouseMoved(java.awt.event.MouseEvent evt)
      {
        decodeLink(evt.getPoint(), false);
      }
    });
    pdfScrollPane.getViewport().add(pdfDecoder);
    pdfScrollPane.getVerticalScrollBar().setUnitIncrement(20);
    pdfScrollPane.getHorizontalScrollBar().setUnitIncrement(20);
    statusBar = new org.jpedal.io.StatusBar();
    pdfDecoder.setStatusBarObject(statusBar);
    progressPanel.add(statusBar.getStatusObject());
    statusBar.getStatusObject().setVisible(false);
    ((javax.swing.JProgressBar)statusBar.getStatusObject()).setStringPainted(false);
    // Bookmarks.
    bookmarkTree.getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION);
    javax.swing.tree.DefaultMutableTreeNode rootTreeNode = new javax.swing.tree.DefaultMutableTreeNode("Root");
    ((javax.swing.tree.DefaultTreeModel)bookmarkTree.getModel()).setRoot(rootTreeNode);
    // Thumbnails.
    thumbnailScrollPane.getVerticalScrollBar().setUnitIncrement(THUMBNAIL_HEIGHT);
    thumbnailToolBar.setLayout(new java.awt.GridLayout(pdfDecoder.getPageCount(), 1));
    pdfSplitPane.setVisible(false);
  }
  
  /**
   * Method for closing a PDF document.
   */
  private void closePdf() throws java.lang.Exception
  {
    wheelMovedCount = 1;
    currentPage = 0;
    // Document.
    if(pdfDecoder != null)
    {
      pdfDecoder.closePdfFile();
    }
    // Disable modifications.
    saveMenuItem.setEnabled(false);
    closeMenuItem.setEnabled(false);
    viewMenu.setEnabled(false);
    saveButton.setEnabled(false);
    zoomComboBox.setEnabled(false);
    rotationComboBox.setEnabled(false);
    pageCountTextField.setEnabled(false);
    firstButton.setEnabled(false);
    previousButton.setEnabled(false);
    nextButton.setEnabled(false);
    lastButton.setEnabled(false);
    // Bookmarks.
    javax.swing.tree.DefaultMutableTreeNode rootTreeNode = new javax.swing.tree.DefaultMutableTreeNode("Root");
    ((javax.swing.tree.DefaultTreeModel)bookmarkTree.getModel()).setRoot(rootTreeNode);
    // Thumbnails.
    thumbnailToolBar.removeAll();
    // Indexer.
    indexPanel.removeAll();
    pdfSplitPane.setVisible(false);
    setTitle(com.pdfindexer.util.Util.translate(TITLE));
  }
  
  /**
   * Method for opening a PDF document.
   * @param documentURL the file URL.
   */
  private void openPdf(java.net.URL documentURL) throws java.lang.Exception
  {
    // Document.
    this.documentURL = documentURL;
    pdfDecoder.openPdfFileFromURL(documentURL.toString());
    if (pdfDecoder.isEncrypted() && !pdfDecoder.isFileViewable())
    {
      javax.swing.JPasswordField pwField = new javax.swing.JPasswordField();
      int returnOption = com.pdfindexer.util.Messages.showOkCancel(thisComponent, pwField, com.pdfindexer.util.Util.translate(PASSWORD));
      if(returnOption == javax.swing.JOptionPane.OK_OPTION)
      {
        password = java.lang.String.valueOf(pwField.getPassword());
        pdfDecoder.setEncryptionPassword(password);
        pdfDecoder.verifyAccess();
      }
    }
    // Bookmarks.
    javax.swing.tree.DefaultMutableTreeNode rootTreeNode = new javax.swing.tree.DefaultMutableTreeNode("Root");
    if(pdfDecoder.hasOutline())
    {
      org.w3c.dom.Node node = pdfDecoder.getOutlineAsXML().getFirstChild();
      if(node != null)
      {
        addBookmarks(node, rootTreeNode);
      }
    }
    ((javax.swing.tree.DefaultTreeModel)bookmarkTree.getModel()).setRoot(rootTreeNode);
    // Thumbnails.
    thumbnailScrollPane.getVerticalScrollBar().setUnitIncrement(THUMBNAIL_HEIGHT);
    int columnCount = pdfSplitPane.getLeftComponent().getWidth() / THUMBNAIL_HEIGHT;
    columnCount = columnCount == 0 ? 1 : columnCount;
    thumbnailToolBar.setLayout(new java.awt.GridLayout(0, columnCount));
    java.awt.image.BufferedImage blankThumbnail = new java.awt.image.BufferedImage(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, java.awt.image.BufferedImage.TYPE_INT_RGB);
    java.awt.Graphics2D graphics2d = (java.awt.Graphics2D)blankThumbnail.getGraphics();
    graphics2d.setColor(java.awt.Color.WHITE);
    graphics2d.fill(new java.awt.Rectangle(0,0,THUMBNAIL_WIDTH - 1, THUMBNAIL_HEIGHT - 1));
    graphics2d.setColor(java.awt.Color.BLACK);
    graphics2d.draw(new java.awt.Rectangle(0,0,THUMBNAIL_WIDTH - 1, THUMBNAIL_HEIGHT - 1));
    for(int i = 1; i <= pdfDecoder.getPageCount(); i++)
    {
      final int page = i;
      javax.swing.ImageIcon thumbnailIcon = new javax.swing.ImageIcon(blankThumbnail.getScaledInstance(-1, blankThumbnail.getHeight(), java.awt.Image.SCALE_FAST));
      javax.swing.JToggleButton thumbnailButton = new javax.swing.JToggleButton(com.pdfindexer.util.Util.format(page), thumbnailIcon);
      thumbnailButton.setFocusable(false);
      thumbnailButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
      thumbnailButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      thumbnailButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
      thumbnailButton.addActionListener(new java.awt.event.ActionListener()
      {
        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
          decodePage(page);
        }
      });
      thumbnailButtonGroup.add(thumbnailButton);
      thumbnailToolBar.add(thumbnailButton);
    }
    // Indexer.
    indexPanel.add(new IndexerPanel(documentURL, password, pdfDecoder.getPageCount(), this));
    // First page.
    decodePage(1);
    highlightPage(null);
    // Enable modifications.
    pdfSplitPane.setVisible(true);
    saveMenuItem.setEnabled(true);
    closeMenuItem.setEnabled(true);
    viewMenu.setEnabled(true);
    saveButton.setEnabled(true);
//    setTitle(com.pdfindexer.util.Util.translate(TITLE) + " - [" + documentURL.toString() + "]");
  }
  
  /**
   * Method for openig a document file.
   */
  protected void openFile()
  {
    try
    {
      javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
      javax.swing.filechooser.FileFilter extensionFilter = new javax.swing.filechooser.FileFilter()
      {
        /**
         * Overridden method from <CODE>javax.swing.filechooser.FileFilter</CODE>.
         */
        public boolean accept(java.io.File file)
        {
          boolean isAcceptable = true;
          if(file != null && !file.isDirectory())
          {
            java.lang.String extension = com.pdfindexer.util.Util.getFileExtension(file.getName());
            if(!"pdf".equals(extension))
            {
              isAcceptable = false;
            }
          }
          return isAcceptable;
        }

        /**
         * Overridden method from <CODE>javax.swing.filechooser.FileFilter</CODE>.
         */
        public java.lang.String getDescription()
        {
          return "*.pdf";
        }
      };
      fileChooser.addChoosableFileFilter(extensionFilter);
      fileChooser.setDialogTitle(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.OPEN).toString());
      if(fileChooser.showDialog(thisComponent, new com.pdfindexer.util.Action(com.pdfindexer.util.Action.OPEN).toString()) == javax.swing.JFileChooser.APPROVE_OPTION)
      {
        closePdf();
        openPdf(fileChooser.getSelectedFile().toURI().toURL());
        setTitle(com.pdfindexer.util.Util.translate(TITLE) + " - " + fileChooser.getSelectedFile().getName());
      }
    }
    catch(java.lang.Exception exc)
    {
      com.pdfindexer.util.Messages.showException(thisComponent, exc);
    }
  }  
  
  /**
   * Method for openig a document URL.
   */
  protected void openURL()
  {
    try
    {
      javax.swing.JLabel urlLabel = new javax.swing.JLabel("URL:");
      javax.swing.JTextField urlTextField = new javax.swing.JTextField();
      javax.swing.JPanel urlPanel = new javax.swing.JPanel();
      urlPanel.setLayout(new java.awt.BorderLayout());
      urlPanel.add(urlLabel, java.awt.BorderLayout.NORTH);
      urlPanel.add(urlTextField, java.awt.BorderLayout.SOUTH);
      if(com.pdfindexer.util.Messages.showOkCancel(thisComponent, urlPanel, new com.pdfindexer.util.Action(com.pdfindexer.util.Action.OPEN).toString()) == javax.swing.JOptionPane.OK_OPTION)
      {
        closePdf();
        openPdf(new java.net.URL(urlTextField.getText()));
      }
    }
    catch(java.lang.Exception exc)
    {
      com.pdfindexer.util.Messages.showException(thisComponent, exc);
    }
  }  
  
  /**
   * Method for saving a document file.
   */
  protected void saveFile()
  {
    try
    {
      javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
      javax.swing.filechooser.FileFilter extensionFilter = new javax.swing.filechooser.FileFilter()
      {
        /**
         * Overridden method from <CODE>javax.swing.filechooser.FileFilter</CODE>.
         */
        public boolean accept(java.io.File file)
        {
          boolean isAcceptable = true;
          if(file != null && !file.isDirectory())
          {
            java.lang.String extension = com.pdfindexer.util.Util.getFileExtension(file.getName());
            if(!extension.equals("pdf"))
            {
              isAcceptable = false;
            }
          }
          return isAcceptable;
        }

        /**
         * Overridden method from <CODE>javax.swing.filechooser.FileFilter</CODE>.
         */
        public java.lang.String getDescription()
        {
          return "*.pdf";
        }
      };
      fileChooser.addChoosableFileFilter(extensionFilter);
      fileChooser.setDialogTitle(new com.pdfindexer.util.Action(com.pdfindexer.util.Action.SAVE).toString());
      if(fileChooser.showDialog(thisComponent, new com.pdfindexer.util.Action(com.pdfindexer.util.Action.SAVE).toString()) == javax.swing.JFileChooser.APPROVE_OPTION)
      {
        // Secure the extension ending.
        java.lang.String saveableDocumentName = fileChooser.getSelectedFile().getAbsolutePath().split("\\.pdf")[0] + ".pdf";
        
        java.io.InputStream inputStream = documentURL.openStream();
        java.io.FileOutputStream outputStream = new java.io.FileOutputStream(saveableDocumentName);
        int abyte;
        while((abyte = inputStream.read()) != -1)
        {
          outputStream.write(abyte);
        }
        inputStream.close();
        outputStream.close();
      }
    }
    catch(java.lang.Exception exc)
    {
      com.pdfindexer.util.Messages.showException(thisComponent, exc);
    }
  }  
  
  /**
   * Method for decoding a page.
   * @param page the page number.
   */
  protected void decodePage(final int page)
  {
    decodePage(page, null, null);
  }  
  
  /**
   * Method for decoding a page and scrolling to the specified location.
   * @param page the page number.
   * @param location the scrolling point.
   */
  protected void decodePage(final int page, final java.awt.Point location)
  {
    decodePage(page, location, null);
  }
  
  /**
   * Method for decoding a page, scrolling to the specified location and highlighting the specified shapes..
   * @param page the page number.
   * @param scrollRectangle the scrolling rectangle.
   * @param shapes the highlightable shapes.
   */
  protected void decodePage(final int page, final java.awt.Point location, final java.util.Vector shapes)
  {
    if(page > pdfDecoder.getPageCount() || page < 1)
    {
      return;
    }
    else if(page == currentPage)
    {
      highlightPage(shapes);
      if(location == null)
      {
        pdfDecoder.scrollRectToVisible(new java.awt.Rectangle(0, 0, 1, 1));
      }
      else
      {
        java.awt.Point decodedLocation = decodePoint(location);
        java.awt.Rectangle wiewportRect = pdfScrollPane.getViewport().getViewRect();
        pdfDecoder.scrollRectToVisible(new java.awt.Rectangle(decodedLocation.x, decodedLocation.y, wiewportRect.width, wiewportRect.height));
      }
      return;
    }
    com.pdfindexer.util.SwingWorker swingWorker = new com.pdfindexer.util.SwingWorker()
    {
      /**
       * Overridden method from <code>com.pdfindexer.util.SwingWorker</code>.
       */
      public java.lang.Object construct()
      {
        try
        {
          ((javax.swing.JProgressBar)statusBar.getStatusObject()).setValue(0);
          statusBar.getStatusObject().setVisible(true);
          // Disable modifications.
          zoomComboBox.setEnabled(false);
          rotationComboBox.setEnabled(false);
          pageCountTextField.setEnabled(false);
          firstButton.setEnabled(false);
          previousButton.setEnabled(false);
          nextButton.setEnabled(false);
          lastButton.setEnabled(false);
          goMenu.setEnabled(false);
          rotateMenu.setEnabled(false);
          // Decode page.
          pdfDecoder.decodePage(page);
          org.jpedal.objects.PdfPageData pdfPageData = pdfDecoder.getPdfPageData();
          cropBox = new java.awt.Rectangle(pdfPageData.getCropBoxX(page), pdfPageData.getCropBoxY(page),
          pdfPageData.getCropBoxWidth(page), pdfPageData.getCropBoxHeight(page));
          mediaBox = new java.awt.Rectangle(pdfPageData.getMediaBoxX(page), pdfPageData.getMediaBoxY(page),
          pdfPageData.getMediaBoxWidth(page), pdfPageData.getMediaBoxHeight(page));
          annotations = pdfDecoder.getPdfAnnotsData(null);
          pdfDecoder.setPageParameters(zoom, page);
          pdfDecoder.setPageRotation(rotation);
          pdfDecoder.invalidate();
          pdfScrollPane.revalidate();
          pdfScrollPane.repaint();
          repaint();
          highlightPage(shapes);
          if(location == null)
          {
            pdfDecoder.scrollRectToVisible(new java.awt.Rectangle(0, 0, 1, 1));
          }
          else
          {
            java.awt.Point decodedLocation = decodePoint(location);
            java.awt.Rectangle wiewportRect = pdfScrollPane.getViewport().getViewRect();
            pdfDecoder.scrollRectToVisible(new java.awt.Rectangle(decodedLocation.x, decodedLocation.y, wiewportRect.width, wiewportRect.height));
          }
          // Set thumbnail.
          if(currentPage > 0)
          {
            javax.swing.JToggleButton button = (javax.swing.JToggleButton)thumbnailToolBar.getComponentAtIndex(currentPage - 1);
            button.setSelected(false);
          }
          final javax.swing.JToggleButton newButton = (javax.swing.JToggleButton)thumbnailToolBar.getComponentAtIndex(page - 1);
          newButton.setSelected(true);
          java.awt.Rectangle rectangle = thumbnailToolBar.getVisibleRect();
          if (!rectangle.contains(newButton.getBounds()))
          {
            thumbnailToolBar.scrollRectToVisible(newButton.getBounds());
          }
//          setThumbnails();
          // Enable modifications. 
          zoomComboBox.setEnabled(true);
          rotationComboBox.setEnabled(true);
          pageCountTextField.setEnabled(true);
          goMenu.setEnabled(true);
          rotateMenu.setEnabled(true);
          firstMenuItem.setEnabled(page != 1);
          previousMenuItem.setEnabled(page != 1);
          nextMenuItem.setEnabled(page != pdfDecoder.getPageCount());
          lastMenuItem.setEnabled(page != pdfDecoder.getPageCount());
          firstButton.setEnabled(page != 1);
          previousButton.setEnabled(page != 1);
          nextButton.setEnabled(page != pdfDecoder.getPageCount());
          lastButton.setEnabled(page != pdfDecoder.getPageCount());
          statusBar.getStatusObject().setVisible(false);
          currentPage = page;
        }
        catch(java.lang.Exception exc)
        {
          com.pdfindexer.util.Messages.showException(thisComponent, exc);
        }
        finally
        {
          pageCountTextField.setText(currentPage + " / " + pdfDecoder.getPageCount());
        }
        return null;
      }
    };
    swingWorker.start();
  }
  
  /**
   * Method for setting the visible thumbnails.
   */
  protected void setThumbnails()
  {
    com.pdfindexer.util.SwingWorker swingWorker = new com.pdfindexer.util.SwingWorker()
    {
      /**
       * Overridden method from <code>com.pdfindexer.util.SwingWorker</code>.
       */
      public java.lang.Object construct()
      {
        try
        {
          java.awt.Rectangle visibleRect = thumbnailToolBar.getVisibleRect();
          for(int i = 0; i < pdfDecoder.getPageCount(); i++)
          {
            javax.swing.JButton thubnailButton = (javax.swing.JButton)thumbnailToolBar.getComponentAtIndex(i);
            if(visibleRect.intersects(thubnailButton.getBounds()))
            {
              java.awt.image.BufferedImage thumbnailImage = pdfDecoder.getPageAsThumbnail(i + 1, THUMBNAIL_HEIGHT);
              java.awt.Graphics2D g2=(java.awt.Graphics2D)thumbnailImage.getGraphics();
              g2.setColor(java.awt.Color.BLACK);
              g2.draw(new java.awt.Rectangle(0, 0, thumbnailImage.getWidth() - 1, thumbnailImage.getHeight() - 1));
              javax.swing.ImageIcon thumbnailIcon = new javax.swing.ImageIcon(thumbnailImage.getScaledInstance(-1, thumbnailImage.getHeight(), java.awt.image.BufferedImage.SCALE_FAST));
              thubnailButton.setIcon(thumbnailIcon);
            }
            java.lang.Thread.yield();
          }
        }
        catch(java.lang.Exception exc)
        {
          com.pdfindexer.util.Messages.showException(thisComponent, exc);
        }
        return null;
      }
    };
    swingWorker.start();
  }
  
  /**
   * Method for rotating a page.
   * @param degree the rotation degree.
   */
  private void rotatePage(int degree)
  {
    try
    {
      if(degree != rotation)
      {
        pdfDecoder.setPageRotation(degree);
        pdfDecoder.invalidate();
        pdfScrollPane.revalidate();
        repaint();
        rotation = degree;
      }
    }
    catch(java.lang.Exception exc)
    {
      com.pdfindexer.util.Messages.showException(thisComponent, exc);
    }
    finally
    {
      rotationComboBox.setSelectedItem(rotation + "°");
    }
  }
  
  /**
   * Method for zooming a page.
   * @param zoomString the string reprezentation of the zooming option.
   */
  private void zoomPage(java.lang.String zoomString)
  {
    try
    {
      float newZoom = 1;
      if(zoomString.equals(zoomValues[PAGE]))
      {
        int width = pdfScrollPane.getViewport().getWidth() - 2 * HORIZONTAL_INSET;
        int height = pdfScrollPane.getViewport().getHeight() - 2 * VERTICAL_INSET;
        float widthPercentage = (float)width / cropBox.width;
        float heightPercentage = (float)height / cropBox.height;
        newZoom = widthPercentage < heightPercentage ? widthPercentage : heightPercentage;
      }
      else if(zoomString.equals(zoomValues[SIZE]))
      {
        newZoom = 1;
      }
      else if(zoomString.equals(zoomValues[WIDTH]))
      {
        int width = pdfScrollPane.getViewport().getWidth() - 2 * HORIZONTAL_INSET;
        newZoom = (float)width / cropBox.width;
      }
      else
      {
        newZoom = java.lang.Float.parseFloat(zoomString) / 100;
        if(newZoom <= 0 || newZoom > 10)
        {
          return;
        }
      }
      pdfDecoder.setPageParameters(newZoom, currentPage);
      pdfDecoder.setPageRotation(rotation);
      pdfDecoder.invalidate();
      pdfScrollPane.revalidate();
      pdfScrollPane.repaint();
      repaint();
      zoom = newZoom;
    }
    catch(java.lang.NumberFormatException exc)
    {
    }
    catch(java.lang.Exception exc)
    {
      com.pdfindexer.util.Messages.showException(thisComponent, exc);
    }
    finally
    {
      zoomComboBox.setSelectedItem((int)(zoom * 100) + "%");
    }
  }
  
  /**
   * Method for highlighting shapes on page.
   * @param shapes a vector of highlightable shapes.
   */
  protected void highlightPage(java.util.Vector shapes)
  {
    try
    {
      if(shapes != null)
      {
        int size = shapes.size();
        java.awt.Shape[] fillingShapes = new java.awt.Shape[size];
        java.awt.geom.Rectangle2D[] boundingRectangles = new java.awt.geom.Rectangle2D[size];
        boolean[] areVisible = new boolean[size];
        java.awt.Color[] colors = new java.awt.Color[size];
        for(int i = 0; i < size; i++)
        {
          fillingShapes[i] = (java.awt.Shape)shapes.get(i);
//          boundingRectangles[i] = new java.awt.Rectangle.Float(0, 0, 0, 0);
          boundingRectangles[i] = (java.awt.Rectangle.Float)shapes.get(i);
          areVisible[i] = true;
          colors[i] = java.awt.Color.ORANGE;
        }
        pdfDecoder.setHighlightedZones(1, null, null, fillingShapes, null, null, boundingRectangles, areVisible, colors, null);
      }
      else
      {
        pdfDecoder.setHighlightedZones(1, null, null, null, null, null, null, null, null, null);
      }
      pdfDecoder.repaint();
    }
    catch(java.lang.Exception exc)
    {
      com.pdfindexer.util.Messages.showException(thisComponent, exc);
    }
  }
  
  /**
   * Method for decoding point coordinates.
   * @param point the relative point.
   * @return the decoded absolute point.
   */
  private void decodeLink(java.awt.Point point, boolean mouseClicked)
  {
    try
    {
      pdfDecoder.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
      pdfDecoder.setToolTipText(null);
      statusLabel.setText(null);
      java.awt.Rectangle[] hotspots = pdfDecoder.getPageHotspots();
      if(hotspots != null)
      {          
        int annotationIndex = 0;
        for(int i = 0; i < hotspots.length; i++)
        {
          java.awt.Rectangle iteratorHotspot = hotspots[i];
          if(iteratorHotspot.contains(encodePoint(point)))
          {
            if(annotations.getAnnotSubType(annotationIndex).equals("Link"))
            {
              pdfDecoder.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
              java.util.Map annotationDetails = annotations.getAnnotRawData(annotationIndex);
              java.lang.String annotationDestination = (java.lang.String)annotationDetails.get("Dest");
              java.util.Map annotationAction = (java.util.Map)annotationDetails.get("A");
              java.lang.String destination = "";
              if(annotationDestination != null)
              {
                destination = annotationDestination.toString();
                if(mouseClicked)
                {
//                    int page = java.lang.Integer.parseInt((java.lang.String)annotationDetails.get("StructParent"));
//                    decodePage(page, parsePoint(destination));
                  com.pdfindexer.util.Messages.showOk(thisComponent, destination, "");
                }
              }
              else if(annotationAction != null)
              {
                if(annotationAction.get("S").equals("/GoTo"))
                {
                  destination = annotationAction.get("D").toString();
                  if(mouseClicked)
                  {
//                    int page = java.lang.Integer.parseInt((java.lang.String)annotationDetails.get("StructParent"));
//                    decodePage(page, parsePoint(destination));
                    com.pdfindexer.util.Messages.showOk(thisComponent, destination, "");
                  }
                }
                else if(annotationAction.get("S").equals("/URI"))
                {
                  destination = annotationAction.get("URI").toString();
                  java.util.StringTokenizer sT = new java.util.StringTokenizer(destination, "()");
                  destination = sT.nextToken();
                  if(mouseClicked)
                  {
                    javax.swing.JTextArea destinationTextArea = new javax.swing.JTextArea(destination);
                    destinationTextArea.setFont(new java.awt.Font("Dialog", 0, 11));
                    destinationTextArea.setEditable(false);
                    javax.swing.JScrollPane destinationScrollPane = new javax.swing.JScrollPane(destinationTextArea);
                    com.pdfindexer.util.Messages.showOk(thisComponent, destinationScrollPane, "");
                  }
                }
              }
              pdfDecoder.setToolTipText(destination);
              statusLabel.setText(destination);
            }
            break;
          }
          annotationIndex++;
        }
      }
    }
    catch(java.lang.Exception exc)
    {
      com.pdfindexer.util.Messages.showException(thisComponent, exc);
    }
  }
  
  /**
   * Method for encoding point coordinates.
   * @param point the absolute point.
   * @return the encoded relative point.
   */
  private java.awt.Point encodePoint(java.awt.Point point)
  {
    java.awt.Point decodedPoint = null;
    try
    {
      int dX = (int)((point.x - HORIZONTAL_INSET) / zoom);
      int dY = (int)((point.y - VERTICAL_INSET) / zoom);
      if(rotation == 90)
      {
        int tmp = (int)(dX + cropBox.y);
        dX = (int)(dY + cropBox.x);
        dY = tmp;	
      }
      else if(rotation == 180)
      {
        dX = mediaBox.width - (int)(dX + mediaBox.width - cropBox.width - cropBox.x);
        dY = (int)(dY + cropBox.y);
      }
      else if(rotation == 270)
      {
        int tmp = mediaBox.height - (int)(dX + mediaBox.height - cropBox.height - cropBox.y);
        dX = mediaBox.width - (int)(dY + mediaBox.width - cropBox.width - cropBox.x);
        dY = tmp;
      }
      else
      {
        dX = (int)(dX + cropBox.x);
        dY = mediaBox.height - (int)(dY + mediaBox.height - cropBox.height - cropBox.y);
      }
      decodedPoint = new java.awt.Point(dX, dY);
    }
    catch(java.lang.Exception exc)
    {
      com.pdfindexer.util.Messages.showException(thisComponent, exc);
    }
    return decodedPoint;
  }
  
  /**
   * Method for decoding point coordinates.
   * @param point the relative point.
   * @return the decoded absolute point.
   */
  private java.awt.Point decodePoint(java.awt.Point point)
  {
    java.awt.Point decodedPoint = null;
    try
    {
      int dX = (int)java.lang.Math.floor(((float)(point.x) * zoom));
      int dY = (int)java.lang.Math.floor(((float)(point.y) * zoom));
      java.awt.Rectangle wiewportRect = pdfScrollPane.getViewport().getViewRect();
      if(rotation == 90)
      {
        int tmp = dX;
        dX = dY - wiewportRect.width;
        dY = tmp;	
      }
      else if(rotation == 180)
      {
        dX = pdfDecoder.getWidth() - dX;
        dY = dY - wiewportRect.height;
      }
      else if(rotation == 270)
      {
        int tmp = dX;
        dX = pdfDecoder.getWidth() - dY;
        dY = pdfDecoder.getHeight() - tmp;
      }
      else
      {
        dY = pdfDecoder.getHeight() - dY;
      }
      decodedPoint = new java.awt.Point(dX, dY);
    }
    catch(java.lang.Exception exc)
    {
      com.pdfindexer.util.Messages.showException(thisComponent, exc);
    }
    return decodedPoint;
  }
  
  /**
   * Method for parsing a destination string.
   * @param destination the string reprezentation of the destination.
   * @return the parsed point.
   */
  private java.awt.Point parsePoint(java.lang.String destination)
  {
    java.awt.Point point = null;
    try
    {
      if(destination != null && destination.indexOf("/XYZ") != -1)
      {
        destination = destination.substring(destination.indexOf("/XYZ") + 4);
        java.util.StringTokenizer stringTokenizer = new java.util.StringTokenizer(destination, "[] ");
        java.lang.String x = stringTokenizer.nextToken();
        if(x.equals("null"))
        {
          x = "0";
        }
        java.lang.String y = stringTokenizer.nextToken();
        if(y.equals("null"))
        {
          y = "0";
        }
        point = new java.awt.Point((int)java.lang.Float.parseFloat(x), (int)java.lang.Float.parseFloat(y));
      }
      else if(destination != null && destination.indexOf("/FitH") != -1)
      {
        destination = destination.substring(destination.indexOf("/FitH") + 5);
        java.util.StringTokenizer stringTokenizer = new java.util.StringTokenizer(destination, "[] ");
        java.lang.String y = stringTokenizer.nextToken();
        if(y.equals("null"))
        {
          y = "0";
        }
        point = new java.awt.Point(0, (int)java.lang.Float.parseFloat(y));
      }
      else if(destination != null && destination.indexOf("/FitV") != -1)
      {
        destination = destination.substring(destination.indexOf("/FitV") + 5);
        java.util.StringTokenizer stringTokenizer = new java.util.StringTokenizer(destination, "[] ");
        java.lang.String x = stringTokenizer.nextToken();
        if(x.equals("null"))
        {
          x = "0";
        }
        point = new java.awt.Point((int)java.lang.Float.parseFloat(x), 0);
      }
    }
    catch(java.lang.Exception exc)
    {
      com.pdfindexer.util.Messages.showException(thisComponent, exc);
    }
    return point;
  }
  
  /**
   * Method for adding the bookmarks.
   * @param node a bookmark node.
   * @param treeNode a tree node. 
   */
  private void addBookmarks(org.w3c.dom.Node node, javax.swing.tree.DefaultMutableTreeNode treeNode)
  {
    try
    {
      org.w3c.dom.NodeList nodeList = node.getChildNodes();
      for(int j = 0; j < nodeList.getLength(); j++)
      {
        org.w3c.dom.Element element = (org.w3c.dom.Element)nodeList.item(j);
        javax.swing.tree.DefaultMutableTreeNode childTreeNode = new javax.swing.tree.DefaultMutableTreeNode(new Bookmark(element));
        treeNode.add(childTreeNode);
        if(element.hasChildNodes())
        {
          addBookmarks(element, childTreeNode);
        }
      }
    }
    catch(java.lang.Exception exc)
    {
      com.pdfindexer.util.Messages.showException(thisComponent, exc);
    }
  }
  
  /**
   * Method for exiting the application.
   */
  private void exit()
  {
    try
    {
      if(newLocale == null)
      {
        newLocale = com.pdfindexer.util.Util.getActualLocale();
      }
      applicationProperties.setProperty("Language.Locale", newLocale.toString());
      applicationProperties.setProperty("Frame.Location", com.pdfindexer.util.Util.toString(getLocation()));
      applicationProperties.setProperty("Frame.Size", com.pdfindexer.util.Util.toString(getSize()));
      applicationProperties.setProperty("Divider.Location", java.lang.Integer.toString(pdfSplitPane.getDividerLocation()));
      applicationProperties.setProperty("Zoom.Value", java.lang.Float.toString(zoom));
      applicationProperties.store(new java.io.FileOutputStream(propertiesFile), "PDFIndexer properties.");
      java.lang.System.exit(0);
    }
    catch(java.lang.Exception exc)
    {
      com.pdfindexer.util.Messages.showException(thisComponent, exc);
      java.lang.System.exit(1);
    }
  }
  
  /**
   * Inner class for bookmark reprezentation.
   */
  private class Bookmark
  {
    /**
     * Variables.
     */
    private java.lang.String title;
    private int page;
    private java.awt.Point point;
    
    /**
     * Constructor.
     */
    public Bookmark(org.w3c.dom.Element element)
    {
      title = element.getAttribute("title");
      try
      {
        page = java.lang.Integer.parseInt(element.getAttribute("page"));
      }
      catch(java.lang.Exception exc)
      {
        page = 0;
      }
      java.lang.String destination = element.getAttribute("Dest");
      if(destination == null || destination.equals(""))
      {
        destination = element.getAttribute("D");
      }
      point = parsePoint(destination);
      java.lang.String link = element.getAttribute("objectRef");
    }
    
    /**
     * Method for getting the page of this bookmark.
     * @return the page number.
     */
    public int getPage()
    {
      return page;
    }
    
    /**
     * Method for getting the point of this bookmark.
     * @return the point.
     */
    public java.awt.Point getPoint()
    {
      return point;
    }
    
    /**
     * Overridden method from <CODE>java.lang.Object</CODE>.
     * @return the string reprezentation of this bookmark.
     */
    public java.lang.String toString()
    {
      return title;
    }
    
  }
  
  /**
   * Class for the default tree cell rendering.
   */
  public class BookmarkTreeCellRenderer extends javax.swing.tree.DefaultTreeCellRenderer
  {
    
    /**
     * Overridden method from <CODE>javax.swing.tree.DefaultTreeCellRenderer</CODE>.
     */
    public java.awt.Component getTreeCellRendererComponent(javax.swing.JTree tree, java.lang.Object treeNode, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
      super.getTreeCellRendererComponent(tree, treeNode, sel, expanded, leaf, row, hasFocus);
      try
      {
        setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/page.gif")));
        setToolTipText(treeNode.toString());
      }
      catch(java.lang.Exception exc)
      {
        com.pdfindexer.util.Messages.showException(thisComponent, exc);
      }
      return this;
    }
    
  }
  
  /**
   * The main method.
   * @param args the command line arguments.
   */
  public static void main(java.lang.String[] args)
  {
    final java.lang.String[] arguments = args;
    java.awt.EventQueue.invokeLater(new java.lang.Runnable()
    {
      public void run()
      {
        try
        {
          java.net.URL url = null;
          java.io.File file = null;
          if(arguments.length == 1)
          {
            file = new java.io.File(arguments[0]);
            if(file.exists())
            {
              url = new java.io.File(arguments[0]).toURI().toURL();
            }
            else
            {
              url = new java.net.URL(arguments[0]);
            }
          }
          PdfViewer pdfViewer = new PdfViewer();
          if(url != null)
          {
            pdfViewer.openPdf(url);
            if(file!= null)
            {
              pdfViewer.setTitle(com.pdfindexer.util.Util.translate(TITLE) + " - " + file.getName());
            }
          }
          pdfViewer.setVisible(true);
        }
        catch(java.lang.Exception exc)
        {
          com.pdfindexer.util.Messages.showException(null, exc);
          java.lang.System.exit(1);
        }
      }
    });
  }
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JMenuItem aboutMenuItem;
  private javax.swing.JScrollPane bookmarkScrollPane;
  private javax.swing.JTree bookmarkTree;
  private javax.swing.JPanel bottomPanel;
  private javax.swing.JMenuItem closeMenuItem;
  private javax.swing.JMenuItem contentsMenuItem;
  private javax.swing.JMenuItem exitMenuItem;
  private javax.swing.JMenu fileMenu;
  private javax.swing.JSeparator fileSeparator;
  private javax.swing.JToolBar fileToolBar;
  private javax.swing.JButton firstButton;
  private javax.swing.JMenuItem firstMenuItem;
  private javax.swing.JMenu goMenu;
  private javax.swing.JToolBar goToolBar;
  private javax.swing.JMenu helpMenu;
  private javax.swing.JSeparator helpSeparator;
  private javax.swing.JPanel indexPanel;
  private javax.swing.JMenuItem languageMenuItem;
  private javax.swing.JButton lastButton;
  private javax.swing.JMenuItem lastMenuItem;
  private javax.swing.JMenuBar menuBar;
  private javax.swing.JTabbedPane navigationTabbedPane;
  private javax.swing.JButton nextButton;
  private javax.swing.JMenuItem nextMenuItem;
  private javax.swing.JMenuItem ninetyMenuItem;
  private javax.swing.JMenuItem oneeightyMenuItem;
  private javax.swing.JButton openButton;
  private javax.swing.JMenuItem openMenuItem;
  private javax.swing.JTextField pageCountTextField;
  private javax.swing.JMenuItem pageMenuItem;
  private javax.swing.JMenuItem pageZoomMenuItem;
  private javax.swing.JScrollPane pdfScrollPane;
  private javax.swing.JSplitPane pdfSplitPane;
  private javax.swing.JButton previousButton;
  private javax.swing.JMenuItem previousMenuItem;
  private javax.swing.JPanel progressPanel;
  private javax.swing.JMenu rotateMenu;
  private javax.swing.JComboBox rotationComboBox;
  private javax.swing.JButton saveButton;
  private javax.swing.JMenuItem saveMenuItem;
  private javax.swing.JMenuItem sizeZoomMenuItem;
  private javax.swing.JLabel statusLabel;
  private javax.swing.ButtonGroup thumbnailButtonGroup;
  private javax.swing.JScrollPane thumbnailScrollPane;
  private javax.swing.JToolBar thumbnailToolBar;
  private javax.swing.JToolBar topToolBar;
  private javax.swing.JSeparator topViewSeparator;
  private javax.swing.JMenuItem twoseventyMenuItem;
  private javax.swing.JMenu viewMenu;
  private javax.swing.JToolBar viewToolBar;
  private javax.swing.JMenuItem widthZoomMenuItem;
  private javax.swing.JMenuItem zeroMenuItem;
  private javax.swing.JComboBox zoomComboBox;
  private javax.swing.JMenuItem zoomMenuItem;
  // End of variables declaration//GEN-END:variables
  
}
