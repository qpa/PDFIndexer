/**
* ===========================================
* Java Pdf Extraction Decoding Access Library
* ===========================================
*
* Project Info:  http://www.jpedal.org
* Project Lead:  Mark Stephens (mark@idrsolutions.com)
*
* (C) Copyright 2005, IDRsolutions and Contributors.
*
* 	This file is part of JPedal
*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA


*
* ---------------
* SimpleViewer.java
* ---------------
* (C) Copyright 2005, by IDRsolutions and Contributors.
*
* Original Author:  Mark Stephens (mark@idrsolutions.com)
* Contributor(s):
*
*/
//<start-example>
package com.pdfindexer.gui;
/**
//<end-example>
package viewergui;
/***/

/**standard Java stuff*/
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

//import javax.media.jai.JAI;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.RepaintManager;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jpedal.PdfDecoder;
import org.jpedal.examples.simpleviewer.gui.ThumbnailPanel;
import org.jpedal.examples.simpleviewer.utils.FileFilterer;
import org.jpedal.examples.simpleviewer.utils.IconiseImage;
import org.jpedal.examples.simpleviewer.utils.SwingWorker;
import org.jpedal.exception.PdfException;
import org.jpedal.exception.PdfFontException;
import org.jpedal.io.StatusBar;
import org.jpedal.objects.PdfAnnots;
import org.jpedal.objects.PdfFileInformation;
import org.jpedal.objects.PdfPageData;
import org.jpedal.utils.LogWriter;
import org.jpedal.utils.Strip;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>Demo to show JPedal being used as a GUI viewer,
 * and to demonstrate some of JPedal's capabilities Scope:<b>(All)</b>
 */
//<start-example>
public class SimpleViewer extends JFrame{
    /**
//<end-example>//rename class when run through Ant
    
    public class BaseViewer extends JFrame{
    /**/
        
    /**localized text bundle */
    protected ResourceBundle bundle;
    
    /**flag used for text popup display to shpw if menu disappears*/
    boolean display=true;
    
    /**flag to show if modes can be switched*/
    protected boolean switchModes=true;
    
    /**can switch on or off thumbnails - DOES NOT WORK ON JAVA 1.3*/
    //<start-13>
    private boolean showThumbnails=false;
    /**<end-13>
    private boolean showThumbnails=false;
    /**/
    
    /**encapsulates all thumbnail functionality - just ignore if not required*/
    public ThumbnailPanel thumbnails=new ThumbnailPanel();
    
    /**flag to show if shift+draw rectangle extracts image or text*/
    boolean extractImageOnSelection=false;

    
	/**file separator used*/
	protected final String separator=System.getProperty( "file.separator" );
    
	/**PdfDecoder object which does all the decoding and rendering*/
	protected PdfDecoder decode_pdf;
	
	/**holds the annotations data for the page*/
	protected PdfAnnots pageAnnotations;
	
    /**holds XML tree with bookmarks*/
    protected JScrollPane bookmarkScrollPane=new JScrollPane();
	 
	/**displayed on left to hold thumbnails, bookmarks*/
    protected JTabbedPane navOptionsPanel=new JTabbedPane();
	 
    /**split display between PDF and thumbnails/bookmarks*/
	protected JSplitPane displayPane;
	 
    /**XML structure of bookmarks*/
	private JTree tree;
	
	/** contentPane of this JFrame */
	protected Container c;
	
	/**title message on top if you want to over-ride JPedal default*/
	protected String titleMessage=null;
			
	/**used by tree to convert page title into page number*/
	private HashMap pageLookupTable=new HashMap();
	
	/**used by tree to find point to scroll to*/
	private Map pointLookupTable=new HashMap();
	
	/**flag to stop page setting causing a spurious refresh*/
	private boolean ignoreAlteredBookmark=false;
	
	/**directory to load files from*/
	private  String inputDir = System.getProperty("user.dir"); //$NON-NLS-1$
	
	/**current page number*/
	protected int currentPage = 1;
	
	/**temp location to store output, if needed*/
	protected String target;
	
	/**store page rotation*/
	protected int rotation=0;
	
	//<start-example>
	/**used to track changes when dragging rectangle around*/
	public int m_x1, m_y1, m_x2, m_y2,old_m_x2=-1,old_m_y2=-1;
	
	/** for MOUSE LISTENERS ONLY for update the current cursor position */
	protected int x,y;
	
	/** local handle on cursorBox */
	public Rectangle currentRectangle =null;
	//<end-example>
	
	/**current cursor position*/
	public int cx,cy;
	
	/**flag to stop mutliple prints*/
	protected static int printingThreads=0;
	
	/**Swing thread to decode in background - we have one thread we use for various tasks*/
	SwingWorker worker=null;
	
	//<start-example>
	//show co-ords on page
	private static JLabel coords=new JLabel();
	//<end-example>
	
	/**number of pages in current pdf*/
	protected int pageCount = 1;
	
	/**page scaling to use 1=100%*/
	protected float scaling = 1;
	
	/**name of current file being decoded*/
	protected String selectedFile = null;

	/**boolean lock to stop multiple access*/
	protected boolean isProcessing = false;

	protected String[] scalingValues={"25","50","75","100","125","150","200","250","500","750","1000"};
	
	/**scaling values as floats to save conversion*/
    protected float[] scalingFloatValues={.25f,.5f,.75f,1.0f,1.25f,1.5f,2.0f,2.5f,5.0f,7.5f,10.0f};
	
	/**default scaling on the combobox scalingValues*/
    //<start-example>
	private final int defaultSelection=0;
	/**<end-example>
	private final int defaultSelection=3;
	/**/
	
	/**scaling factors on the page*/
	protected JComboBox scalingBox;
	
	/**Scrollpane for pdf panel*/
	JScrollPane scrollPane = new JScrollPane();
	
