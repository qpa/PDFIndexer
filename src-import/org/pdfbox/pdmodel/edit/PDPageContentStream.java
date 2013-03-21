/**
 * Copyright (c) 2004, www.pdfbox.org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of pdfbox; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://www.pdfbox.org
 *
 */
package org.pdfbox.pdmodel.edit;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.PDResources;

import org.pdfbox.pdmodel.common.COSStreamArray;
import org.pdfbox.pdmodel.common.PDStream;

import org.pdfbox.pdmodel.font.PDFont;
import org.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

import org.pdfbox.cos.COSArray;
import org.pdfbox.cos.COSName;
import org.pdfbox.cos.COSString;


/**
 * This class will is a convenience for creating page content streams.  You MUST
 * call close() when you are finished with this object.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.11 $
 */
public class PDPageContentStream
{
    private PDPage page;
    private Writer output;
    private boolean inTextMode = false;
    private Map fontMappings = new HashMap();
    private Map imageMappings = new HashMap();
    private int fontNumber = 1;
    private int imageNumber = 1;
    private PDResources resources;
    
    //cached storage component for getting color values
    private float[] colorComponents = new float[4];
    
    private NumberFormat formatDecimal = NumberFormat.getNumberInstance( Locale.US );
    
    private static final String BEGIN_TEXT = "BT\n";
    private static final String END_TEXT = "ET\n";
    private static final String SET_FONT = "Tf\n";
    private static final String MOVE_TEXT_POSITION = "Td\n";
    private static final String SHOW_TEXT = "Tj\n";
    
    private static final String SAVE_GRAPHICS_STATE = "q\n";
    private static final String RESTORE_GRAPHICS_STATE = "Q\n";
    private static final String CONCATENATE_MATRIX = "cm\n";
    private static final String XOBJECT_DO = "Do\n";
    private static final String RG_STROKING = "RG\n";
    private static final String RG_NON_STROKING = "rg\n";
    private static final String K_STROKING = "K\n";
    private static final String K_NON_STROKING = "k\n";
    private static final String G_STROKING = "G\n";
    private static final String G_NON_STROKING = "g\n";
    private static final String APPEND_RECTANGLE = "re\n";
    private static final String FILL = "f\n";
    
    
    private static final int SPACE = 32;
    
    
    /**
     * Create a new PDPage content stream.
     * 
     * @param document The document the page is part of.
     * @param sourcePage The page to write the contents to.
     * @throws IOException If there is an error writing to the page contents.
     */
    public PDPageContentStream( PDDocument document, PDPage sourcePage ) throws IOException
    {
        this(document,sourcePage,false,true);
    }
    
    /**
     * Create a new PDPage content stream.
     * 
     * @param document The document the page is part of.
     * @param sourcePage The page to write the contents to.
     * @param appendContent Indicates whether content will be overwritten. If false all previous content is deleted.
     * @param compress Tell if the content stream should compress the page contents.
     * @throws IOException If there is an error writing to the page contents.
     */
    public PDPageContentStream( PDDocument document, PDPage sourcePage, boolean appendContent, boolean compress ) throws IOException
    {
        page = sourcePage;
        resources = page.getResources();
        if( resources == null )
        {
            resources = new PDResources();
            page.setResources( resources );
        }
        // If request specifies the need to append to the document
        if(appendContent)
        {
            // Get the pdstream from the source page instead of creating a new one
            PDStream contents = sourcePage.getContents();
            
            // Create a pdstream to append new content 
            PDStream contentsToAppend = new PDStream( document );
            
            // This will be the resulting COSStreamArray after existing and new streams are merged
            COSStreamArray compoundStream = null;
            
            // If contents is already an array, a new stream is simply appended to it
            if(contents.getStream() instanceof COSStreamArray)
            {
                compoundStream = (COSStreamArray)contents.getStream();
                compoundStream.appendStream( contentsToAppend.getStream());
            }
            else
            {
                // Creates the COSStreamArray and adds the current stream plus a new one to it 
                COSArray newArray = new COSArray();
                newArray.add(contents.getCOSObject());
                newArray.add(contentsToAppend.getCOSObject());
                compoundStream = new COSStreamArray(newArray);                
            }
            
            if( compress )
            {
	            List filters = new ArrayList();
	            filters.add( COSName.FLATE_DECODE );
	            contentsToAppend.setFilters( filters );
            }
            
            // Sets the compoundStream as page contents 
            sourcePage.setContents( new PDStream(compoundStream) );
            output = new OutputStreamWriter( contentsToAppend.createOutputStream(), "ISO-8859-1" );
        }
        else
        {        
            PDStream contents = new PDStream( document );
            if( compress )
            {
	            List filters = new ArrayList();
	            filters.add( COSName.FLATE_DECODE );
	            contents.setFilters( filters );
            }
            sourcePage.setContents( contents );
            output = new OutputStreamWriter( contents.createOutputStream(), "ISO-8859-1" );
        }
        
        formatDecimal.setMaximumFractionDigits( 10 );
        formatDecimal.setGroupingUsed( false );
    }
    
