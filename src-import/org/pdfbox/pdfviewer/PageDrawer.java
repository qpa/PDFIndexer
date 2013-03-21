/**
 * Copyright (c) 2003-2005, www.pdfbox.org
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
package org.pdfbox.pdfviewer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import java.io.IOException;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.pdfbox.cos.COSName;
import org.pdfbox.cos.COSNumber;

import org.pdfbox.pdmodel.PDPage;

import org.pdfbox.pdmodel.font.PDFont;
import org.pdfbox.pdmodel.graphics.xobject.PDInlinedImage;
import org.pdfbox.pdmodel.graphics.xobject.PDXObject;
import org.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

import org.pdfbox.util.ImageParameters;
import org.pdfbox.util.Matrix;
import org.pdfbox.util.PDFOperator;
import org.pdfbox.util.PDFStreamEngine;
import org.pdfbox.util.ResourceLoader;
import org.pdfbox.util.TextPosition;

/**
 * This will paint a page in a PDF document to a graphics context.
 *
 * @author Ben Litchfield (ben@benlitchfield.com)
 * @version $Revision: 1.16 $
 */
public class PageDrawer extends PDFStreamEngine
{
    private static Logger log = Logger.getLogger(PDFStreamEngine.class);

    private Graphics2D graphics;
    private Dimension pageSize;
    private PDPage page;

    private GeneralPath linePath = new GeneralPath();
    private Color strokingColor = Color.BLACK;
    private Color nonStrokingColor = Color.BLACK;
    
    /**
     * Default constructor, loads properties from file.
     * 
     * @throws IOException If there is an error loading properties from the file.
     */
    public PageDrawer() throws IOException
    {
        super( ResourceLoader.loadProperties( "Resources/PageDrawer.properties" ) );
    }

    /**
     * This will draw the page to the requested context.
     *
     * @param g The graphics context to draw onto.
     * @param p The page to draw.
     * @param pageDimension The size of the page to draw.
     *
     * @throws IOException If there is an IO error while drawing the page.
     */
    public void drawPage( Graphics g, PDPage p, Dimension pageDimension ) throws IOException
    {
        graphics = (Graphics2D)g;
        page = p;
        pageSize = pageDimension;
        
        graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        processStream( page, page.findResources(), page.getContents().getStream() );
        // Transformations should be done in order
        // 1 - Translate
        // 2 - Rotate
        // 3 - Scale
        // Refer to PDFReference p176 (or 188 in xpdf)
        //AffineTransform transform = graphics.getTransform();        
        //transform.setToTranslate( 0, page.findMediaBox().getHeight()/2 );
        //transform.setToRotation((double)p.getRotation());
        //transform.setTransform( 1, 0, 0, 1, 0, 0 );        
        //transform.setToScale( 1, 1 );
        
        //AffineTransform rotation = graphics.getTransform();
        //rotation.rotate( (page.findRotation() * Math.PI) / 180d );
        //graphics.setTransform( rotation );
    }

    /**
     * You should override this method if you want to perform an action when a
     * string is being shown.
     *
     * @param text The string to display.
     */
    protected void showCharacter( TextPosition text )
    {
        //should use colorspaces for the font color but for now assume that
        //the font color is black
        try
        {
            graphics.setColor( Color.black );
            PDFont font = text.getFont();
            font.drawString( text.getCharacter(), graphics, text.getFontSize(), text.getXScale(), text.getYScale(),
                             text.getX(), text.getY() );
        }
        catch( IOException io )
        {
            io.printStackTrace();
        }
    }
    