	/**scaling values as floats to save conversion*/
	private final String[] rotationValues={"0","90","180","270"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	
	/**scaling factors on the page*/
	protected JComboBox rotationBox=new JComboBox(rotationValues);
	
	//<start-example>
	/**page counter to display*/
	protected JLabel pageCounter1;
	
	protected JTextField pageCounter2 = new JTextField(4);
	
	protected JLabel pageCounter3;
	//<end-example>
	
	/**fall back for messages (ie if using 1.3) */
	private Map messages=null;
	
	/** padding so that the pdf is not right at the edge */
	private final int inset=25;

	/**size of file for display*/
	private long size;

	/**tells user if we enter a link*/
	private String message=""; //$NON-NLS-1$
	
	/**used to trace file change in threads*/
	private static int currentFileCount=0;
	
	/**nav buttons - global so they can be hidden*/
	protected JButton first=new JButton();
	
	/**nav buttons - global so they can be hidden*/
	protected JButton fback=new JButton();
	
	/**nav buttons - global so they can be hidden*/
	protected JButton back=new JButton();
	
	/**nav buttons - global so they can be hidden*/
	protected JButton forward=new JButton();
	
	/**nav buttons - global so they can be hidden*/
	protected JButton fforward=new JButton();
	
	/**nav buttons - global so they can be hidden*/
	protected JButton end=new JButton();
	
	/**list for types - assumes present in org/jpedal/examples/simpleviewer/annots*
	 * "OTHER" MUST BE FIRST ITEM
	 * Try adding Link to the list to see links
	 */
	private final String[] annotTypes={"Other","Text","FileAttachment"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
	/**stops autoscrolling at screen edge*/
	private boolean allowScrolling=true;
	
    /**show if outlines drawn*/
    private boolean hasOutlinesDrawn=false;
    
    /**allows user to toggle on/off bookmarks*/
    private JButton bookmarksButton = new JButton();
    
    /**crop offset if present*/
    private int cropX,cropY,cropW,cropH,mediaW,mediaH,mediaX,mediaY;
    
    private final Font textFont=new Font("Serif",Font.PLAIN,12); //$NON-NLS-1$
	
    private final Font headFont=new Font("SansSerif",Font.BOLD,14); //$NON-NLS-1$

    /**Interactive display object - needs to be added to PdfDecoder*/
    protected StatusBar statusBar=new StatusBar(Color.orange);
    
    /** holding all creators that produce OCR pdf's */
	private String[] ocr = {"TeleForm","dgn2pdf"};
    
	/** location for the divider when bookmarks are displayed */
	public int divLocation=170;
	
	/** location for divider with thumbnails turned on */
	private int thumbLocation=200;
	
	/** minimum screen width to ensure menu buttons are visible */
	private int minimumScreenWidth=700;

	/**offsets to viewport if used*/
	private int dx,dy=0;
	
	/**scaling on viewport if used*/
	private double viewportScale=1;

	/**height of the viewport. Because everything is draw upside down we need this 
	 * to get true y value*/
	protected int maxViewY=0;

	/**handles drawing of thumbnails if needed*/
	private ComponentListener painter;

	/**flag to switch bookmarks on or off*/
	protected boolean showOutlines=true;
	
    /**specify bookmark for each page*/
    //private String[] defaultRefsForPage;

    //private TreeNode[] defaultPageLookup;
    
    /**
     * setup and run client, loading defaultFile on startup
     */
	public void setupViewer(String defaultFile) {
	    
	    setupViewer();
	   
	    openDefaultFile(defaultFile);
	}
	
	/**
	 * open the file passed in by user on startup
	 */
	protected void openDefaultFile(String defaultFile) {
		/** 
		 * open any default file and selected page
		 */ 
		if(defaultFile!=null){
			
			File testExists=new File(defaultFile);
			
			if(!testExists.exists()){
				//##Token:PdfViewerdoesNotExist.message= does not exist.
			    JOptionPane.showMessageDialog(this,defaultFile+"\n"+getMessage("PdfViewerdoesNotExist.message"));
			}else if(testExists.isDirectory()){
				//##Token:PdfViewerFileIsDirectory.message=This file is a Directory and cannot be opened
			    JOptionPane.showMessageDialog(this,defaultFile+"\n"+getMessage("PdfViewerFileIsDirectory.message"));
			}else{
			    
				selectedFile=defaultFile;
			    setViewerTitle(null);
			    
			    size = (testExists.length() >> 10);
			    
			    /**see if user set Page*/
			    String page=System.getProperty("Page");
			    String bookmark=System.getProperty("Bookmark");
			    if(page!=null){
			    	
				    	int pageNum=-1;
				    	try{
				    		pageNum=Integer.parseInt(page);
				    		
				    		if(pageNum<1){
				    			pageNum=-1;
				    			System.err.println(page+ " must be 1 or larger. Opening on page 1");
				    			LogWriter.writeLog(page+ " must be 1 or larger. Opening on page 1");
				    		}
				    		
				    		if(pageNum!=-1)
				    			openFile(testExists,pageNum);
				    		
				    	}catch(Exception e){
				    		System.err.println(page+ "is not a valid number for a page number. Opening on page 1");
				    		LogWriter.writeLog(page+ "is not a valid number for a page number. Opening on page 1");
				    	}
			    }else if(bookmark!=null){
			    		openFile(testExists,bookmark);
			    }else
			    		openFile(defaultFile);
			    
			}
			
			defaultFile=null; //deselect
		}
	}

	//<start-example>
    /**
     * setup and run client
     */
	public SimpleViewer() {
	}
	
	//<end-example>
	
	/**
	 * routine to remove all objects from temp store
	 */
	public final void flush() {

		//get contents
		if(target!=null){
			File temp_files = new File(target);
			String[] file_list = temp_files.list();
			if (file_list != null) {
				for (int ii = 0; ii < file_list.length; ii++) {
						File delete_file = new File(target + separator+file_list[ii]);
						delete_file.delete();
				}
			}
		}	
	}
	
	/**
	 * initialise client
	 */
	public void setupViewer() {
	
		//switch on thumbnails
		 String setThumbnail=System.getProperty("thumbnail");
		 if((setThumbnail!=null)&&(setThumbnail.equals("true")))
		 	this.showThumbnails=true;
		
		setupGUI();
	}
	
	/**
     * setup the viewer
     */
	protected void init(ResourceBundle bundle) {
		
		/**
		 * load correct set of messages
		 */
		if(bundle==null){
			try{
				//Locale.setDefault(Locale.CHINESE);
				this.bundle=ResourceBundle.getBundle("org.jpedal.international.messages"); //$NON-NLS-1$
			}catch(Exception e){
				e.printStackTrace();  
				System.out.println("Exception loading resource bundle"); //$NON-NLS-1$
			}
		}else
			this.bundle=bundle;
	    
		
		/**debugging code to create a log
		LogWriter.setupLogFile(true,1,"","v",false);
		LogWriter.log_name =  "/mnt/shared/log.txt"; */
		/**/
		
		/**
		 * init instance of PdfDecoder as a GUI object by using true
		 */
		decode_pdf = new PdfDecoder(); //USE THIS FOR THE VIEWER ONLY
		
		/**/
		//ensure border round display - not flush with panel edge
		decode_pdf.setInset(inset,inset);
		
		/**setup status object in PdfDecoder*/
		decode_pdf.setStatusBarObject(statusBar);
		
		//<start-example>
		/**
		 * ANNOTATIONS code 1
		 * 
		 * use for annotations, loading icons and enabling display of annotations
		 * this enables general annotations with an icon for each type. 
		 * See below for more specific function.
		 */
		decode_pdf.createPageHostspots(annotTypes,"org/jpedal/examples/simpleviewer/annots/"); //$NON-NLS-1$
		
		/**
		 * ANNOTATIONS code 2
		 * 
		 * this code allows you to create a unique set on icons for any type of annotations, with
		 * an icons for every annotation, not just types.
		 */
		//createUniqueAnnontationIcons();
        
		
		//<end-example>
		/**/
		/**
		 * FONT EXAMPLE CODE showing JPedal's functionality to set values for 
		 * non-embedded fonts. 
		 * 
		 * This allows sophisticated substitution of non-embedded fonts.
		 * 
		 * Most font mapping is done as the fonts are read, so these calls must
		 * be made BEFORE the openFile() call.
		 */
	
		/**
		 * FONT EXAMPLE - Replace global default for non-embedded fonts.
		 * 
		 * You can replace Lucida as the standard font used for all non-embedded and substituted fonts 
		 * by using is code.
		 * Java fonts are case sensitive, but JPedal resolves this, so you could 
		 * use Webdings, webdings or webDings for Java font Webdings
		 */
		try{
		    //choice of example font to stand-out (useful in checking results to ensure no font missed. 
		    //In general use Helvetica or similar is recommended
		    decode_pdf.setDefaultDisplayFont("SansSerif"); //$NON-NLS-1$
		}catch(PdfFontException e){ //if its not available catch error and show valid list
		    
		    System.out.println(e.getMessage());
		    //<start-example>
		    //get list of fonts you can use
			String[] fontList =GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
			//##Token:PdfViewerFontsFound.message=Fonts available are:-
			System.out.println(getMessage("PdfViewerFontsFound.message"));
			System.out.println("=====================\n"); //$NON-NLS-1$
			int count = fontList.length;
			for (int i = 0; i < count; i++) {
				Font f=new Font(fontList[i],1,10);
				//##Token:PdfViewerFontsPostscript.message=Postscript
				System.out.println(fontList[i]+" ("+getMessage("PdfViewerFontsPostscript.message")+"="+f.getPSName()+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				
			}
			System.exit(1);
			//<end-example>
			
		}/***/

		//<start-example>
		/**
         * IMPORTANT note on fonts for EXAMPLES
         * 
         * USEFUL TIP : The SimpleViewer displays a list of fonts used on the
         * current PDF page with the File > Fonts menu option.
         * 
         * PDF allows the use of weights for fonts so Arial,Bold is a weight of
         * Arial. This value is not case sensitive so JPedal would regard
         * arial,bold and aRiaL,BoLd as the same.
         * 
         * Java supports a set of Font families internally (which may have
         * weights), while JPedals substitution facility uses physical True Type
         * fonts so it is resolving each font weight separately. So mapping
         * works differently, depending on which is being used.
         * 
         * If you are using a font, which is named as arial,bold you can use
         * either arial,bold or arial (and JPedal will then try to select the
         * bold weight if a Java font is used).
         * 
         * So for a font such as Arial,Bold JPedal will test for an external
         * truetype font substitution (ie arialMT.ttf) mapped to Arial,Bold. BUT
         * if the substitute font is a Java font an additional test will be made
         * for a match against Arial if there is no match on Arial,Bold.
         * 
         * If you want to map all Arial to equivalents to a Java font such as
         * Times New Roman, just map Arial to Times New Roman (only works for
         * inbuilt java fonts). Note if you map Arial,Bold to a Java font such
         * as Times New Roman, you will get Times New Roman in a bold weight, if
         * available. You cannot set a weight for the Java font.
         * 
         * If you wish to substitute Arial but not Arial,Bold you should
         * explicitly map Arial,Bold to Arial,Bold as well.
         * 
         * The reason for the difference is that when using Javas inbuilt fonts
         * JPedal can resolve the Font Family and will try to work out the
         * weight internally. When substituting Truetype fonts, these only
         * contain ONE weight so JPedal is resolving the Font and any weight as
         * a separate font . Different weights will require separate files.
         * 
         * Open Source version does not support all font capabilities.
         */
		
		
		/**
         * FONT EXAMPLE - Use Standard Java fonts for substitution
         * 
         * This code tells JPedal to substitute fonts which are not embedded.
         * 
         * The Name is not case-sensitive.
         * 
         * Spaces are important so TimesNewRoman and Times New Roman are
         * degarded as 2 fonts.
         * 
         * If you have 2 copies of arialMT.ttf in the scanned directories, the
         * last one will be used.
         * 
         * 
         * If you wish to use one of Javas fonts for display (for example, Times
         * New Roman is a close match for myCompanyFont in the PDF, you can the
         * code below
         * 
         * String[] aliases={"Times New Roman"};//,"helvetica","arial"};
         * decode_pdf.setSubstitutedFontAliases("myCompanyFont",aliases);
         * 
         * Here is is used to map Javas Times New Roman (and all weights) to 
         * TimesNewRoman.
         * 
         * This can also be done with the command -TTfontMaps="TimesNewRoman=Times New Roman","font2=pdfFont1"
         */
		
		//String[] nameInPDF={"TimesNewRoman"};//,"helvetica","arial"}; //$NON-NLS-1$
		//decode_pdf.setSubstitutedFontAliases("Times New Roman",nameInPDF); //$NON-NLS-1$
		//<end-example>
		
		/**
		 * general setup for GUI
		 */
		
		c = getContentPane();
	    c.setLayout(new BorderLayout());
		
		RepaintManager rm = RepaintManager.currentManager(this);
		rm.setDoubleBufferingEnabled(true);

		/**add the pdf display to show pages*/
		scrollPane.getViewport().add(decode_pdf);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setUnitIncrement(80);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(80);
		
		/**create scrollpanes*/
		bookmarkScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		bookmarkScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		/**Create a left-right split pane with tabs 
		 * and add to main display 
		 */
		navOptionsPanel.setTabPlacement(JTabbedPane.TOP);
		displayPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, navOptionsPanel, scrollPane);
		
		displayPane.setOneTouchExpandable(true);
		c.add(displayPane, BorderLayout.CENTER);
		
    }
    
    //<start-example>
	
    //<end-example>

    //<start-example>
    /**
     * example code which sets up an individual icon for each annotation to display - only use
     * if you require each annotation to have its own icon<p>
     * To use this you ideally need to parse the annotations first -there is a method allowing you to
     * extract just the annotations from the data.
     */
    private void createUniqueAnnontationIcons() {
        
        int pages=20;// hard-code. IF you want to use 
        //decode_pdf.getPageCount(); , you will need to use this method AFTER the openFile call!!!
	
        int max=20; //you will need to adapt to suit
		
        //this code will either do all or just 1 - comment out to suit
        //for (int types = 0; types < this.annotTypes.length; types++) { //all annots
            { int types=1; //just 1 type

            for (int p = 1; p < pages + 1; p++) {

                Image[] annotIcons = new Image[max];

                for (int i = 0; i < max; i++) {

                    //create a unique graphic
                    annotIcons[i] = new BufferedImage(32, 32,BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2 = (Graphics2D) annotIcons[i].getGraphics();
                    g2.setColor(Color.black);
                    g2.fill(new Rectangle(0, 0, 32, 32));
                    g2.setColor(Color.red);
                    g2.draw(new Rectangle(0, 0, 32, 32));
                    g2.setColor(Color.white);
                    g2.drawString((p + "/" + i + annotTypes[types]),0, 10); //$NON-NLS-1$

                }

                //add set of icons to display
                decode_pdf.addUserIconsForAnnotations(p,annotTypes[types],annotIcons);
                
            }
        }   
    }
    //<end-example>

    /**
     * Scans sublist to get the children bookmark nodes.
     *
     * @param rootNode Node
     * @param topNode DefaultMutableTreeNode
     */
	private void readChildNodes(Node rootNode,DefaultMutableTreeNode topNode) {
		
	    NodeList children=rootNode.getChildNodes();
		int childCount=children.getLength();
		
		for(int i=0;i<childCount;i++){
			
			Node child=children.item(i);
			
			Element currentElement = (Element) child;
			
			String title=currentElement.getAttribute("title"); //$NON-NLS-1$
			String page=currentElement.getAttribute("page"); //$NON-NLS-1$
			String rawDest=currentElement.getAttribute("Dest"); //$NON-NLS-1$
			String ref=currentElement.getAttribute("objectRef");//$NON-NLS-1$
			
			/**create the lookup table*/
			pageLookupTable.put(title,page);
			
			/**create the point lookup table*/
			if((rawDest!=null)&&(rawDest.indexOf("/XYZ")!=-1)){ //$NON-NLS-1$
				
				rawDest=rawDest.substring(rawDest.indexOf("/XYZ")+4); //$NON-NLS-1$
				
				StringTokenizer values=new StringTokenizer(rawDest,"[] "); //$NON-NLS-1$
				
				//ignore the first, read next 2
				//values.nextToken();
				String x=values.nextToken();
				if(x.equals("null")) //$NON-NLS-1$
					x="0"; //$NON-NLS-1$
				String y=values.nextToken();
				if(y.equals("null")) //$NON-NLS-1$
					y="0"; //$NON-NLS-1$
				
				pointLookupTable.put(title,new Point((int) Float.parseFloat(x),(int) Float.parseFloat(y)));
			}
			
			DefaultMutableTreeNode childNode =new DefaultMutableTreeNode(title);
			
			/**add the nodes or initialise to top level*/	
			topNode.add(childNode);
			
			
			if(child.hasChildNodes())
				readChildNodes(child,childNode);
						
		}
	}
	
	/**
	 * Expand all nodes found from the XML outlines for the PDF.
	 */
	private void expandAll() {
	    int row = 0;
	    while (row < tree.getRowCount()) {
	        tree.expandRow(row);
	        row++;
	    }
	    
	}
	  
	/**
	 *  put the outline data into a display panel which we can pop up 
	 * for the user - outlines, thumbnails
	 */
	private void createOutlinePanels() {
	   
	    boolean hasNavBars=false;
	    
	    //<start-example>
	    /**
	     * set up first 10 thumbnails by default. Rest created as needed.
	     */
	     //add if statement or comment out this section to remove thumbnails
	    if(showThumbnails){
	        hasNavBars=true;
	       
	        int pages=decode_pdf.getPageCount();
	        
	        //setup and add to display
	        
	        //##Token:PdfViewerTitle.thumbnails=Thumbnails
	        navOptionsPanel.add(thumbnails.setupThumbnails(pages,textFont,getMessage("PdfViewerPageLabel.text"),decode_pdf.getPdfPageData())
		    ,getMessage("PdfViewerTitle.thumbnails"));
	        
		    //add listener so clicking on button changes to page - has to be in SimpleViewer so it can update it
            for(int i=0;i<pages;i++)
            	thumbnails.getButton(i).addActionListener(new PageChanger(i));
		    
	        //<start-13>
            //add global listener
	        thumbnails.addComponentListener(painter);
	        //<end-13>
	        
	    }
	    //<end-example>
	    
	    /**
	     * add any outline
	     */
	    if(decode_pdf.hasOutline()&& showOutlines){
	        
	        hasNavBars=true;
	
	        DefaultMutableTreeNode top =new DefaultMutableTreeNode("Root"); //$NON-NLS-1$
	        
	        /**
	         * default settings for bookmarks for each page
	         */
	        //defaultRefsForPage=decode_pdf.getOutlineDefaultReferences();
	        //this.defaultPageLookup=new TreeNode[this.pageCount];
	        
	        /**graphical display*/
	        Node rootNode= decode_pdf.getOutlineAsXML().getFirstChild();
	        
	        if(rootNode!=null){
	            readChildNodes(rootNode,top);
	            
	            tree=new JTree(top);
		        expandAll();
		        tree.setRootVisible(false);
		        
		        //create dispaly for bookmarks
		        bookmarkScrollPane.getViewport().add(tree);
		        //##Token:PdfViewerTitle.bookmarks=Bookmarks
		        navOptionsPanel.add(bookmarkScrollPane,getMessage("PdfViewerTitle.bookmarks"));
		        
		        //Listen for when the selection changes - looks up dests at present
		        tree.addTreeSelectionListener(new TreeSelectionListener(){
		            
		            /** Required by TreeSelectionListener interface. */
		            public void valueChanged(TreeSelectionEvent e) {
		                
		            	if(ignoreAlteredBookmark)
		            		return;
		            		
		                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		                tree.getLastSelectedPathComponent();
		                
		                if (node == null) return;
		                
		                Object nodeInfo = node.getUserObject();
		                
		                /**get title and open page if valid*/
		                String title=(String)node.getUserObject();
		                String page=(String) pageLookupTable.get(title);
		                
		                if((page!=null)&&(page.length()>0)){
		                    int pageToDisplay=Integer.parseInt(page);
		                    
		                    if((!isProcessing)&&(currentPage!=pageToDisplay)){
		                        currentPage=pageToDisplay;
		                        /**reset as rotation may change!*/
		                        scalingBox.setSelectedIndex(defaultSelection); 
		        				   decode_pdf.setPageParameters(scaling, currentPage);
		        				decodePage(false);
		                    }
		                    
		                    Point p= (Point) pointLookupTable.get(title);
		                    if(p!=null)
		                        decode_pdf.ensurePointIsVisible(p);
		             
		                }else
		                    System.out.println("No dest page set for "+title);	 //$NON-NLS-1$
		            }
		        });
		        
		        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	        }else{
	            //there is a nav bar but no values so do not display
	            hasNavBars=false;
	            bookmarksButton.setEnabled(false);
	        }
	    }
	    
	    /**
	     * resize to show if there are nav bars
	     */
		if(hasNavBars){
		    //<start-example>
			if(!showThumbnails){
			    navOptionsPanel.setVisible(true);   
				displayPane.setDividerLocation(divLocation);
				displayPane.invalidate();
				displayPane.repaint();
				
			}
			//<end-example>
        }
		
		/*
		else{
        	if(navOptionsPanel.isVisible())
        		mainFrame.setSize(mainFrame.getWidth(),mainFrame.getHeight());
        }
		 */
	}
	
    /**
     * remove outlines and flag for redraw
     */
    private void removeOutlinePanels() {
        /**
	     * reset left hand nav bar
	    */
        //<start-example>
        thumbnails.removeAll();
	    bookmarkScrollPane.setMinimumSize(new Dimension(50,getHeight()));
	    if(!showThumbnails) //<end-example>
	    navOptionsPanel.setVisible(false);
	    navOptionsPanel.removeAll();
	    
	    /**flag for redraw*/
	    hasOutlinesDrawn=false;
	    
	    /**update GUI button*/
	    bookmarksButton.setEnabled(decode_pdf.hasOutline());
    }

	//<start-example>
	//<start-13>
	/** class to paint thumbnails */
	private class ThumbPainter extends ComponentAdapter {

		/** used to track user stopping movement */
		Timer trapMultipleMoves = trapMultipleMoves = new Timer(250,
				new ActionListener() {

					public void actionPerformed(ActionEvent event) {

						if (worker != null)
							worker.interrupt();

						/** if running wait to finish */
						while (isProcessing) {
							try {
								Thread.sleep(20);
							} catch (InterruptedException e) {
								// should never be called
								e.printStackTrace();
							}
						}
						
						/**create any new thumbnails revaled by scroll*/
						thumbnails.drawVisibleThumbnailsOnScroll(decode_pdf);

					}
				});

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
		 */
		public void componentMoved(ComponentEvent e) {

			//allow us to disable on scroll
			if (trapMultipleMoves.isRunning())
				trapMultipleMoves.stop();

			trapMultipleMoves.setRepeats(false);
			trapMultipleMoves.start();
			
		}
	}
	//<end-13>
	//<end-example>
	
    class PageChanger implements ActionListener {

	    int page;
	    public PageChanger(int i){
	        i++;
	        page=i;
	    }
	    
		public void actionPerformed(ActionEvent e) {
		    if((!isProcessing)&&(currentPage!=page)){
		        currentPage=page;
		        
		        statusBar.resetStatus(""); //$NON-NLS-1$
		        
		        scalingBox.setSelectedIndex(defaultSelection); 
		        decode_pdf.setPageParameters(scaling, currentPage);
		        
		        decodePage(false);
		        
		    }
		}
	}
    
    /**
	 * get raw co-ords and convert to correct scaled units
	 * @return int[] of size 2, [0]=new x value, [1] = new y value
	 */
	protected int[] updateXY(MouseEvent event) {
		
	    //get co-ordinates of top point of outine rectangle
        x=(int)((event.getX()-inset)/scaling);
        y=(int)((event.getY()-inset)/scaling);
        
	    //undo any viewport scaling
		if(maxViewY!=0){ // will not be zero if viewport in play
			x=(int)(((x-(dx*scaling))/viewportScale));
			y=(int)((mediaH-((mediaH-(y/scaling)-dy)/viewportScale))*scaling);
		}
		
		int[] ret=new int[2];
	    if(rotation==90){	        
	        ret[1] = x+cropY;
	        ret[0] =y+cropX;
	    }else if((rotation==180)){
	        ret[0]=mediaW- (x+mediaW-cropW-cropX);
	        ret[1] =y+cropY;
	    }else if((rotation==270)){
	        ret[1] =mediaH- (x+mediaH-cropH-cropY);
	        ret[0]=mediaW-(y+mediaW-cropW-cropX);
	    }else{
	        ret[0] = x+cropX;
	        ret[1] =mediaH-(y+mediaH-cropH-cropY);    
	}
		return ret;
	}
	
    //<start-example>
	/**listener used to update display*/
	protected class mouse_mover implements MouseMotionListener {
		
		public mouse_mover() {}
		
		public void mouseDragged(MouseEvent event) {
			
			int[] values = updateXY(event);
			m_x2=values[0];
			m_y2=values[1];
			
		    scrollAndUpdateCoords(event);
    
		    generateNewCursorBox();
		    
		    checkLinks(false);
		    
		}
		
		/**
		 * scroll to visible Rectangle and update Coords box on screen
		 */
		protected void scrollAndUpdateCoords(MouseEvent event) {
			//scroll if user hits side
			int interval=decode_pdf.getScrollInterval();
		    Rectangle visible_test=new Rectangle(event.getX(),event.getY(),interval,interval);
		    if((allowScrolling)&&(!decode_pdf.getVisibleRect().contains(visible_test)))
		        decode_pdf.scrollRectToVisible(visible_test);
		    
		    updateCords(event);
		}

		/**
		 * generate new  cursorBox and highlight extractable text,
		 * if hardware acceleration off and extraction on<br>
		 * and update current cursor box displayed on screen
		 */
		protected void generateNewCursorBox() {
			
			//redraw rectangle of dragged box onscreen if it has changed significantly
		    if ((old_m_x2!=-1)|(old_m_y2!=-1)|(Math.abs(m_x2-old_m_x2)>5)|(Math.abs(m_y2-old_m_y2)>5)) {	
		       
		        //allow for user to go up
		        int top_x = m_x1;
		        if (m_x1 > m_x2)
		            top_x = m_x2;
		        int top_y = m_y1;
		        if (m_y1 > m_y2)
		            top_y = m_y2;
		        int w = Math.abs(m_x2 - m_x1);
		        int h = Math.abs(m_y2 - m_y1);
		        
		        //add an outline rectangle  to the display
		        currentRectangle=new Rectangle (top_x,top_y,w,h);
		        decode_pdf.updateCursorBoxOnScreen(currentRectangle,Color.blue);
		        
		        //tell JPedal to highlight text in this area (you can add other areas to array)
		        Rectangle[] highlightedAreas=new Rectangle[2];
		        highlightedAreas[0]=currentRectangle;//change to, redraw increase hieght area. chris
		        //highlightedAreas[1]=new Rectangle(300,300,100,100);// - try me to see extra highlights
		        /**to view highlighted ares ensure that setHardwareAccelerationforScreen(boolean) is set to false*/
		        decode_pdf.setHighlightedAreas(highlightedAreas);
		        
		        //reset tracking
		        old_m_x2=m_x2;
		        old_m_y2=m_y2;
		        
		    }
		}

		public void mouseMoved(MouseEvent event) {
			
			updateCords(event);
			checkLinks(false);
		}
			
	}
	//<end-example>
	
	//<start-example>
	/**
     * picks up clicks so we can draw an outline on screen
     */
    protected class mouse_clicker extends MouseAdapter {
    	
        //user has pressed mouse button so we want to use this 
        //as one point of outline
        public void mousePressed(MouseEvent event) {
        	
            //get co-ordinates of top point of outine rectangle
            int x=(int)((event.getX()-inset)/scaling);
            int y=(int)((event.getY()-inset)/scaling);
            
            //undo any viewport scaling (no crop assumed
    		if(maxViewY!=0){ // will not be zero if viewport in play
    			x=(int)(((x-(dx*scaling))/viewportScale));
    			y=(int)((mediaH-((mediaH-(y/scaling)-dy)/viewportScale))*scaling);
    		}
    		
            if (rotation == 90) {
                m_y1 = x+cropY;
                m_x1 = y+cropX;
            } else if ((rotation == 180)) {
                m_x1 = mediaW - (x+mediaW-cropW-cropX);
                m_y1 = y+cropY;
            } else if ((rotation == 270)) {
                m_y1 = mediaH - (x+mediaH-cropH-cropY);
                m_x1 = mediaW - (y+mediaW-cropW-cropX);
            } else {
                m_x1 = x+cropX;
                m_y1 = mediaH - (y+mediaH-cropH-cropY);
            }

            updateCords(event);

        }

        //show the description in the text box or update screen
        public void mouseClicked(MouseEvent event) {
        	
            checkLinks(true);
        }

        //user has stopped clicking so we want to remove the outline rectangle
        public void mouseReleased(MouseEvent event) {
        	
            old_m_x2 = -1;
            old_m_y2 = -1;

            updateCords(event);

			/** extract text */
            if (event.isShiftDown()){
            }
            
            /** remove any outline and reset variables used to track change */
            decode_pdf.updateCursorBoxOnScreen(null, null); //remove box
            decode_pdf.setHighlightedAreas(null); //remove highlighted text
            
            decode_pdf.repaintArea(new Rectangle(m_x1-cropX, m_y2+cropY, m_x2 - m_x1+cropX,
                    (m_y1 - m_y2)+cropY), mediaH);//redraw
            decode_pdf.repaint();
        }
    }
	
	/**checks the link areas on the page for mouse entering. Provides 
	 * option to behave differently on mouse click. Note code will not check
	 * multiple links only first match.
	 * */
	public void checkLinks(boolean mouseClicked){
		
		message=""; //$NON-NLS-1$
		
		//get hotspots for the page
		Rectangle[] hotSpots=decode_pdf.getPageHotspots();
		
		if(hotSpots!=null){
			int count=hotSpots.length;
			int matchFound=-1;
			
			//look for first match
			for(int i=0;i<count;i++){
				if((hotSpots[i]!=null)&&(hotSpots[i].contains(cx,cy))){
					
					matchFound=i;
					i=count;
				}
			}
			
			/**action for moved over of clicked*/
			if(matchFound!=-1){
			    
//				mouseClicked = false; 
			    //forms now active so annotations popup disabled
				
				if(mouseClicked){
				    
					//get values in Annotation
					Map annotDetails=this.pageAnnotations.getAnnotRawData(matchFound);
					
					Map annotAction=(Map) annotDetails.get("A"); //$NON-NLS-1$
					
					String subtype=pageAnnotations.getAnnotSubType(matchFound);
					
					if((subtype.equals("Link"))&&(annotAction!=null)){ //$NON-NLS-1$
						Iterator keys=annotAction.keySet().iterator();
						
						//just build a display
						JPanel details=new JPanel();
						//<start-13>
						details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
						//<end-13>
						
						while(keys.hasNext()){
							String nextKey=(String) keys.next();
							details.add(new JLabel(nextKey+" : "+annotDetails.get(nextKey))); //$NON-NLS-1$
						}
						
						//##Token:PdfViewerTitle.annots=Annotation Properties
						JOptionPane.showMessageDialog(this,details,getMessage("PdfViewerTitle.annots"),JOptionPane.PLAIN_MESSAGE);
						
					}else if(subtype.equals("Text")){ //$NON-NLS-1$
					    
					    String title=pageAnnotations.getField(matchFound,"T"); //$NON-NLS-1$
					    if(title==null)
					    		//##Token:PdfViewerAnnots.notitle=No Title
					        title=getMessage("PdfViewerAnnots.notitle"); 
					    
					    String contents=pageAnnotations.getField(matchFound,"Contents"); //$NON-NLS-1$
					    if(contents==null)
					    		//##Token:PdfViewerAnnots.nocont=No Contents
					        contents=getMessage("PdfViewerAnnots.nocont"); 
					    JOptionPane.showMessageDialog(this,new TextArea(contents),title,JOptionPane.PLAIN_MESSAGE);
					
					}else if(subtype.equals("FileAttachment")){ //saves file (Adobe default is to open the file, but Java does not have a simpel open command. //$NON-NLS-1$
					    
					    //drill down to file held as binary stream
					    Map fileDetails=(Map) annotDetails.get("FS"); //$NON-NLS-1$
					    if(fileDetails!=null)
					        fileDetails=(Map) fileDetails.get("EF"); //$NON-NLS-1$
					    if(fileDetails!=null)
					        fileDetails=(Map) fileDetails.get("F"); //$NON-NLS-1$
					    
					    if(fileDetails!=null){
					        byte[] file=(byte[]) fileDetails.get("DecodedStream"); //$NON-NLS-1$
					        
					        if(file==null)
					        		//##Token:PdfViewerAnnots.nofile=No File Embedded
					            JOptionPane.showMessageDialog(this,getMessage("PdfViewerAnnots.nofile")); 
					        else{
					            /**
					             * create the file chooser to select the file name
					             */
					            JFileChooser chooser = new JFileChooser(inputDir);
					            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					            
					            int state = chooser.showSaveDialog(this);
					            if(state==0){
						            File fileTarget = chooser.getSelectedFile();
						            FileOutputStream fos;
						            try {
						                fos = new FileOutputStream(fileTarget);
						                fos.write(file);
							            fos.close();
						            } catch (Exception e) {
						                e.printStackTrace();
						            }
					            }
					        }
					    }
					    
					}else{ //type not yet implemented so just display details
					    
					    JPanel details=new JPanel();
					    details.setLayout(new BoxLayout(details,BoxLayout.Y_AXIS));
					    Iterator keys=annotDetails.keySet().iterator();
					    while(keys.hasNext()){
							String nextKey=(String) keys.next();
							details.add(new JLabel(nextKey+" : "+annotDetails.get(nextKey))); //$NON-NLS-1$
						}
					    
					    JOptionPane.showMessageDialog(
					    		//##Token:PdfViewerAnnots.nosubtype=Unimplemented subtype
								this,details,getMessage("PdfViewerAnnots.nosubtype") //$NON-NLS-1$
								+" "+subtype,JOptionPane.PLAIN_MESSAGE); //$NON-NLS-1$
					}
				}else
					//##Token:PdfViewerAnnots.entered=Entered link
					message=getMessage("PdfViewerAnnots.entered")+" "+matchFound;  //$NON-NLS-2$
			}
		}
	}

	//<end-example>

	//<start-example>
	/**
	 * do xml as nicely coloured text
	 */
	protected final JScrollPane createPane(JTextPane text_pane,String content, boolean useXML) throws BadLocationException {
	    
	    text_pane.setEditable(true);
	    text_pane.setFont(new Font("Lucida", Font.PLAIN, 14)); //$NON-NLS-1$
	    
	    //##Token:PdfViewerTooltip.text=Text can be selected and copied to clipboard
	    text_pane.setToolTipText(getMessage("PdfViewerTooltip.text")); 
	    Document doc = text_pane.getDocument();
	    //##Token:PdfViewerTitle.text=Extracted Content
	    text_pane.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), getMessage("PdfViewerTitle.text"))); 
	    text_pane.setForeground(Color.black);
	    
	    SimpleAttributeSet token_attribute = new SimpleAttributeSet();
	    SimpleAttributeSet text_attribute = new SimpleAttributeSet();
	    SimpleAttributeSet plain_attribute = new SimpleAttributeSet();
	    StyleConstants.setForeground(token_attribute, Color.blue);
	    StyleConstants.setForeground(text_attribute, Color.black);
	    StyleConstants.setForeground(plain_attribute, Color.black);
	    int pointer=0;
	   
        /**put content in and color XML*/
        if((useXML)&&(content!=null)){
            //tokenise and write out data
            StringTokenizer data_As_tokens = new StringTokenizer(content,"<>", true); //$NON-NLS-1$
            while (data_As_tokens.hasMoreTokens()) {
                String next_item = data_As_tokens.nextToken();
                
                if ((next_item.equals("<"))&&((data_As_tokens.hasMoreTokens()))) { //$NON-NLS-1$
                    
                    String current_token = next_item + data_As_tokens.nextToken()+ data_As_tokens.nextToken();
                    
                    doc.insertString(pointer, current_token,token_attribute);
                    pointer = pointer + current_token.length();
                    
                } else {
                    doc.insertString(pointer, next_item, text_attribute);
                    pointer = pointer + next_item.length();
                }
            }
        }else
            doc.insertString(pointer,content, plain_attribute);
        
        //wrap in scrollpane
		JScrollPane text_scroll = new JScrollPane();
		text_scroll.getViewport().add( text_pane );
		text_scroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		text_scroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
		return text_scroll;
	}
	