    /**
     * Begin some text operations.
     * 
     * @throws IOException If there is an error writing to the stream or if you attempt to 
     *         nest beginText calls.
     */
    public void beginText() throws IOException
    {
        if( inTextMode )
        {
            throw new IOException( "Error: Nested beginText() calls are not allowed." );
        }
        output.write( BEGIN_TEXT );
        inTextMode = true;
    }
    
    /**
     * End some text operations.
     * 
     * @throws IOException If there is an error writing to the stream or if you attempt to 
     *         nest endText calls.
     */
    public void endText() throws IOException
    {
        if( !inTextMode )
        {
            throw new IOException( "Error: You must call beginText() before calling endText." );
        }
        output.write( END_TEXT );
        inTextMode = false;
    }
    
    /**
     * Set the font to draw text with.
     * 
     * @param font The font to use.
     * @param fontSize The font size to draw the text.
     * @throws IOException If there is an error writing the font information.
     */
    public void setFont( PDFont font, float fontSize ) throws IOException
    {
        String fontMapping = (String)fontMappings.get( font );
        if( fontMapping == null )
        {
            fontMapping = "F" + fontNumber++;
            fontMappings.put( font, fontMapping );
            resources.getFonts().put( fontMapping, font );
        }
        output.write( "/");
        output.write( fontMapping );
        output.write( SPACE );
        output.write( formatDecimal.format( fontSize ) );
        output.write( SPACE );
        output.write( SET_FONT );        
    }
    
    /**
     * Draw an image at the x,y coordinates, with the default size of the image.
     * 
     * @param image The image to draw.
     * @param x The x-coordinate to draw the image.
     * @param y The y-coordinate to draw the image.
     * 
     * @throws IOException If there is an error writing to the stream.
     */
    public void drawImage( PDXObjectImage image, int x, int y ) throws IOException
    {
        drawImage( image, x, y, image.getWidth(), image.getHeight() );
    }
    
    /**
     * Draw an image at the x,y coordinates and a certain width and height.
     * 
     * @param image The image to draw.
     * @param x The x-coordinate to draw the image.
     * @param y The y-coordinate to draw the image.
     * @param width The width of the image to draw.
     * @param height The height of the image to draw.
     * 
     * @throws IOException If there is an error writing to the stream.
     */
    public void drawImage( PDXObjectImage image, int x, int y, int width, int height ) throws IOException
    {
        String imageMapping = (String)imageMappings.get( image );
        if( imageMapping == null )
        {
            imageMapping = "Im" + imageNumber++;
            imageMappings.put( image, imageMapping );
            resources.getImages().put( imageMapping, image );
        }
        output.write( SAVE_GRAPHICS_STATE );
        output.write( formatDecimal.format( width ) );
        output.write( SPACE );
        output.write( formatDecimal.format( 0 ) );
        output.write( SPACE );
        output.write( formatDecimal.format( 0 ) );
        output.write( SPACE );
        output.write( formatDecimal.format( height ) );
        output.write( SPACE );
        output.write( formatDecimal.format( x ) );
        output.write( SPACE );
        output.write( formatDecimal.format( y ) );
        output.write( SPACE );
        output.write( CONCATENATE_MATRIX );
        output.write( SPACE );
        output.write( "/" );
        output.write( imageMapping );
        output.write( SPACE );
        output.write( XOBJECT_DO );
        output.write( SPACE );
        output.write( RESTORE_GRAPHICS_STATE ); 
    }
    
    /**
     * The Td operator.
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @throws IOException If there is an error writing to the stream.
     */
    public void moveTextPositionByAmount( float x, float y ) throws IOException
    {
        output.write( formatDecimal.format( x ) );
        output.write( SPACE );
        output.write( formatDecimal.format( y ) );
        output.write( SPACE );
        output.write( MOVE_TEXT_POSITION );
    }
    