    /**
     * This is used to handle an operation.
     *
     * @param operator The operation to perform.
     * @param arguments The list of arguments.
     *
     * @throws IOException If there is an error processing the operation.
     */
    protected void processOperator( PDFOperator operator, List arguments ) throws IOException
    {
        super.processOperator( operator, arguments );
        String operation = operator.getOperation();
        if( log.isDebugEnabled() )
        {
            log.debug( "processOperator( '" + operation + "' )" );
        }
        
        if( operation.equals( "BI" ) )
        {
            //begin inline image object
            ImageParameters params = operator.getImageParameters();
            PDInlinedImage image = new PDInlinedImage();
            image.setImageParameters( params );
            image.setImageData( operator.getImageData() );
            BufferedImage awtImage = image.createImage();
            
            Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
            
            int width = awtImage.getWidth();
            int height = awtImage.getHeight();

            
            AffineTransform at = new AffineTransform(
                ctm.getValue(0,0)/width,
                ctm.getValue(0,1),
                ctm.getValue(1,0),
                ctm.getValue(1,1)/height,
                ctm.getValue(2,0),
                ctm.getValue(2,1)
            );
            //at.setToRotation((double)page.getRotation());
 
            
            // The transformation should be done 
            // 1 - Translation
            // 2 - Rotation
            // 3 - Scale or Skew
            //AffineTransform at = new AffineTransform();

            // Translation
            //at = new AffineTransform();
            //at.setToTranslation((double)ctm.getValue(0,0),
            //                    (double)ctm.getValue(0,1));

            // Rotation
            //AffineTransform toAdd = new AffineTransform();
            //toAdd.setToRotation(1.5705);
            //toAdd.setToRotation(ctm.getValue(2,0)*(Math.PI/180));
            //at.concatenate(toAdd);

            // Scale / Skew?
            //toAdd.setToScale(width, height); 
            //at.concatenate(toAdd);
            //at.setToScale( width, height );
            graphics.drawImage( awtImage, at, null );
            //graphics.drawImage( awtImage,0,0, width,height,null);
        }
        else if( operation.equals( "f*" ) )
        {
            //linePath.closePath();
            graphics.setColor( nonStrokingColor );
            linePath.setWindingRule( GeneralPath.WIND_EVEN_ODD );
            graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
            //else
            //{
                graphics.fill( linePath );
            //}
        }
        else if( operation.equals( "Do" ) )
        {
            COSName objectName = (COSName)arguments.get( 0 );
            Map xobjects = getResources().getXObjects();
            PDXObject xobject = (PDXObject)xobjects.get( objectName.getName() );
            if( xobject instanceof PDXObjectImage )
            {
                PDXObjectImage image = (PDXObjectImage)xobject;
                try
                {
                    BufferedImage awtImage = image.getRGBImage();
                    Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
                    
                    int width = awtImage.getWidth();
                    int height = awtImage.getHeight();

                    double rotationInRadians =(page.findRotation() * Math.PI)/180;
                     
                    
                    AffineTransform rotation = new AffineTransform();
                    rotation.setToRotation( rotationInRadians );
                    AffineTransform rotationInverse = rotation.createInverse();
                    Matrix rotationInverseMatrix = new Matrix();
                    rotationInverseMatrix.setFromAffineTransform( rotationInverse );
                    Matrix rotationMatrix = new Matrix();
                    rotationMatrix.setFromAffineTransform( rotation );
                    
                    Matrix unrotatedCTM = ctm.multiply( rotationInverseMatrix );
                    
                    Matrix scalingParams = unrotatedCTM.extractScaling();
                    Matrix scalingMatrix = Matrix.getScaleInstance(1f/width,1f/height);
                    scalingParams = scalingParams.multiply( scalingMatrix );
                    
                    Matrix translationParams = unrotatedCTM.extractTranslating();
                    Matrix translationMatrix = null;
                    int pageRotation = page.findRotation();
                    if( pageRotation == 0 )
                    {
                        translationParams.setValue(2,1, -translationParams.getValue( 2,1 ));
                        translationMatrix = Matrix.getTranslatingInstance( 
                            0, (float)pageSize.getHeight()-height*scalingParams.getYScale() );
                    }
                    else if( pageRotation == 90 )
                    {
                        translationMatrix = Matrix.getTranslatingInstance( 0, (float)pageSize.getHeight() );
                    }
                    else 
                    {
                        //TODO need to figure out other cases
                    }
                    translationParams = translationParams.multiply( translationMatrix );

                    AffineTransform at = new AffineTransform( 
                            scalingParams.getValue( 0,0), 0,
                            0, scalingParams.getValue( 1, 1),
                            translationParams.getValue(2,0), translationParams.getValue( 2,1 )
                        );
                    
                    
                    

                    //at.setToTranslation( pageSize.getHeight()-ctm.getValue(2,0),ctm.getValue(2,1) );
                    //at.setToScale( ctm.getValue(0,0)/width, ctm.getValue(1,1)/height);
                    //at.setToRotation( (page.findRotation() * Math.PI)/180 );
                    
                    
                    
                    //AffineTransform rotation = new AffineTransform();
                    //rotation.rotate( (90*Math.PI)/180);
                    
                    /*
                    
                    // The transformation should be done 
                    // 1 - Translation
                    // 2 - Rotation
                    // 3 - Scale or Skew
                    AffineTransform at = new AffineTransform();
    
                    // Translation
                    at = new AffineTransform();
                    //at.setToTranslation((double)ctm.getValue(0,0),
                    //                    (double)ctm.getValue(0,1));
                    
                    // Rotation
                    //AffineTransform toAdd = new AffineTransform();
                    toAdd.setToRotation(1.5705);
                    toAdd.setToRotation(ctm.getValue(2,0)*(Math.PI/180));
                    at.concatenate(toAdd);
                    */
                    
                    // Scale / Skew?
                    //toAdd.setToScale(1, 1); 
                    //at.concatenate(toAdd);

                    graphics.drawImage( awtImage, at, null );
                }
                catch( Exception e )
                {
                    e.printStackTrace();
                }
            }
            else
            {
                log.warn( "Unknown xobject type:" + xobject );
            }
            
            
            //invoke named object.
        }
        else if( operation.equals( "l" ) )
        {
            //append straight line segment from the current point to the point.
            COSNumber x = (COSNumber)arguments.get( 0 );
            COSNumber y = (COSNumber)arguments.get( 1 );
            linePath.lineTo( x.floatValue(), (float)fixY( x.doubleValue(), y.doubleValue()) );
        }
        else if( operation.equals( "m" ) )
        {
            COSNumber x = (COSNumber)arguments.get( 0 );
            COSNumber y = (COSNumber)arguments.get( 1 );
            
            linePath.reset();
            linePath.moveTo( x.floatValue(), (float)fixY( x.doubleValue(), y.doubleValue()) );
            //System.out.println( "<m x=\"" + x.getValue() + "\" y=\"" + y.getValue() + "\" >" );
        }
        else if( operation.equals( "re" ) )
        {
            COSNumber x = (COSNumber)arguments.get( 0 );
            COSNumber y = (COSNumber)arguments.get( 1 );
            COSNumber w = (COSNumber)arguments.get( 2 );
            COSNumber h = (COSNumber)arguments.get( 3 );
            Rectangle2D rect = new Rectangle2D.Double(
                x.doubleValue(),
                fixY( x.doubleValue(), y.doubleValue())-h.doubleValue(),
                w.doubleValue()+1,
                h.doubleValue()+1);
            linePath.reset();
            
            linePath.append( rect, false );
            //graphics.drawRect((int)x.doubleValue(), (int)(pageSize.getHeight() - y.doubleValue()),
            //                  (int)w.doubleValue(),(int)h.doubleValue() );
            //System.out.println( "<re x=\"" + x.getValue() + "\" y=\"" + y.getValue() + "\" width=\"" +
            //                                 width.getValue() + "\" height=\"" + height.getValue() + "\" >" );
        }
        else if( operation.equals( "rg" ) )
        {
            COSNumber r = (COSNumber)arguments.get( 0 );
            COSNumber g = (COSNumber)arguments.get( 1 );
            COSNumber b = (COSNumber)arguments.get( 2 );
            nonStrokingColor = new Color( r.floatValue(), g.floatValue(), b.floatValue() );
        }
        else if( operation.equals( "RG" ) )
        {
            COSNumber r = (COSNumber)arguments.get( 0 );
            COSNumber g = (COSNumber)arguments.get( 1 );
            COSNumber b = (COSNumber)arguments.get( 2 );
            strokingColor = new Color( r.floatValue(), g.floatValue(), b.floatValue() );
        }
        else if( operation.equals( "S" ) )
        {
            graphics.setColor( strokingColor );
            graphics.draw( linePath );
        }
        else if( operation.equals( "w" ) )
        {
            float lineWidth = (float)getGraphicsState().getLineWidth();
            if( lineWidth == 0 )
            {
                lineWidth = 1;
            }
            graphics.setStroke( new BasicStroke( lineWidth ) );
        }
    }
    
    
    private double fixY( double x, double y )
    {
        double retval = y;
        int rotation = page.findRotation();
        if( rotation == 0 )
        {
            retval = pageSize.getHeight() - y;
        }
        else if( rotation == 90 )
        {
            retval = y;
        }
        return retval;
    }


}