	//<end-example>
	
	//<start-example>
	/**update current page co-ordinates on screen*/
	public void updateCords(MouseEvent event){
		
		int ex=event.getX()-inset;
		int ey=event.getY()-inset;
		
		//undo any viewport scaling
		if(maxViewY!=0){ // will not be zero if viewport in play
			ex=(int)(((ex-(dx*scaling))/viewportScale));
			ey=(int)((mediaH-((mediaH-(ey/scaling)-dy)/viewportScale))*scaling);
		}
		
		cx=(int)((ex)/scaling);
		cy=(int)((ey/scaling));
		
		if(rotation==90){
			int tmp=(int)(cx+cropY);
			cx = (int)(cy+cropX);
			cy =tmp;	
		}else if((rotation==180)){
			cx =mediaW-(int)(cx+mediaW-cropW-cropX);
			cy =(int)(cy+cropY);
		}else if((rotation==270)){
			int tmp=mediaH-(int)(cx+mediaH-cropH-cropY);
			cx =mediaW-(int)(cy+mediaW-cropW-cropX);
			cy =tmp;
		}else{
			cx = (int)(cx+cropX);
			cy =mediaH-(int)(cy+mediaH-cropH-cropY);
		}

		if((isProcessing)|(selectedFile==null))
		    coords.setText("  X: "+ " Y: " + " "+" "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		else
		    coords.setText("  X: " + cx + " Y: " + cy+ " "+" "+message); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		
		//scroll if user hits side and shift key not pressed
		if((allowScrolling)&&(!event.isShiftDown())){
			int interval=decode_pdf.getScrollInterval()*2;
			Rectangle visible_test=new Rectangle(event.getX()-interval,event.getY()-interval,interval*2,interval*2);
			if(!decode_pdf.getVisibleRect().contains(visible_test))
				decode_pdf.scrollRectToVisible(visible_test);
		}
	}
	//<end-example>
	
	//exit on window closed
	  protected class FrameCloser extends WindowAdapter {
		 public void windowClosing(WindowEvent e) {
		 	
		 	if(target!=null)
		 		flush();
		 	
		 	if(printingThreads>0)
		 		//##Token:PdfViewerBusyPrinting.message=Pages are printing...
		 		JOptionPane.showMessageDialog(c,getMessage("PdfViewerBusyPrinting.message"));
		 	if(!isProcessing){
//		 		##Token:PdfViewerCloseing.message=Are you sure you want to close?
		 		int confirm=JOptionPane.showConfirmDialog(c,getMessage("PdfViewerCloseing.message"),null,JOptionPane.YES_NO_OPTION);
		 		
		 		if(confirm==0){
		 			decode_pdf.closePdfFile();
		 			System.exit(0);
		 		}
				/**/
		 		
		 	}else{
//		 		##Token:PdfViewerDecodeWait.message=Please wait for page to display.
		 		JOptionPane.showMessageDialog(c,getMessage("PdfViewerDecodeWait.message"));
		 	}
		 }
		  public FrameCloser() {}
	  }

	  //<start-example>
	/**
	 * create GUI display with a drop down menu and icons at top and
	 * also add actions to commands - designed so you can over-ride 
	 */
	protected void setupGUI() {
		
		init(null);

		/**
		 * add a title
		 */
		//##Token:PdfViewerOs.message=JPedal GUI Open Source version - version
		setViewerTitle(getMessage("PdfViewerOs.message")+"  " + PdfDecoder.version);
			/**/
		
		/**
		 * setup add tools on top of screen
		 */
		initGUIComponents();		
		
		/**
		 * create a menu bar and add to display
		 */
		JPanel top = new JPanel();
		top.setLayout(new BorderLayout());
		getContentPane().add(top, BorderLayout.NORTH);
		
		/**
		 * track and display screen co-ordinates and support links
		 */
		decode_pdf.addMouseMotionListener(new mouse_mover());
		decode_pdf.addMouseListener(new mouse_clicker());
		
		/**
		 *setup menu and create options
		 */
		JMenuBar currentMenu = new JMenuBar();
		top.add(currentMenu, BorderLayout.NORTH);

		/**
		 * add menu options
		 */
		createMenu(currentMenu,true);
		
		/**
		 * create tool bars and add to display
		 */
		JToolBar currentBar1 = new JToolBar();
		currentBar1.setBorder(BorderFactory.createEmptyBorder());
		currentBar1.setLayout(new FlowLayout(FlowLayout.LEADING));
		currentBar1.setFloatable(false);
		currentBar1.setFont(new Font("SansSerif", Font.PLAIN, 8)); 
		top.add(currentBar1, BorderLayout.CENTER);
		
		JToolBar currentBar2 = new JToolBar();
		currentBar2.setBorder(BorderFactory.createEmptyBorder());
		currentBar2.setLayout(new FlowLayout(FlowLayout.LEADING));
		currentBar2.setFloatable(false);
		currentBar2.setFont(new Font("SansSerif", Font.PLAIN, 8)); 
		top.add(currentBar2, BorderLayout.SOUTH);
		
		/**
		 * shortcut buttons to press
		 */
		createButtons(currentBar1);	
		
		currentBar1.add(Box.createHorizontalGlue());
		currentBar1.add(Box.createHorizontalGlue());
		currentBar1.add(Box.createHorizontalGlue());
		currentBar1.add(Box.createHorizontalGlue());
		
		/**
		 * forward, backward
		 **/
		createPageNavigation(currentBar1);

		/**
		 * zoom,scale,rotation, status,cursor
		 */
		createPageSizingButtons(currentBar2);
		
		/**
		 * set display to occupy half screen size and display, add listener and
		 * make sure appears in centre
		 */
		setupScreen();
		
	}
	//<end-example>

	/**
	 * put screen in middle with sized window
	 */
	protected void setupScreen() {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int width = d.width / 2, height = d.height / 2;
		if(width<minimumScreenWidth)
			width=minimumScreenWidth;
		
		setSize(width, height);
		
		/**move over if we will display thumbnails*/
		if (showThumbnails)
		    displayPane.setDividerLocation(thumbLocation);
			//width=width+thumbLocation;
		
		//<start-13>
		//centre on screen
		setLocationRelativeTo(null);
		/*<end-13>
		setLocation((d.width-getWidth())/2,(d.height-getHeight())/2);
		/**/
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new FrameCloser());
		
		show();
	}