    /**
     * This will draw a string at the current location on the screen.
     * 
     * @param text The text to draw.
     * @throws IOException If an io exception occurs.
     */
    public void drawString( String text ) throws IOException
    {
        COSString string = new COSString( text );
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        string.writePDF( buffer );
        output.write( new String( buffer.toByteArray(), "ISO-8859-1"));
        output.write( SPACE );
        output.write( SHOW_TEXT );
    }
    
    /**
     * Set the stroking color, specified as RGB.
     * 
     * @param color The color to set.
     * @throws IOException If an IO error occurs while writing to the stream.
     */
    public void setStrokingColor( Color color ) throws IOException
    {
        ColorSpace colorSpace = color.getColorSpace();
        if( colorSpace.getType() == ColorSpace.TYPE_RGB )
        {
            setStrokingColor( color.getRed(), color.getGreen(), color.getBlue() );
        }
        else if( colorSpace.getType() == ColorSpace.TYPE_GRAY )
        {
            color.getColorComponents( colorComponents );
            setStrokingColor( colorComponents[0] );
        }
        else if( colorSpace.getType() == ColorSpace.TYPE_CMYK )
        {
            color.getColorComponents( colorComponents );
            setStrokingColor( colorComponents[0], colorComponents[2], colorComponents[2], colorComponents[3] );
        }
        else
        {
            throw new IOException( "Error: unknown colorspace:" + colorSpace );
        }
    }
    
    /**
     * Set the non stroking color, specified as RGB.
     * 
     * @param color The color to set.
     * @throws IOException If an IO error occurs while writing to the stream.
     */
    public void setNonStrokingColor( Color color ) throws IOException
    {
        ColorSpace colorSpace = color.getColorSpace();
        if( colorSpace.getType() == ColorSpace.TYPE_RGB )
        {
            setNonStrokingColor( color.getRed(), color.getGreen(), color.getBlue() );
        }
        else if( colorSpace.getType() == ColorSpace.TYPE_GRAY )
        {
            color.getColorComponents( colorComponents );
            setNonStrokingColor( colorComponents[0] );
        }
        else if( colorSpace.getType() == ColorSpace.TYPE_CMYK )
        {
            color.getColorComponents( colorComponents );
            setNonStrokingColor( colorComponents[0], colorComponents[2], colorComponents[2], colorComponents[3] );
        }
        else
        {
            throw new IOException( "Error: unknown colorspace:" + colorSpace );
        }
    }
    
    /**
     * Set the stroking color, specified as RGB, 0-255.
     * 
     * @param r The red value.
     * @param g The green value.
     * @param b The blue value.
     * @throws IOException If an IO error occurs while writing to the stream.
     */
    public void setStrokingColor( int r, int g, int b ) throws IOException
    {
        output.write( formatDecimal.format( r/255d ) );
        output.write( SPACE );
        output.write( formatDecimal.format( g/255d ) );
        output.write( SPACE );
        output.write( formatDecimal.format( b/255d ) );
        output.write( SPACE );
        output.write( RG_STROKING );
    }
    
    /**
     * Set the stroking color, specified as CMYK, 0-255.
     * 
     * @param c The cyan value.
     * @param m The magenta value.
     * @param y The yellow value.
     * @param k The black value.
     * @throws IOException If an IO error occurs while writing to the stream.
     */
    public void setStrokingColor( int c, int m, int y, int k) throws IOException
    {
        output.write( formatDecimal.format( c/255d ) );
        output.write( SPACE );
        output.write( formatDecimal.format( m/255d ) );
        output.write( SPACE );
        output.write( formatDecimal.format( y/255d ) );
        output.write( SPACE );
        output.write( formatDecimal.format( k/255d ) );
        output.write( SPACE );
        output.write( K_STROKING );
    }
    
    /**
     * Set the stroking color, specified as CMYK, 0.0-1.0.
     * 
     * @param c The cyan value.
     * @param m The magenta value.
     * @param y The yellow value.
     * @param k The black value.
     * @throws IOException If an IO error occurs while writing to the stream.
     */
    public void setStrokingColor( double c, double m, double y, double k) throws IOException
    {
        output.write( formatDecimal.format( c ) );
        output.write( SPACE );
        output.write( formatDecimal.format( m ) );
        output.write( SPACE );
        output.write( formatDecimal.format( y ) );
        output.write( SPACE );
        output.write( formatDecimal.format( k ) );
        output.write( SPACE );
        output.write( K_STROKING );
    }
    
    /**
     * Set the stroking color, specified as grayscale, 0-255.
     * 
     * @param g The gray value.
     * @throws IOException If an IO error occurs while writing to the stream.
     */
    public void setStrokingColor( int g ) throws IOException
    {
        output.write( formatDecimal.format( g/255d ) );
        output.write( SPACE );
        output.write( G_STROKING );
    }
    