	/**
	 * zoom, rotation, scale, status bar and cursor location
	 */
	protected void createPageSizingButtons(JToolBar currentBar2) {
		
		/**zoom in*/	
		//##Token:PdfViewerTooltip.scaling=Scaling
		JLabel plus = new JLabel(getMessage("PdfViewerTooltip.scaling"));
		currentBar2.add(plus);
		//##Token:PdfViewerTooltip.zoomin=zoom in
		plus.setToolTipText(getMessage("PdfViewerTooltip.zoomin"));
		currentBar2.add(scalingBox);
		scalingBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				if(!isProcessing){
					if(selectedFile!=null)
						zoom();
				}
			}
		});
		
		/**add a gap in display*/
		currentBar2.add(Box.createHorizontalGlue());

		/**page rotation option*/	
		//##Token:PdfViewerRotation.text=Rotation
		JLabel rotationLabel = new JLabel(getMessage("PdfViewerRotation.text")); 
		//##Token:PdfViewerTooltip.rotation=Rotation in degrees
		rotationLabel.setToolTipText(getMessage("PdfViewerTooltip.rotation")); 
		currentBar2.add(rotationLabel);
		currentBar2.add(rotationBox);
		rotationBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			if(selectedFile!=null){
			    rotation=Integer.parseInt((String) rotationBox.getSelectedItem());
			    //decode_pdf.setPageRotation(rotation);//not needed as zoom does it for use
			    
			    // if scaling to window reset screen to fit rotated page
			    //commented out for accelerated images should work on all
			//	if(scalingBox.getSelectedIndex()<3){//<start-example>
					zoom();
				//}//<end-example>
				
				//decode_pdf.repaint();
				decode_pdf.updateUI();
				}
			}
		});
		
		//set status bar child color
		statusBar.setColorForSubroutines(Color.blue);
		//and initialise the display
		currentBar2.add(statusBar.getStatusObject());
		
		/**add cursor location*/
		JToolBar cursor = new JToolBar();
		cursor.setBorder(BorderFactory.createEmptyBorder());
		cursor.setLayout(new FlowLayout(FlowLayout.LEADING));
		cursor.setFloatable(false);
		cursor.setFont(new Font("SansSerif", Font.ITALIC, 10)); //$NON-NLS-1$
		//##Token:PdfViewerCursorLoc=Cursor at:
		cursor.add(new JLabel(getMessage("PdfViewerCursorLoc"))); 
		currentBar2.add(Box.createHorizontalGlue());
		
		cursor.add(coords);
		coords.setBackground(Color.white);
		coords.setOpaque(true);
		coords.setBorder(BorderFactory.createLineBorder(Color.black,1));
		
		currentBar2.add(cursor);
	}
	
	/**
	 * create a pressed look of the <b>icon</b> and added it to the pressed Icon of <b>button</b>
	 */
	protected void createPressedLook(AbstractButton button, ImageIcon icon) {
		BufferedImage image = new BufferedImage(icon.getIconWidth()+2,icon.getIconHeight()+2,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)image.getGraphics();
        g.drawImage(icon.getImage(), 1, 1, null);
        g.dispose();
        ImageIcon iconPressed = new ImageIcon(image);
        button.setPressedIcon(iconPressed);
	}

	/**
	 * setup up the buttons
	 */
	private void createButtons(JToolBar currentBar1) {
		
		openButton(currentBar1);
		
		/**print icon*/
		JButton printButton = new JButton();
		printButton.setBorderPainted(false);
		ImageIcon printIcon = new ImageIcon(ClassLoader.getSystemResource("org/jpedal/examples/simpleviewer/print.gif")); //$NON-NLS-1$
		printButton.setIcon(printIcon);
		createPressedLook(printButton,printIcon);
		//##Token:PdfViewerPrint.tip=Print the current PDF file
		printButton.setToolTipText(getMessage("PdfViewerPrint.tip")); //$NON-NLS-1$
		currentBar1.add(printButton);
		printButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        if(printingThreads>0)
		        	//##Token:PdfViewerPrintWait.message=Please wait for printing to complete
					JOptionPane.showMessageDialog(c,getMessage("PdfViewerPrintWait.message"));
				else if(isProcessing)
					//##Token:PdfViewerDecodeWait.message=Please wait for page to display
					JOptionPane.showMessageDialog(c,getMessage("PdfViewerDecodeWait.message"));
				else  if(selectedFile!=null){
		            if(printingThreads==0)
		                printDialog();
		            else
		            		//##Token:PdfViewerPrintFinish.message=Please wait for print to finish
		                JOptionPane.showMessageDialog(c,getMessage("PdfViewerPrintFinish.message"));
		        }else
		        		//##Token:PdfViewerNoFile.message=No file to print
		           JOptionPane.showMessageDialog(c,getMessage("PdfViewerNoFile.message"));
		    }
		});
		currentBar1.add(Box.createHorizontalGlue());
		
		/**bookmarks icon*/
		bookmarksButton.setBorderPainted(false);
		ImageIcon bookmarksIcon = new ImageIcon(ClassLoader.getSystemResource("org/jpedal/examples/simpleviewer/bookmarks.gif")); //$NON-NLS-1$
		bookmarksButton.setIcon(bookmarksIcon);
		createPressedLook(bookmarksButton,bookmarksIcon);
		if(showThumbnails)
			//##Token:PdfViewerShowide.bookmarks=Show/Hide any bookmarks and thumbnails in the current PDF file
		    bookmarksButton.setToolTipText(getMessage("PdfViewerShowide.bookmarks")); 
		else
			//##Token:PdfViewerShowHide2.bookmarks=Show/Hide any bookmarks in the current PDF file
		    bookmarksButton.setToolTipText(getMessage("PdfViewerShowHide2.bookmarks"));
		
		currentBar1.add(bookmarksButton);
		bookmarksButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {//toggle nav bar on/off
			    boolean current=!navOptionsPanel.isVisible();
			    navOptionsPanel.setVisible(current);
			    if(current)
			        displayPane.setDividerLocation(divLocation);
			    else
			        displayPane.setDividerLocation(0);
			        
			    //mainFrame.repaint();
			}
		});
		currentBar1.add(Box.createHorizontalGlue());
		
		createPropertyIcon(currentBar1);
		
		createFontButton(currentBar1);
		
		createAboutButton(currentBar1);
		

	}


	/**
	 * open button function
	 */
	protected void openButton(JToolBar currentBar1) {
		/**open icon*/
		JButton open = new JButton();
		ImageIcon openIcon = new ImageIcon(ClassLoader.getSystemResource("org/jpedal/examples/simpleviewer/open.gif"));//$NON-NLS-1$
		open.setIcon(openIcon);
		createPressedLook(open,openIcon);
		//##Token:PdfViewerTooltip.open=Open a file
		open.setToolTipText(getMessage("PdfViewerTooltip.open")); 
		open.setBorderPainted(false);
		currentBar1.add(open);
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/**if you require the ability to open files while printing you should 
				 * create a new PdfDecoder instance each time 
				 */
			    if(printingThreads>0)
			    		//##Token:PdfViewerPrintWait.message=Please wait for printing to complete
					JOptionPane.showMessageDialog(c,getMessage("PdfViewerPrintWait.message"));
				else if(isProcessing)
					//##Token:PdfViewerDecodeWait.message=Please wait for page to display
					JOptionPane.showMessageDialog(c,getMessage("PdfViewerDecodeWait.message"));
				else
				    selectFile();
			}
		});
		currentBar1.add(Box.createHorizontalGlue());
	}


	/**
	 * property icon button and function
	 */
	protected void createPropertyIcon(JToolBar currentBar1) {
		/**properties icon*/
		JButton propButton = new JButton();
		propButton.setBorderPainted(false);
		ImageIcon propIcon = new ImageIcon(ClassLoader.getSystemResource("org/jpedal/examples/simpleviewer/properties.gif")); //$NON-NLS-1$
		propButton.setIcon(propIcon);
		createPressedLook(propButton,propIcon);
		//##Token:PdfViewer.propsMessage=Show properties of current PDF file
		propButton.setToolTipText(getMessage("PdfViewer.propsMessage")); 
		currentBar1.add(propButton);
		propButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			   showPropertiesBox();
			}
		});
		currentBar1.add(Box.createHorizontalGlue());
	}


	/**
	 * about button and function
	 */
	protected void createAboutButton(JToolBar currentBar1) {
		/**about icon*/
		JButton aboutButton = new JButton();
		aboutButton.setBorderPainted(false);
		ImageIcon aboutIcon = new ImageIcon(ClassLoader.getSystemResource("org/jpedal/examples/simpleviewer/about.gif"));//$NON-NLS-1$
		aboutButton.setIcon(aboutIcon); 
		createPressedLook(aboutButton,aboutIcon);
		//##Token:PdfViewerTooltip.about=Message about library
		aboutButton.setToolTipText(getMessage("PdfViewerTooltip.about")); 
		currentBar1.add(aboutButton);
		aboutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    showInfoBox();
			}
		});
	}


	/**
	 * font button to show list of fonts
	 */
	protected void createFontButton(JToolBar currentBar1) {
		/**fonts icon*/
		JButton fontButton = new JButton();
		fontButton.setBorderPainted(false);
		ImageIcon fontIcon = new ImageIcon(ClassLoader.getSystemResource("org/jpedal/examples/simpleviewer/font.gif")); //$NON-NLS-1$
		fontButton.setIcon(fontIcon);
		createPressedLook(fontButton,fontIcon);
		//##Token:PdfViewerFontDetails.text=Show details of fonts on the current page
		fontButton.setToolTipText(getMessage("PdfViewerFontDetails.text")); //$NON-NLS-1$
		currentBar1.add(fontButton);
		fontButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			  	showFontsBox();
			}
		});
		currentBar1.add(Box.createHorizontalGlue());
	}


	/**
	 * navigation toolbar for moving between pages
	 */
	protected void createPageNavigation(JToolBar currentBar1) {
		
		/**back to page 1*/
		URL startImage =
		ClassLoader.getSystemResource(
		"org/jpedal/examples/simpleviewer/start.gif"); //$NON-NLS-1$
		first.setBorderPainted(false);
		first.setIcon(new ImageIcon(startImage));
		createPressedLook(first,new ImageIcon(startImage));
		//##Token:PdfViewerRewindToStart=Rewind to page 1
		first.setToolTipText(getMessage("PdfViewerRewindToStart"));
		currentBar1.add(first);
		first.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    if((selectedFile!=null)&&(pageCount>1)&&(currentPage!=1))
				back(currentPage-1);
			}
		});
		
		/**back 10 icon*/
		URL fbackImage =
		ClassLoader.getSystemResource(
		"org/jpedal/examples/simpleviewer/fback.gif"); //$NON-NLS-1$
		fback.setBorderPainted(false);
		fback.setIcon(new ImageIcon(fbackImage));
		createPressedLook(fback,new ImageIcon(fbackImage));
		//##Token:PdfViewerRewind10=Rewind 10 pages
		fback.setToolTipText(getMessage("PdfViewerRewind10"));
		currentBar1.add(fback);
		fback.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			if(selectedFile!=null)/**%%*/
				back(10);
			}
		});
		
		/**back icon*/
		URL backImage =
			ClassLoader.getSystemResource(
				"org/jpedal/examples/simpleviewer/back.gif"); //$NON-NLS-1$
		back.setBorderPainted(false);
		back.setIcon(new ImageIcon(backImage));
		createPressedLook(back,new ImageIcon(backImage));
		//##Token:PdfViewerRewind1=Rewind one page
		back.setToolTipText(getMessage("PdfViewerRewind1")); 
		currentBar1.add(back);
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			if(selectedFile!=null)
				back(1);
			}
		});

		/**put page count in middle of forward and back*/
		currentBar1.add(pageCounter1);
		currentBar1.add(pageCounter2);
		currentBar1.add(pageCounter3);

		/**forward icon*/
		URL forwardImage =
			ClassLoader.getSystemResource(
				"org/jpedal/examples/simpleviewer/forward.gif"); //$NON-NLS-1$
		forward.setBorderPainted(false);
		forward.setIcon(new ImageIcon(forwardImage));
		createPressedLook(forward,new ImageIcon(forwardImage));
		//##Token:PdfViewerForward1=forward 1 page
		forward.setToolTipText(getMessage("PdfViewerForward1")); 
		currentBar1.add(forward);
		forward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			if(selectedFile!=null)
				forward(1);
			}
		});
		
		/**fast forward icon*/
		URL fforwardImage =
		ClassLoader.getSystemResource(
		"org/jpedal/examples/simpleviewer/fforward.gif"); //$NON-NLS-1$
		fforward.setBorderPainted(false);
		fforward.setIcon(new ImageIcon(fforwardImage));
		createPressedLook(fforward,new ImageIcon(fforwardImage));
		//##Token:PdfViewerForward10=Fast forward 10 pages
		fforward.setToolTipText(getMessage("PdfViewerForward10")); 
		currentBar1.add(fforward);
		fforward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			if(selectedFile!=null)
				forward(10);
			}
		});

		/**goto last page*/
		URL endImage =
		ClassLoader.getSystemResource(
		"org/jpedal/examples/simpleviewer/end.gif"); //$NON-NLS-1$
		end.setBorderPainted(false);
		end.setIcon(new ImageIcon(endImage));
		createPressedLook(end,new ImageIcon(endImage));
		//##Token:PdfViewerForwardLast=Fast forward to last page
		end.setToolTipText(getMessage("PdfViewerForwardLast"));
		currentBar1.add(end);
		end.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			if((selectedFile!=null)&&(pageCount>1)&&(pageCount-currentPage>0))
				forward(pageCount-currentPage);
			}
		});

		/**add a gap in display*/
		currentBar1.add(Box.createHorizontalGlue());
	}

	/**
	 * initialise GUI components
	 */
	protected void initGUIComponents() {
		
		//##Token:PdfViewerScaleWindow
		//##Token:PdfViewerScaleHeight.text=Height
		final String[] scalingValues={getMessage("PdfViewerScaleWindow.text"),getMessage("PdfViewerScaleHeight.text"),
		//##Token:PdfViewerScaleWidth.text=Width
				getMessage("PdfViewerScaleWidth.text"), 
				"25","50","75","100","125","150","200","250","500","750","1000"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$
		this.scalingValues=scalingValues;
		float[] scalingFloatValues={1.0f,1.0f,1.0f,.25f,.5f,.75f,1.0f,1.25f,1.5f,2.0f,2.5f,5.0f,7.5f,10.0f};
		this.scalingFloatValues=scalingFloatValues;
		
		/**setup combo*/
		scalingBox=new JComboBox(scalingValues);
				
		//<start-13>
		//navOptionsPanel.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		//<end-13>
		if(!showThumbnails)
			navOptionsPanel.setVisible(false);
		
		/**
		 * set colours on display boxes and add listener to page number
		 */
		//pageCounter1.setEditable(false);
		//##Token:PdfViewerPageLabel.text=Page
		pageCounter1 = new JLabel(getMessage("PdfViewerPageLabel.text")); 
		pageCounter1.setOpaque(false);
		pageCounter2.setEditable(true);
		//##Token:PdfViewerTooltip.goto=To go to a page - Type in page number and press return
		pageCounter2.setToolTipText(getMessage("PdfViewerTooltip.goto")); 
		pageCounter2.setBorder(BorderFactory.createLineBorder(Color.black));
		pageCounter2.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent arg0) {
				
				String value=(String) pageCounter2.getText().trim();
				int newPage;
				
				//allow for bum values
				try{
					newPage=Integer.parseInt(value);
					
					if((newPage>decode_pdf.getPageCount())|(newPage<1)){
						//##Token:PdfViewerPageLabel.text=Page
						JOptionPane.showMessageDialog(c,getMessage("PdfViewerPageLabel.text")+ " "+ //$NON-NLS-2$
								//##Token:PdfViewerOutOfRange.text=is not in range. Total pagecount=
								value+" "+getMessage("PdfViewerOutOfRange.text")+" "+decode_pdf.getPageCount()); //$NON-NLS-1$  //$NON-NLS-3$
						newPage=currentPage;
						pageCounter2.setText(""+currentPage); //$NON-NLS-1$
					}
					
				}catch(Exception e){
					//##Token:PdfViewerInvalidNumber.text=is not valid. Please use a number (ie 4)
					JOptionPane.showMessageDialog(c,">"+value+ "< "+getMessage("PdfViewerInvalidNumber.text")); //$NON-NLS-1$ //$NON-NLS-2$ 
					newPage=currentPage;
					pageCounter2.setText(""+currentPage); //$NON-NLS-1$
				}
				
				//open new page
				if((!isProcessing)&&(currentPage!=newPage)){
					currentPage=newPage;
					decodePage(false);
				}
			}
			
		});
		
		//##Token:PdfViewerOfLabel.text=of
		pageCounter3=new JLabel(getMessage("PdfViewerOfLabel.text")+" "); 
		pageCounter3.setOpaque(false);
		
		scalingBox.setBackground(Color.white);
		scalingBox.setEditable(true);
		scalingBox.setPreferredSize(new Dimension(85,25));
		scalingBox.setSelectedIndex(defaultSelection); //set default before we add a listener
		
		rotationBox.setBackground(Color.white);
		rotationBox.setSelectedIndex(0); //set default before we add a listener
		
	}


	/**
	 * create items on drop down menus
	 */
	protected void createMenu(JMenuBar currentMenu,boolean includeAll) {
		
		//##Token:PdfViewerFile.text=File
		JMenu fileMenuList = new JMenu(getMessage("PdfViewerFile.text"));
		currentMenu.add(fileMenuList);
		
		if(includeAll){
			//##Token:PdfViewerHelp.text=Help
			JMenu help = new JMenu(getMessage("PdfViewerHelp.text")); 
			
			currentMenu.add(help);
			infoOption(help);
		}
		
		//##Token:PdfViewerMenu.options=Options
		JMenu options = new JMenu(getMessage("PdfViewerMenu.options")); 
		currentMenu.add(options);
		
		optionAutoscroll(options);
		
		
		
		if(includeAll){
			openOption(fileMenuList);
			saveOption(fileMenuList);
		}
		
		fontsOption(fileMenuList);
		
		docInfoOption(fileMenuList);
		
		pageInfoOption(fileMenuList);
		
		if(includeAll)
		printOption(fileMenuList);

		exitOption(fileMenuList);
	}


	/**
	 * setup autoscroll menu item
	 */
	private void optionAutoscroll(JMenu options) {
		//stop screen scrolling when it reaches edge
		//##Token:PdfViewerTitle.autoscroll=Toggle autoscrolling
		JMenuItem autoScroll = new JMenuItem(getMessage("PdfViewerTitle.autoscroll")); 
		//##Token:PdfViewerTooltip.autoscroll=Stops screen scrolling when reached edge of viewport
		autoScroll.setToolTipText(getMessage("PdfViewerTooltip.autoscroll")); 
		autoScroll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				
				allowScrolling=!allowScrolling;
			}
		});
		options.add(autoScroll);
	}


	/**
	 *setup menu exit option
	 */
	private void exitOption(JMenu file) {
		/**exit program option*/
		//##Token:PdfViewerExit.text=Exit
		JMenuItem exit = new JMenuItem(getMessage("PdfViewerExit.text")); 
		//##Token:PdfViewerShutdown.text=Shut down the program
		exit.setToolTipText(getMessage("PdfViewerShutdown.text")); 
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(printingThreads>0)
					//##Token:PdfViewerStillPrinting.text=Pages still printing
					JOptionPane.showMessageDialog(c,getMessage("PdfViewerStillPrinting.text"));
				else
				exit();
			}
		});
		file.add(exit);
	}


	/**
	 * setup menu print option
	 */
	private void printOption(JMenu file) {
		/**Print option*/
		//##Token:PdfViewerPrint.text=Print
		JMenuItem print = new JMenuItem(getMessage("PdfViewerPrint.text")); 
		file.add(print);
		//##Token:PdfViewerTooltip.print=Print using JavaAPI
		print.setToolTipText(getMessage("PdfViewerTooltip.print"));
		print.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(selectedFile!=null){
					    if(printingThreads==0)
					        printDialog();
						else
							//##Token:PdfViewerPrintFinish.message=Please wait for print to finish
						    JOptionPane.showMessageDialog(c,getMessage("PdfViewerPrintFinish.message"));
					}else
						//##Token:PdfViewerNoFile.message=No file to print
						JOptionPane.showMessageDialog(c,getMessage("PdfViewerNoFile.message")); 
				}
		});
	}


	/**
	 *setup menu pageinfo option
	 */
	private void pageInfoOption(JMenu file) {
		/**show page size info*/
		//##Token:PdfViewerMenu.pageSize=PageSize
		JMenuItem psize = new JMenuItem(getMessage("PdfViewerMenu.pageSize")); 
		//##Token:PdfViewerTooltip.pageSize=Internal page size of current page
		psize.setToolTipText(getMessage("PdfViewerTooltip.pageSize")); 
		psize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				PdfPageData currentPageSize=decode_pdf.getPdfPageData();
				
				/**get the Pdf file information object to extract info from*/
				if(currentPageSize!=null){
					
					JPanel details=new JPanel();
					//<start-13>
					details.setLayout(new BoxLayout(details, BoxLayout.PAGE_AXIS));
					//<end-13>
					
					//general details
					//##Token:PdfViewerCoords.text=Page Co-ordinates
					JLabel header1=new JLabel(getMessage("PdfViewerCoords.text"));
					header1.setFont(headFont);
					details.add(header1);
					
					details.add(Box.createRigidArea(new Dimension(0,5)));
					
					//##Token:PdfViewermediaBox.text=Media Box: 
					JLabel g1=new JLabel(getMessage("PdfViewermediaBox.text")+currentPageSize.getMediaValue(currentPage)); 
					g1.setFont(textFont);
					details.add(g1);
					details.add(Box.createRigidArea(new Dimension(0,5)));
					
					//##Token:PdfViewercropBox.text=Crop Box : 
					JLabel g2=new JLabel(getMessage("PdfViewercropBox.text")+currentPageSize.getCropValue(currentPage)); 
					g2.setFont(textFont);
					details.add(g2);
					details.add(Box.createRigidArea(new Dimension(0,5)));
					
					//##Token:PdfViewerrotation.text=Rotation :\ 
					JLabel g3=new JLabel(getMessage("PdfViewerrotation.text")+currentPageSize.getRotation(currentPage)); 
					g3.setFont(textFont);
					details.add(g3);
					
					details.add(Box.createRigidArea(new Dimension(0,5)));
					
					//##Token:PdfViewersize.text=Current Page Size
					JOptionPane.showMessageDialog(c,details,getMessage("PdfViewersize.text"),JOptionPane.PLAIN_MESSAGE); 
				}else
					//##Token:PdfVieweremptyFile.message=No file data available
					//##Token:PdfViewerTooltip.pageSize=Internal page size of current page
					JOptionPane.showMessageDialog(c,getMessage("PdfVieweremptyFile.message"),getMessage("PdfViewerTooltip.pageSize"),JOptionPane.PLAIN_MESSAGE);
				
			}
		});
		file.add(psize);
	}


	/**
	 * setup doc info menu button
	 */
	private void docInfoOption(JMenu file) {
		/**show Document info*/
		//##Token:PdfViewerProperties=Properties
		JMenuItem props = new JMenuItem(getMessage("PdfViewerProperties")); 
		//##Token:PdfViewerMenu.props=Document Properties
		props.setToolTipText(getMessage("PdfViewerMenu.props"));
		props.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			    showPropertiesBox();
				
			}
		});
		file.add(props);
	}


	/**
	 * setup font menu item
	 */
	private void fontsOption(JMenu file) {
		/**show fonts used option*/
		//##Token:PdfViewerMenu.fonts=Fonts
		JMenuItem fonts = new JMenuItem(getMessage("PdfViewerMenu.fonts")); 
		//##Token:PdfViewerTooltip.fonts=List of fonts used on page
		fonts.setToolTipText(getMessage("PdfViewerTooltip.fonts"));
		fonts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    showFontsBox();
			}
		});
		file.add(fonts);
	}


	/**
	 * setup menu help items and add to menu
	 */
	private void infoOption(JMenu help) {
		/**show info option*/
		//##Token:PdfViewerMenu.about=About
		JMenuItem info = new JMenuItem(getMessage("PdfViewerMenu.about"));
		//##Token:PdfViewerTooltip.about=Message about library
		info.setToolTipText(getMessage("PdfViewerTooltip.about"));
		info.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    	showInfoBox();
			}
		});
		help.add(info);
	}


	/**
	 * setup menu file open option
	 */
	private void openOption(JMenu file) {
		/**open file option*/
		//##Token:PdfViewerOpen.text=Open
		JMenuItem Open = new JMenuItem(getMessage("PdfViewerOpen.text")); 
		//##Token:PdfViewerTooltip.open=Open a file
		Open.setToolTipText(getMessage("PdfViewerTooltip.open")); 
		Open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				/**if you require the ability to open files while printing you should 
				 * create a new PdfDecoder instance each time 
				 */
			    if(printingThreads>0)
			    	//##Token:PdfViewerPrintWait.message=Please wait for printing to complete
					JOptionPane.showMessageDialog(c,getMessage("PdfViewerPrintWait.message"));
				else if(isProcessing)
					//##Token:PdfViewerDecodeWait.message=Please wait for page to display
					JOptionPane.showMessageDialog(c,getMessage("PdfViewerDecodeWait.message"));
				else
				    selectFile();
				  		
			}
			
		});
		file.add(Open);
	}


	/**
	 * setup menu save option
	 */
	private void saveOption(JMenu file) {
		/**save file option*/
		//##Token:PdfViewerSave.text=Save
		JMenuItem save = new JMenuItem(getMessage("PdfViewerSave.text")); 
		//##Token:PdfViewerTooltip.save=Save a file
		save.setToolTipText(getMessage("PdfViewerTooltip.save")); 
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				
				/**
				 * create the file chooser to select the file
				 */
				JFileChooser chooser = new JFileChooser(inputDir);
				chooser.setSelectedFile(new File(inputDir+"/"+selectedFile));
				chooser.addChoosableFileFilter(new FileFilterer(new String[]{"pdf"}, "Pdf (*.pdf)")); //$NON-NLS-1$
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				//set default name to current file name 
				int approved=chooser.showSaveDialog(null);
				if(approved==JFileChooser.APPROVE_OPTION){
					File file = chooser.getSelectedFile();
					//chooser.get
					FileInputStream fis=null;
					FileOutputStream fos=null;
					
					try {
						fis=new FileInputStream(selectedFile);
						fos=new FileOutputStream(file.toString()+".pdf");
						
						byte[] buffer=new byte[4096];
						int bytes_read;
						
						while((bytes_read=fis.read(buffer))!=-1)
							fos.write(buffer,0,bytes_read);
					} catch (Exception e1) {
						
						e1.printStackTrace();
					}
					
					try{
						fis.close();
						fos.close();
					} catch (Exception e2) {
						
						e2.printStackTrace();
					}
					
				}
			}
		});
		file.add(save);
	}


	//<start-example>
	/**
     * show document properties
     */
    protected void showPropertiesBox() {
       
        PdfFileInformation currentFileInformation=decode_pdf.getFileInformationData();
		
		/**get the Pdf file information object to extract info from*/
		if(currentFileInformation!=null){
			
			JPanel details=new JPanel();
			//details.setPreferredSize(new Dimension(400,300));
			details.setOpaque(false);
			//<start-13>
			details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
			//<end-13>
			
			//general details
			//##Token:PdfViewerGeneral=General
			JLabel header1=new JLabel(getMessage("PdfViewerGeneral")); 
			header1.setFont(headFont);
			header1.setOpaque(false);
			details.add(header1);
			header1.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			//##Token:PdfViewerFileName=File name : 
			JLabel g1=new JLabel(getMessage("PdfViewerFileName")+selectedFile);
			g1.setFont(textFont);
			g1.setOpaque(false);
			details.add(g1);
			
			//##Token:PdfViewerFilePath=File path : 
			JLabel g2=new JLabel(getMessage("PdfViewerFilePath")+inputDir); 
			g2.setFont(textFont);
			g2.setOpaque(false);
			details.add(g2);
			
			//##Token:PdfViewerFileSize=File size : 
			JLabel g3=new JLabel(getMessage("PdfViewerFileSize")+size+" K");  //$NON-NLS-2$
			g3.setFont(textFont);
			g2.setOpaque(false);
			details.add(g3);
			
			//##Token:PdfViewerPageCount=Page Count : 
			JLabel g4=new JLabel(getMessage("PdfViewerPageCount")+pageCount); 
			g4.setOpaque(false);
			g4.setFont(textFont);
			details.add(g4);
			
			details.add(Box.createRigidArea(new Dimension(0,15)));
			
			//general details
			//##Token:PdfViewerProperties=Properties
			JLabel header2=new JLabel(getMessage("PdfViewerProperties"));
			header2.setFont(headFont);
			header2.setOpaque(false);
			details.add(header2);
			header2.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			//get the document properties
			String[] values=currentFileInformation.getFieldValues();
			String[] fields=currentFileInformation.getFieldNames();
			
			//add to list and display
			int count=fields.length;
			
			JLabel[] displayValues=new JLabel[count];
			
			for(int i=0;i<count;i++){
				if(values[i].length()>0){
					
					displayValues[i]=new JLabel(fields[i]+" = "+values[i]); //$NON-NLS-1$
					displayValues[i].setFont(textFont);
					displayValues[i].setOpaque(false);
					details.add(displayValues[i]);
				}
			}
			
			details.add(Box.createRigidArea(new Dimension(0,5)));
			
			String xmlText=currentFileInformation.getFileXMLMetaData();
			if(xmlText.length()>0){
				
				JLabel header3=new JLabel("XML metadata"); //$NON-NLS-1$
				header3.setFont(headFont);
				details.add(header3);
				
				
				JTextArea xml=new JTextArea();
				xml.setRows(5);
				xml.setColumns(15);
				xml.setLineWrap(true);
				xml.setText(xmlText);
				details.add(new JScrollPane(xml));
				xml.setCaretPosition(0);
				xml.setOpaque(false);
				
				details.add(Box.createRigidArea(new Dimension(0,5)));
			}
			
			details.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
			
			JOptionPane.showMessageDialog(
					//##Token:PdfViewerMenu.props=Document Properties
					this,details,getMessage("PdfViewerMenu.props"),JOptionPane.PLAIN_MESSAGE); //$NON-NLS-1$
		}else
			JOptionPane.showMessageDialog(
					//##Token:PdfVieweremptyFile.message=No file data available
					//##Token:PdfViewerMenu.props=Document Properties
					this,getMessage("PdfVieweremptyFile.message"),getMessage("PdfViewerMenu.props"),JOptionPane.PLAIN_MESSAGE);
		
        
    }

    /**
	 * show fonts displayed
	 */
	private void showFontsBox(){
	    
		JPanel details=new JPanel();
		details.setPreferredSize(new Dimension(400,300));
		details.setOpaque(false);
		details.setEnabled(false);
		//<start-13>
		details.setLayout(new BoxLayout(details, BoxLayout.PAGE_AXIS));
		//<end-13>
		
		//general details
		//##Token:PdfViewerFontList.title=List of Fonts
		JLabel header1=new JLabel(getMessage("PdfViewerFontList.title")); 
		header1.setFont(headFont);
		header1.setOpaque(false);
		details.add(header1);
		header1.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		details.add(Box.createRigidArea(new Dimension(0,5)));
		
		String xmlText=decode_pdf.getFontsInFile();
		if(xmlText.length()>0){
			
			JTextArea xml=new JTextArea();
			//xml.setRows(5);
			//xml.setColumns(15);
			xml.setLineWrap(false);
			xml.setText(xmlText);
			details.add(xml);
			xml.setCaretPosition(0);
			xml.setOpaque(false);
			
			details.add(Box.createRigidArea(new Dimension(0,5)));
		}
		
		///details.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		JOptionPane.showMessageDialog(
				//##Token:PdfViewerFontList.subtitle=List of Fonts used on Page
				this,details,getMessage("PdfViewerFontList.subtitle"),JOptionPane.PLAIN_MESSAGE); 
	}
	
	
	/**
     * display a box giving user info about program
     */
    protected void showInfoBox() {
        
        JPanel details=new JPanel();
        details.setPreferredSize(new Dimension(400,230));
        details.setOpaque(false);
		//<start-13>
		details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
		//<end-13>
		
		//general details
		//##Token:PdfViewerInfo.title=Simple Viewer Information 
		JLabel header1=new JLabel(getMessage("PdfViewerInfo.title")); 
		header1.setOpaque(false);
		header1.setFont(headFont);
		header1.setAlignmentX(Component.CENTER_ALIGNMENT);
		details.add(header1);
		
		details.add(Box.createRigidArea(new Dimension(0,15)));
		
		//##Token:PdfViewerInfo1=This application demonstrates the use of the JPedal library as a GUI component.
		//##Token:PdfViewerInfo2=Full source code, more examples, forums and advice are available from the website
		String xmlText=getMessage("PdfViewerInfo1")+getMessage("PdfViewerInfo2");
		if(xmlText.length()>0){
			
			JTextArea xml=new JTextArea();
			xml.setOpaque(false);
			xml.setText(xmlText);
			//xml.setFont(textFont);
			//xml.setColumns(30);
			xml.setLineWrap(true);
			xml.setWrapStyleWord(true);
			//xml.setPreferredSize(new Dimension(250,200));
			details.add(xml);
			xml.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		}
		
		ImageIcon logo=new ImageIcon(ClassLoader.getSystemResource("org/jpedal/examples/simpleviewer/logo.gif")); //$NON-NLS-1$
		details.add(Box.createRigidArea(new Dimension(0,25)));
		JLabel idr=new JLabel(logo);
		idr.setAlignmentX(Component.CENTER_ALIGNMENT);
		details.add(idr);
		
		JLabel url=new JLabel("http://www.jpedal.org"); //$NON-NLS-1$
		url.setForeground(Color.blue);
		url.setAlignmentX(Component.CENTER_ALIGNMENT);
		details.add(url);
		details.add(Box.createRigidArea(new Dimension(0,5)));
		
		details.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		JOptionPane.showMessageDialog(
				//##Token:PdfViewerInfo3=Simple demo of JPedal as a GUI component
				this,details,getMessage("PdfViewerInfo3"),JOptionPane.PLAIN_MESSAGE); 
			
    }
    
    //<end-example>
    
    /**
	 * handle printing functionality
	 */
	protected void printDialog() {
	    
	    /**the Java tutorial from Sun has some nice examples of
	     * setting up printing under Java and some performance tips
	     */
	    
	    //provides atomic flag on printing so we don't exit until all done
	    printingThreads++;
	    
	    //##Token:PdfViewerPrint.text=Print
	    //System.out.println(getMessage("PdfViewerPrint.text")+printingThreads); 
	    /**
	     * printing in thread to improve background printing -
	     * comment out if not required
	     */
	    Thread worker = new Thread() {
	        public void run() {
	            
	            Timer t=null;
	            //<start-example>
	            boolean canceled=false;
	            //<end-example>
	            
	            try {
	            	
		            	//##Token:PdfViewera4Border=A4 (Border)
		            	String printDescription=getMessage("PdfViewera4Border"); 
		                
		            	//default for printing is to print to 100% unless we need
		            	//to scale it smaller to fit it to the page.
	            		decode_pdf.enableScaledPrinting(false);
	                
	                //setup print job and objects
	                PrinterJob printJob = PrinterJob.getPrinterJob();
	                PageFormat pf = printJob.defaultPage();
	                
	                // Set PageOrientation to best use page layout
	                int orientation = decode_pdf.getPDFWidth() < decode_pdf
	                .getPDFHeight() ? PageFormat.PORTRAIT
	                        : PageFormat.LANDSCAPE;
	                pf.setOrientation(orientation);
	                
	                Paper paper = new Paper();
	                //<start-example>
	                paper.setSize(595, 842);
	                paper.setImageableArea(43, 43, 509, 756);
	                //<end-example>
	                
	                /**
	                 * Create default page format A4 - you may wish to
	                 * change this for your printer
	                 */
	                
	                /**
	                 * provide list of groupings available and brief
	                 * description (keep help as first)
	                 */
	                //<start-example>
	                boolean selectPageSize = true;
	                while (selectPageSize) {
	                    
	                		//##Token:PdfViewerHelp.text=Help
	                		//##Token:PdfViewera4=A4
	                    String[] options = { getMessage("PdfViewerHelp.text"), getMessage("PdfViewera4"), 
	                    		//##Token:PdfViewera4borderless=A4 (borderless)
	                    		//##Token:PdfViewera5=A5
	                            getMessage("PdfViewera4borderless"),getMessage("PdfViewera5"),
								//##Token:PdfVieweruseScaleOnPrinting.text=Use Scale On Printing
								//##TOKEN:PdfViewerCancel.text=Cancel
	                            getMessage("PdfVieweruseScaleOnPrinting.text"),getMessage("PdfViewerCancel.text"),
								//##Token:PdfViewerPrint.text=Print								
								getMessage("PdfViewerPrint.text") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	                    
	                    /** tell users about printing */
	                    JTextArea message = new JTextArea(
	                    		//##Token:PdfViewerPrintInfo1=Print Details
	                            getMessage("PdfViewerPrintInfo1") 
	                            +"\n=============\n"+
								//##Token:PdfViewerPrintInfo2=Paper size=
	                            getMessage("PdfViewerPrintInfo2")+" "+
								//##Token:PdfViewerPrintInfo3=width=
	                            printDescription+"\n"
								+getMessage("PdfViewerPrintInfo3")
	                            + paper.getWidth()+"\n"+
								//##Token:PdfViewerPrintInfo4=height=
	                            getMessage("PdfViewerPrintInfo4")+ paper.getHeight()
								//##Token:PdfViewerPrintInfo5=Margins=
	                            +"\n"+ getMessage("PdfViewerPrintInfo5")+ paper.getImageableX()
	                            + " " //$NON-NLS-1$
	                            + paper.getImageableY()+ " " //$NON-NLS-1$
	                            + paper.getImageableWidth()+ " " //$NON-NLS-1$
	                            + paper.getImageableHeight()
								//##Token:PdfViewerPrintInfo6=Printing will use Page rotation as set in PDF file.
	                            +"\n"+getMessage("PdfViewerPrintInfo6") +"\n\n"
								//##Token:PdfViewerPrintInfo7=Useful tips
	                            + getMessage("PdfViewerPrintInfo7")
	                            + "\n===========\n" //$NON-NLS-1$
								//##Token:PdfViewerPrintInfo8=Your printer may use different values
	                            + getMessage("PdfViewerPrintInfo8")+"\n"+
								//##Token:PdfViewerPrintInfo9=Pages will be scaled if larger than page to fit
								//##Token:PdfViewerPrintInfo10=PLEASE look at SimpleViewer sample code for example code for printing including servderside and without a print dialog    
	                            getMessage("PdfViewerPrintInfo9")+"\n"+ getMessage("PdfViewerPrintInfo10")
								//##Token:PdfViewerPrintScalingValue=Scale to be used for Printing=
								+"\n\n"+getMessage("PdfViewerPrintScalingValue")+decode_pdf.getScaleForPrinting()); 
	                    message.setColumns(30);
	                    message.setWrapStyleWord(true);
	                    
	                    /**
	                     * bringup display and process user requests
	                     */
	                    int n = JOptionPane.showOptionDialog(c,
	                    		//##Token:PdfViewerPrintOptions.title=Printing Options
	                            message, getMessage("PdfViewerPrintOptions.title"),
	                            JOptionPane.OK_OPTION,JOptionPane.INFORMATION_MESSAGE, null,
	                            options, options[0]);
	                    
	                    //make choice
	                    switch (n) {
	                    case 0: //help
	                    	//##Token:PdfViewerHelp.message=This allows you to user to select a Page size.\n\nWe have provided 3 examples and others can easily be added into SimpleViewer.java\n
	                        JTextArea info = new JTextArea(getMessage("PdfViewerHelp.message")); 
	                        
	                        JOptionPane.showMessageDialog(c,info);
	                        break;
	                        
	                    case 1: //A4 (border)
	                    	//##Token:PdfViewera4=A4
	                    	printDescription=getMessage("PdfViewera4"); 
	                        paper.setSize(595, 842);
	                        paper.setImageableArea(43, 43, 509, 756);
	                        break;
	                        
	                    case 2: //A4 (borderless)
	                    	//##Token:PdfViewera4borderless=A4 (borderless)
	                    	printDescription=getMessage("PdfViewera4borderless"); 
	                        paper.setSize(595, 842);
	                        paper.setImageableArea(0, 0, 595, 842);
	                        //##Token:PdfViewerPrintWarning=Warning - this setting may be over-ridden by your printer if unsupported
	                        JOptionPane.showMessageDialog(c,getMessage("PdfViewerPrintWarning"));
	                        break;
	                        
	                    case 3: //A5
	                    	//##Token:PdfViewera5=A5
	                    	printDescription=getMessage("PdfViewera5");
	                        paper.setSize(297, 421);
	                        paper.setImageableArea(23,23,254,378);
	                        break;
	                        
	                    case 4://use scale on printing
	                        if(decode_pdf.usePageScaling)
	                        	decode_pdf.enableScaledPrinting(false);
	                        else
	                        	decode_pdf.enableScaledPrinting(true);
	                        break;
	                        
	                    case 5: //cancel
	                    	canceled=true;
	                        selectPageSize=false;
	                        break;
	                        
	                    case 6: //done
	                        selectPageSize=false;
	                        break;
	                            
	                    default: //just in case user edits code and to
	                        // handle cancel
	                        System.out.println("No selection"); //$NON-NLS-1$
	                    break;
	                    
	                    }
	                }
	                
	                if(!canceled){
	                //<end-example>
		                
		                pf.setPaper(paper);
		                
		                //<start-example>
		                //very useful for debugging! (shows the imageable
		                // area as a green box bordered by a rectangle)
		                //decode_pdf.showImageableArea();
		                
		                /**
		                 * SERVERSIDE printing IF you wish to print using a
		                 * server, do the following
		                 * 
		                 * 1. Select page size manually and delete the loop
		                 * ==================
		                 * 
		                 * if(printJob.printDialog()){ }
		                 * 
		                 * 2. Use this alternative code (1.4 only)
		                 * ============================
		                 *  
		                 * decode_pdf.setPagePrintRange(startPage,endPage);
		                 * for(int page=startPage;page <endPage+1;page++)
		                 * decode_pdf.setPageFormat(page,pf); //change to
		                 * suit - sets format for each page
		                 * printJob.setPageable(decode_pdf);
		                 * printJob.print();
		                 *  
		                 */
		                //<end-example>
		                
		                //allow user to edit settings and select printing
		                printJob.setPrintable(decode_pdf, pf);
		                
		                boolean printFile=printJob.printDialog();
		                if (printFile) {
		                    
		                    //<start-example>
		                    /**<end-example>
		                    //tell users about scaling
		                    JOptionPane.showMessageDialog(
		                      c,
		                      //##Token:PdfViewerPrintScaling.message=Pages will be scaled to fit if larger than pane
		                      getMessage("PdfViewerPrintScaling.message"));
		                    /**/
		                    
		                    // Print PDF document
		                    printJob.print();
		                    
		                }
		            //<start-example>
	                }
	                //<end-example>
	            } catch (PrinterException ee) {
	                LogWriter.writeLog("Exception " + ee + " printing"); //$NON-NLS-1$ //$NON-NLS-2$
	                //<start-13>
	                JOptionPane.showMessageDialog(c,ee.getMessage()+" "+ee+" "+" "+ee.getCause());
	                //<end-13>
	            } catch (Exception e) {
	                LogWriter.writeLog("Exception " + e + " printing"); //$NON-NLS-1$ //$NON-NLS-2$
	                e.printStackTrace();
	                JOptionPane.showMessageDialog(c,"Exception "+e);
	            } catch (Error err) {
	                LogWriter.writeLog("Error " + err + " printing"); //$NON-NLS-1$ //$NON-NLS-2$
	                JOptionPane.showMessageDialog(c,"Error "+err);
	            }
	            
	            /**report any or our errors 
	             * (we do it this way rather than via PrinterException as MAC OS X has a nasty bug in PrinterException)
	             */
	            if(!canceled && !decode_pdf.isPageSuccessful())
	            	JOptionPane.showMessageDialog(c,"Problems encountered\n "+decode_pdf.getPageFailureMessage()+"\n");
	            	            
	            printingThreads--;
	            
	            //redraw to clean up
	            decode_pdf.invalidate();
	            decode_pdf.updateUI();
	            repaint();
	           
	            if(!canceled)//##Token:PdfViewerPrintingFinished=finished printing
	            JOptionPane.showMessageDialog(c,getMessage("PdfViewerPrintingFinished"));
	        }
	    };
	    
	    //start printing in background (comment out if not required)
	    worker.start();
	    
    }

	//<start-example>
	/** main method to run the software */
	public static void main(String[] args) {
		SimpleViewer current = new SimpleViewer();
		
		/** Run the software */
		if (args.length > 0)
			current.setupViewer(args[0]);
		else 
			current.setupViewer();
		
		
	}
    //<end-example>
    
	/** opens a pdf file and calls the display/decode routines */
	public void selectFile() {
		
		/**
		 * create the file chooser to select the file
		 */
		final JFileChooser chooser = new JFileChooser(inputDir);
		if(selectedFile!=null)
		    chooser.setSelectedFile(new File(selectedFile));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		String[] pdf = new String[] { "pdf" }; //$NON-NLS-1$
		chooser.addChoosableFileFilter(new FileFilterer(pdf, "Pdf (*.pdf)")); //$NON-NLS-1$

		final int state = chooser.showOpenDialog(c);

		final File file = chooser.getSelectedFile();

		/**
		 * decode
		 */
		if (file != null && state == JFileChooser.APPROVE_OPTION) {
			try {
				selectedFile = file.getCanonicalPath();

				size = (file.length() >> 10);

				/** save path so we reopen her for later selections */
				inputDir = chooser.getCurrentDirectory().getCanonicalPath();


			    //<start-example>
				setViewerTitle(null);
				//<end-example>
			} catch (Exception e) {
				System.err.println("Exception " + e + " getting paths"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			/** check file exists */
			File testFile = new File(selectedFile);
			if (!testFile.exists()) {
				//##Token:PdfViewerFile.text=File
				JOptionPane.showMessageDialog(this, getMessage("PdfViewerFile.text") + selectedFile 
						//##Token:PdfViewerNotExist=\ does not exist
						+ getMessage("PdfViewerNotExist"));
				/**
				 * open the file
				 */
			} else if ((selectedFile != null) & (isProcessing == false)) {
				
				/**
				 * trash previous display now we are sure it is not needed
				 */
				decode_pdf.repaint();
				if((isProcessing)|(worker!=null))
					worker.interrupt();
				decode_pdf.flushObjectValues(true);
				
				//reset the viewableArea before opening a new file
				decode_pdf.resetViewableArea();
			    
				//<start-example>
			    //<start-13>
				thumbnails.removeComponentListener(painter);
				//<end-13>
				//<end-example>
				
				openFile(selectedFile);

			}

		} else { //no file selected so redisplay old
			decode_pdf.repaint();
			//##Token:PdfViewerNoSelection=Nothing selected
			JOptionPane.showMessageDialog(this, getMessage("PdfViewerNoSelection")); 

		}
	}

	//<start-example>
	/**
	 * set title or over-ride with message
	 */
	public void setViewerTitle(final String title) {
			
        	if(title!=null){
    			setTitle(title);
    		}else{
    				//##Token:PdfViewerOs.message=JPedal GUI Open Source version - version
				 setViewerTitle(getMessage("PdfViewerOs.message")+"  " + PdfDecoder.version+" "+selectedFile);
				 /**/
    		}	
	}
	//<end-example>

	/**
	   * Launch the PdfViewer to a specific pdf file
	   * Opens a pdf file and calls the display/decode routines
	 *  
	   * @param file File the PDF to be decoded
	   * @param String bookmark - if not present, exception will be thrown
	 */
	  public void openFile(File file, String bookmark) {
        
	  	maxViewY=0;// rensure reset for any viewport
	  	
	    try{
	    	
	        boolean fileCanBeOpened=openUpFile(file.getCanonicalPath());
	        
	        //reads tree and populates lookup table
	        Node rootNode= decode_pdf.getOutlineAsXML().getFirstChild();
	        DefaultMutableTreeNode top =new DefaultMutableTreeNode("Root"); //$NON-NLS-1$
	        if(rootNode!=null)
	            readChildNodes(rootNode,top);
	        
	        //open page if bookmark found
	        Object bookmarkPage=pageLookupTable.get(bookmark);
	        if(bookmarkPage==null)
	        	throw new PdfException("Unknown bookmark "+bookmark);
	        	
	        int page=Integer.parseInt((String)bookmarkPage);
  			currentPage=page;
		    if(fileCanBeOpened)
				processPage();
		    
		}catch(Exception e){
		    System.err.println("Exception " + e + " processing file"); //$NON-NLS-1$ //$NON-NLS-2$
		 
			
			//<end-example>
			isProcessing = false;
		}
	  }
	  
    /**
	   * Launch the PdfViewer to a specific pdf file
	   * Opens a pdf file and calls the display/decode routines
	 *  
	   * @param file File the PDF to be decoded
	   * @param page int page number to show the user
	 */
	  public void openFile(File file, int page) {
	    
	  	maxViewY=0;// rensure reset for any viewport
	  	
	    try{
	        boolean fileCanBeOpened=openUpFile(file.getCanonicalPath());
	        
    			currentPage=page;
    			
		    if(fileCanBeOpened)
				processPage();
		}catch(Exception e){
		    System.err.println("Exception " + e + " processing file"); //$NON-NLS-1$ //$NON-NLS-2$
		 
			
			//<end-example>
			isProcessing = false;
		}
	  }
	  
	/**
	 *  checks file can be opened (permission) 
	 */
	  private void openFile(String selectedFile) {
	  	
	  	maxViewY=0;// rensure reset for any viewport
	  	
	  	boolean fileCanBeOpened=openUpFile(selectedFile);
	  	currentPage = 1;
	  	
	  	try{
	  		if(fileCanBeOpened)
	  			processPage();
	  	}catch(Exception e){
	  		System.err.println("Exception " + e + " decoding file"); //$NON-NLS-1$ //$NON-NLS-2$
	  		e.printStackTrace();
	  		
	  		//<end-example>
	  		isProcessing = false;
	  	}
	  }

    /**
	 *  initial method called to open a new PDF
	 */
	private boolean openUpFile(String selectedFile) {

		maxViewY=0;// rensure reset for any viewport
		
	    boolean fileCanBeOpened = true;

		/** reset default values */
		scaling = (float) 1.0;
		scalingBox.setSelectedIndex(defaultSelection); 

	    decode_pdf.closePdfFile();
		
		/** ensure all data flushed from PdfDecoder before we decode the file */
		//decode_pdf.flushObjectValues(true);

		try {

			/** opens the pdf and reads metadata */
			decode_pdf.openPdfFile(selectedFile);

			statusBar.resetStatus("opening file"); //$NON-NLS-1$
			
			/** flag up if encryption present */

			/** popup window if needed */
			if ((decode_pdf.isEncrypted()) && (!decode_pdf.isFileViewable())) {
				fileCanBeOpened = false;

				//<start-13>
				/**
				 * //<end-13>JOptionPane.showMessageDialog(this,"Please
				 * use Java 1.4 to display encrypted files"); //<start-13>
				 */

				String password = JOptionPane
					//##Token:PdfViewerPassword.message=Please enter password
						.showInputDialog(this,getMessage("PdfViewerPassword.message")); //$NON-NLS-1$

				/** try and reopen with new password */
				if (password != null) {
					decode_pdf.setEncryptionPassword(password);
					decode_pdf.verifyAccess();

					if (decode_pdf.isFileViewable())
						fileCanBeOpened = true;

				}
				//mainFrame.repaint();
				//<end-13>
			}

			removeOutlinePanels();
			
			
			if (!fileCanBeOpened) {
				//<start-13>
				JOptionPane
						.showMessageDialog(this,
								//##Token:PdfViewerPasswordRequired.message=A valid password is required to display encrypted files
						        getMessage("PdfViewerPasswordRequired.message")); //$NON-NLS-1$
				//<end-13>

			} else {

				//update count
				currentFileCount++;

				/** reset values */
				currentPage = 1;
			}

		} catch (Exception e) {
			System.err.println("Exception " + e + " opening file"); //$NON-NLS-1$ //$NON-NLS-2$
			
			//<start-example>
			JOptionPane
					.showMessageDialog(
							this,
							//##Token:PdfViewerOpenerror=This file generated an exception and cannot continue. Please send file to IDRsolutions for analysis
							getMessage("PdfViewerOpenerror")); //$NON-NLS-1$
			System.exit(1);
			//<end-example>
			isProcessing = false;
		}
		
		return fileCanBeOpened;
		
	}

    /**
     * decode and display selected page
     */
	private void processPage() {
		
		if(switchModes){
			/**
			 * get PRODUCER and if OCR disable text printing
			 */
			PdfFileInformation currentFileInformation=decode_pdf.getFileInformationData();
			
			/**switch all on by default*/
			decode_pdf.setRenderMode(PdfDecoder.RENDERIMAGES+PdfDecoder.RENDERTEXT);
			
			String[] values=currentFileInformation.getFieldValues();
			String[] fields=currentFileInformation.getFieldNames();
			
			for(int i=0;i<fields.length;i++){
				
				if((fields[i].equals("Creator"))|(fields[i].equals("Producer"))){
					
					for(int j=0;j<ocr.length;j++){
						
						if(values[i].equals(ocr[j])){
							
							decode_pdf.setRenderMode(PdfDecoder.RENDERIMAGES);
							
							/**
							 * if we want to use java 13 JPEG conversion
							 */
							decode_pdf.setEnableLegacyJPEGConversion(true);
							
						}
					}
				}
				
				/**
				 * if pages contain just hires images, display at highest res
				 */
				if(fields[i].equals("Creator")){
					
					if(values[i].equals("XPP")){
						
						/**
						 * this will create a hirer quality screen display without down-sampling images.
						 * It is slower and uses more memory. It is only recommended for pages where the page
						 * is actually a graphic. It only needs to be set ONCE before the first page is drawn.
						 */
						decode_pdf.useHiResScreenDisplay(true);
						
					}else
						decode_pdf.useHiResScreenDisplay(false);
					
				}
			}
		}
    	
		pageCount = decode_pdf.getPageCount();
		
		if(this.pageCount<currentPage){
			currentPage=pageCount;
			System.err.println(currentPage+ " out of range. Opening on last page");
			LogWriter.writeLog(currentPage+ " out of range. Opening on last page");
		}
				
		
		//values extraction mode,dpi of images, dpi of page as a factor of 72
		decode_pdf.setExtractionMode(0, 72, scaling);
		/***/
		
		//resize (ensure at least certain size)
        //<start-example>
				if (!showThumbnails) {
        //<end-example>
					zoom();
					
				//<start-example>
				}
        //<end-example>
				
				//add a border
				decode_pdf.setPDFBorder(BorderFactory.createLineBorder(
						Color.black, 1));

				/** turn off border in printing */
				decode_pdf.disableBorderForPrinting();

        //<start-example>
				/**
				 * update the display, including any rotation
				 */
				pageCounter2.setForeground(Color.black);
				pageCounter2.setText(" " + currentPage); //$NON-NLS-1$
//##Token:PdfViewerOfLabel.text=of
				pageCounter3.setText(getMessage("PdfViewerOfLabel.text") + pageCount);
        //<end-example>

				resetRotationBox();


				decodePage(true);
				
	}
	
	/**
	 * align rotation combo box to default for page
	 */
	private void resetRotationBox() {
		PdfPageData currentPageData=decode_pdf.getPdfPageData();
		rotation=currentPageData.getRotation(currentPage);
		if(rotationBox.getSelectedIndex()!=(rotation/90)){
			rotationBox.setSelectedIndex(rotation/90);
		}else{
			decode_pdf.repaint();
		}
	}

	/** 
	 * zoom into page 
	 */
	public void zoom() {

		if(decode_pdf!=null){
		    
			/** update value and GUI */
		    int index=scalingBox.getSelectedIndex();
		    if(index==-1){
		    	String numberValue=(String)scalingBox.getSelectedItem();
		    	
		    	float zoom=-1;
		    	if((numberValue!=null)&&(numberValue.length()>0)){
		    		try{
		    			zoom= Float.parseFloat(numberValue);
		    		}catch(Exception e){
		    			zoom=-1;
		    			//its got characters in it so get first valid number string
		    			int length=numberValue.length();
		    			int ii=0;
		    			while(ii<length){
		    				char c=numberValue.charAt(ii);
		    				if(((c>='0')&&(c<='9'))|(c=='.'))
		    					ii++;
		    				else
		    					break;
		    			}
		    			
		    			if(ii>0)
		    				numberValue=numberValue.substring(0,ii);
		    			
		    			//try again if we reset above
		    			if(zoom==-1){
		    				try{
		    					zoom= Float.parseFloat(numberValue);
		    				}catch(Exception e1){zoom=-1;}
		    			}
		    		}
		    		if(zoom>1000){
		    			zoom=1000;
		    		}
		    	}
		    	
		    	//if nothing off either attempt, use window value
		    	if(zoom==-1){
		    		//its not set so use To window value
		    		index=defaultSelection;
		    		scalingBox.setSelectedItem(scalingBox.getItemAt(index));
		    	}else{
		    		scaling=zoom/100;
		    		scalingBox.setSelectedItem(zoom+"");
		    	}
		    	
		    	//<start-example>
		    }
		    if(index!=-1){
		    	if(index<3){ //handle scroll to width/height/window
		    		
		    		PdfPageData pageData = decode_pdf.getPdfPageData();
		    		int cw,ch,raw_rotation=pageData.getRotation(currentPage);
		    		if(rotation==90 || rotation==270){
		    			cw = pageData.getCropBoxHeight(currentPage);
		    			ch = pageData.getCropBoxWidth(currentPage);
		    		}else{
		    			cw = pageData.getCropBoxWidth(currentPage);
		    			ch = pageData.getCropBoxHeight(currentPage);
		    		}
		    		
		    		//define pdf view width and height
		    		// MAY not need dividersize
		    		float width = scrollPane.getViewport().getWidth()-inset-inset-displayPane.getDividerSize();
		    		float height = scrollPane.getViewport().getHeight()-inset-inset;
		    		
		    		float x_factor=0,y_factor=0;
		    		x_factor = width / cw;
		    		y_factor = height / ch;
		    		
		    		if(index==0){//window
		    			if(x_factor<y_factor)
		    				scaling = x_factor;
		    			else
		    				scaling = y_factor;
		    		}else if(index==1)//height
		    			scaling = y_factor;
		    		else if(index==2)//width
		    			scaling = x_factor;
		    	}else{
		    		scaling=scalingFloatValues[index];
		    	}
		    }
		    /**<end-example>
		    }else
		    	scaling=scalingFloatValues[index];
		    /**/
		    
		    //check for 0 to avoid error  and replace with 1
		    PdfPageData pagedata = decode_pdf.getPdfPageData();
		    if((pagedata.getCropBoxHeight(currentPage)*scaling<100) &&//keep the page bigger than 100 pixels high
		    		(pagedata.getCropBoxWidth(currentPage)*scaling<100)){//keep the page bigger than 100 pixels wide
			    	scaling=1;
			    	scalingBox.setSelectedItem("100");
		    }
		    
		    
		    // THIS section commented out so altering scalingbox does NOT reset rotation
		    //if(!scalingBox.getSelectedIndex()<3){
		    	/**update our components*/
		    	//resetRotationBox();
		    //}
		    
			if(decode_pdf!=null) //allow for clicking on it before page opened
				decode_pdf.setPageParameters(scaling, currentPage,rotation);
			
			decode_pdf.invalidate();
			//decode_pdf.repaint();
			scrollPane.repaint();
			validate();
			
		}
		

	}

	/**move forward one page*/
	public void forward(int count) {
	    
		if (!isProcessing) { //lock to stop multiple accesses

			/**if in range update count and decode next page. Decoded pages are cached so will redisplay
			 * almost instantly*/
			if (currentPage+count <= pageCount) {
				currentPage+=count;
				
				statusBar.resetStatus("Loading Page "+currentPage); //$NON-NLS-1$
				/**reset as rotation may change!*/
				decode_pdf.setPageParameters(scaling, currentPage);
				
				//would reset scaling on page change to default
				//scalingBox.setSelectedIndex(defaultSelection); 
				//pass vales to PdfDecoder
				
				//decode the page
				decodePage(false);
				
				//if scaling to window reset screen to fit rotated page
				if(scalingBox.getSelectedIndex()<3){//<start-example>
					zoom();
				}//<end-example>
				
			}
		}else
			//##Token:PdfViewerDecodeWait.message=Please wait for page to display
			JOptionPane.showMessageDialog(this,getMessage("PdfViewerDecodeWait.message")); //$NON-NLS-1$
	}
	
	/**
	 * called by nav functions to decode next page
	 */
	protected void decodePage(final boolean resizePanel) {

		currentRectangle=null;
		
		//stop user changing scaling while decode in progress
		scalingBox.setEnabled(false);
		rotationBox.setEnabled(false);
		
		decode_pdf.clearScreen();

		/** if running terminate first */
		if ((isProcessing) | (worker != null))
			worker.interrupt();

		isProcessing = true;

		/**
		 * add outline if appropriate in a scrollbar on the left to
		 * replicate L & F or Acrobat
		 */
		if (!hasOutlinesDrawn) {
			hasOutlinesDrawn = true;
			createOutlinePanels();
		}
		
		worker = new SwingWorker() {
			public Object construct() {

			    
				try {
					
					statusBar.updateStatus("Decoding Page",0); //$NON-NLS-1$

					//<start-example>
					/**
					 * make sure screen fits display nicely
					 */
					if ((resizePanel) && (showThumbnails)) {
						zoom();
						
					}
					//<end-example>
					

					if (Thread.interrupted())
						throw new InterruptedException();

					/**
					 * decode the page
					 */
					try {
						decode_pdf.decodePage(currentPage);
						
						//read values for page display
						PdfPageData page_data = decode_pdf.getPdfPageData();
						
						mediaW  = page_data.getMediaBoxWidth(currentPage);
						mediaH = page_data.getMediaBoxHeight(currentPage);
						mediaX = page_data.getMediaBoxX(currentPage);
						mediaY = page_data.getMediaBoxY(currentPage);
						
						cropX = page_data.getCropBoxX(currentPage);
						cropY = page_data.getCropBoxY(currentPage);
						cropW = page_data.getCropBoxWidth(currentPage);
						cropH = page_data.getCropBoxHeight(currentPage);
						
						resetRotationBox();

						//read annotations data
						pageAnnotations = decode_pdf.getPdfAnnotsData();
						
						statusBar.updateStatus("Displaying Page",0); //$NON-NLS-1$

					} catch (Exception e) {
						System.err.println("Exception " + e + " decoding page"); //$NON-NLS-1$ //$NON-NLS-2$
						e.printStackTrace();
					}

					//<start-example>
					pageCounter2.setForeground(Color.black);
					pageCounter2.setText(" " + currentPage); //$NON-NLS-1$
					//##Token:PdfViewerOfLabel.text=of
					pageCounter3.setText(getMessage("PdfViewerOfLabel.text") + pageCount); //$NON-NLS-1$
					//<end-example>


					//tell user about embedded fonts in Open Source version 
					if((decode_pdf.hasEmbeddedFonts())&&(!decode_pdf.supportsEmbeddedFonts())){
						JOptionPane.showMessageDialog(c,"Page contains embedded fonts which may not display correctly using Font substitution."); 
						 
					}
					/**/
	 				//<start-example>
					if ((showThumbnails)) {
						thumbnails.addNewThumbnails(currentPage,decode_pdf);
					} else//<end-example>
						isProcessing = false;

					//<start-example>
					//make sure fully drawn
					//decode_pdf.repaint();

					//<start-13>
					setViewerTitle(null); //restore title
					//<end-13>
					
					if (Thread.interrupted())
						throw new InterruptedException();


					if (showThumbnails) {

						/**setup thumbnails in foreground*/
						thumbnails.setupThumbnailsOnDecode(currentPage,decode_pdf);

						isProcessing = false;
						
						/** draw thumbnails in background */
						thumbnails.createThumbnailsOnDecode(currentPage,decode_pdf);
						
					}
					//<end-example>
				} catch (Exception e) {
					//<start-13>
					//<start-example>
					setViewerTitle(null); //restore title
					//<end-example>
					//<end-13>
				}
				
				selectBookmark();

				statusBar.setProgress(100);

				//reanable user changing scaling 
				scalingBox.setEnabled(true);
				rotationBox.setEnabled(true);
				
				return null;
			}
		};

		worker.start();
		
	}

	/** move back one page */
	public void back(int count) {

		if (!isProcessing) { //lock to stop multiple accesses

			/**
			 * if in range update count and decode next page. Decoded pages are
			 * cached so will redisplay almost instantly
			 */
			if (currentPage-count >= 1) {
				currentPage=currentPage-count;
				
				statusBar.resetStatus("loading page "+currentPage); //$NON-NLS-1$
				
				/** reset as rotation may change! */
				decode_pdf.setPageParameters(scaling, currentPage);
				
				//would reset scaling on page change to default
				//scalingBox.setSelectedIndex(defaultSelection); //set to 100%
				
				//pass vales to PdfDecoder
				decodePage(false);
				
				//if scaling to window reset screen to fit rotated page
				if(scalingBox.getSelectedIndex()<3){//<start-example>
					zoom();
				}//<end-example>
			}
		}else
			//##Token:PdfViewerDecodeWait.message=Please wait for page to display
			JOptionPane.showMessageDialog(this,getMessage("PdfViewerDecodeWait.message")); //$NON-NLS-1$
	}

	/**Clean up and exit program*/
	public void exit() {

	    
		/**
		 * create the dialog
		 */
		JOptionPane.showConfirmDialog(
				//##Token:PdfViewerExiting=Exiting program
			this,new JLabel(getMessage("PdfViewerExiting")),
			//##Token:PdfViewerprogramExit=Program exit
			getMessage("PdfViewerprogramExit"), 
			JOptionPane.DEFAULT_OPTION,
			JOptionPane.PLAIN_MESSAGE);

		/**cleanup*/
		decode_pdf.closePdfFile();

		if(target!=null)
	 		flush();
		
		//@exit
		System.exit(1);
	}
	
	/**
	 * display message from message bundle or name if problem
	 */
	protected String getMessage(String key) {
		
		String message=null;
		
		try{
			message=bundle.getString(key);
		}catch(Exception e){
		}
		
		//trap for 1.3 or missing
		if(message==null){
			
			try{
				//lazy initialisation on messages
				if(messages==null)
					initMessages();
				
				message=(String)messages.get(key);
				
			}catch(Exception e){
			}
		}
		
		//if still null use message key
		if(message==null)
			message=key;
		
		return message;
	}
	
	/**
	 * reads message bundle manually if needed (bug in 1.3.0)
	 */
	private void initMessages() {
		
		String line = null;
		BufferedReader input_stream = null;
		ClassLoader loader = getClass().getClassLoader();
		/**must use windows encoding because files were edited on Windows*/
		String enc = "Cp1252";
		int equalsIndex;
		
		try {
			
			//initialise inverse lookup (add space)
			messages=new HashMap();
			
			input_stream =
				new BufferedReader(
						new InputStreamReader(
								loader.getResourceAsStream(
								"org/jpedal/international/messages.properties"),
								enc));
	
			// trap problems
			if (input_stream == null)
				LogWriter.writeLog("Unable to open messages.properties from jar");
			
			//read in lines and place in map for fast lookup
			while (true) {
				line = input_stream.readLine();
				if (line == null)
					break;
				
				equalsIndex=line.indexOf('=');
				
				if(equalsIndex!=-1)	
					messages.put(line.substring(0,equalsIndex),line.substring(equalsIndex+1));
				
			}
		}catch (Exception e) {
			LogWriter.writeLog(					"Exception " + e + " reading message Bundle");
		}
		
		//ensure closed
		if(input_stream!=null){
			try{
				input_stream.close();
			}catch (Exception e) {
				LogWriter.writeLog(
						"Exception " + e + " reading lookup table for pdf  for abobe map");
			}		
		}
		
	}
	
	/**
	   * Handles the functionality for highlighting the correct bookmark
	   * tree node for the page we opened the PDF to.
	   */
	  protected void selectBookmark() {

	    if(!decode_pdf.hasOutline()) {
	      return;
	    }
	    else {
	        
	        //<start-example>
	        /** code to walk not fully operational so only runs on example
	        //<end-example>
	      traverse();
	      /**/
	    }
	  }


	  /**
	   * Method to traverse all nodes of the Bookmarks JTree.  This is used when
	   * invoking the 'open to page' functionality, because we need to
	   * highlight the bookmark that we are opening to, if there is one.
	   *
	   */
	  private void traverse() {

	    if(tree instanceof JTree) {

	      TreeModel model = tree.getModel();

	      if (model != null)
	        walk(model, model.getRoot());
	      
	      }
	    }

	  /**
	   * Walk through any given node of a JTree.
	   *
	   * @param model TreeModel
	   * @param o Object
	   */
	  private void walk(TreeModel model, Object o){

	    int cc;
	    cc = model.getChildCount(o);
	    DefaultMutableTreeNode node;
	    String title;
	    String page;
	    Object child;

	    for(int i = 0; i < cc; i++) {

	      child = model.getChild(o, i);

	      if(model.isLeaf(child)) {

	        // try to get the page anchor for this node

	        node = (DefaultMutableTreeNode)child;

	        // get title and open page if valid
	        title = (String)node.getUserObject();
	        page = (String)pageLookupTable.get(title);

	        if(page.length()>0){
		        	try {
		        		if(Integer.parseInt(page) == currentPage) {
		        			ignoreAlteredBookmark=true;
		        			tree.setSelectionPath(new TreePath(node.getPath()));
		        			ignoreAlteredBookmark=false;
		        			//System.out.println(tree.getSelectionPath());@MARK
		        		}
		        	}
		        	catch(NumberFormatException nfe) {
		        		System.out.println("bad page number: " + page); //$NON-NLS-1$
		        		ignoreAlteredBookmark=false;
		        	}
	        }
	      }
	      else {
	        //System.out.print(child.toString() + "--"); //$NON-NLS-1$
	        walk(model, child);
	      }
	    }
	  }

	  /**
	   * Walk through any given node of a JTree.
	   */
	  private void createLookupTable(TreeModel model, Object o){

	    int cc;
	    cc = model.getChildCount(o);
	    DefaultMutableTreeNode node;
	    String title;
	    String page;
	    Object child;

	    for(int i = 0; i < cc; i++) {

	      child = model.getChild(o, i);

	      if(model.isLeaf(child)) {

	        // try to get the page anchor for this node

	        node = (DefaultMutableTreeNode)child;

	        // get title and open page if valid
	        title = (String)node.getUserObject();
	        page = (String)pageLookupTable.get(title);

	        try {
	          if(Integer.parseInt(page) == currentPage) {
	            tree.setSelectionPath(new TreePath(node.getPath()));
				}
	        }
	        catch(NumberFormatException nfe) {
	          System.out.println("bad page number: " + page); //$NON-NLS-1$
	        }
	      }
	      else {
	        //System.out.print(child.toString() + "--"); //$NON-NLS-1$
	        walk(model, child);
	      }
	    }
	  }
	  
}