    /**
     * Set the stroking color, specified as Grayscale 0.0-1.0.
     * 
     * @param g The gray value.
     * @throws IOException If an IO error occurs while writing to the stream.
     */
    public void setStrokingColor( double g ) throws IOException
    {
        output.write( formatDecimal.format( g ) );
        output.write( SPACE );
        output.write( G_STROKING );
    }
    
    /**
     * Set the non stroking color, specified as RGB, 0-255.
     * 
     * @param r The red value.
     * @param g The green value.
     * @param b The blue value.
     * @throws IOException If an IO error occurs while writing to the stream.
     */
    public void setNonStrokingColor( int r, int g, int b ) throws IOException
    {
        output.write( formatDecimal.format( r/255d ) );
        output.write( SPACE );
        output.write( formatDecimal.format( g/255d ) );
        output.write( SPACE );
        output.write( formatDecimal.format( b/255d ) );
        output.write( SPACE );
        output.write( RG_NON_STROKING );
    }
    
    /**
     * Set the non stroking color, specified as CMYK, 0-255.
     * 
     * @param c The cyan value.
     * @param m The magenta value.
     * @param y The yellow value.
     * @param k The black value.
     * @throws IOException If an IO error occurs while writing to the stream.
     */
    public void setNonStrokingColor( int c, int m, int y, int k) throws IOException
    {
        output.write( formatDecimal.format( c/255d ) );
        output.write( SPACE );
        output.write( formatDecimal.format( m/255d ) );
        output.write( SPACE );
        output.write( formatDecimal.format( y/255d ) );
        output.write( SPACE );
        output.write( formatDecimal.format( k/255d ) );
        output.write( SPACE );
        output.write( K_NON_STROKING );
    }
    
    /**
     * Set the non stroking color, specified as CMYK, 0.0-1.0.
     * 
     * @param c The cyan value.
     * @param m The magenta value.
     * @param y The yellow value.
     * @param k The black value.
     * @throws IOException If an IO error occurs while writing to the stream.
     */
    public void setNonStrokingColor( double c, double m, double y, double k) throws IOException
    {
        output.write( formatDecimal.format( c ) );
        output.write( SPACE );
        output.write( formatDecimal.format( m ) );
        output.write( SPACE );
        output.write( formatDecimal.format( y ) );
        output.write( SPACE );
        output.write( formatDecimal.format( k ) );
        output.write( SPACE );
        output.write( K_NON_STROKING );
    }
    
    /**
     * Set the non stroking color, specified as grayscale, 0-255.
     * 
     * @param g The gray value.
     * @throws IOException If an IO error occurs while writing to the stream.
     */
    public void setNonStrokingColor( int g ) throws IOException
    {
        output.write( formatDecimal.format( g/255d ) );
        output.write( SPACE );
        output.write( G_NON_STROKING );
    }
    
    /**
     * Set the non stroking color, specified as Grayscale 0.0-1.0.
     * 
     * @param g The gray value.
     * @throws IOException If an IO error occurs while writing to the stream.
     */
    public void setNonStrokingColor( double g ) throws IOException
    {
        output.write( formatDecimal.format( g ) );
        output.write( SPACE );
        output.write( G_NON_STROKING );
    }
    
    /**
     * Draw a rectangle on the page using the current non stroking color.
     * 
     * @param x The lower left x coordinate.
     * @param y The lower left y coordinate.
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     * @throws IOException If there is an error while drawing on the screen.
     */
    public void fillRect( float x, float y, float width, float height ) throws IOException
    {
        output.write( formatDecimal.format( x ) );
        output.write( SPACE );
        output.write( formatDecimal.format( y ) );
        output.write( SPACE );
        output.write( formatDecimal.format( width ) );
        output.write( SPACE );
        output.write( formatDecimal.format( height ) );
        output.write( SPACE );
        output.write( APPEND_RECTANGLE );
        output.write( FILL );
    }
    
    
    /**
     * This will append raw commands to the content stream.
     * 
     * @param commands The commands to append to the stream.
     * @throws IOException If an error occurs while writing to the stream.
     */
    public void appendRawCommands( String commands ) throws IOException
    {
        output.write( commands );
    }
    
    /**
     * Close the content stream.  This must be called when you are done with this
     * object.
     * @throws IOException If the underlying stream has a problem being written to.
     */
    public void close() throws IOException
    {
        output.close();
    }
}