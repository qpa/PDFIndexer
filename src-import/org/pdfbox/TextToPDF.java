/**
 * Copyright (c) 2005, www.pdfbox.org
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
package org.pdfbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDPage;

import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.pdfbox.pdmodel.font.PDSimpleFont;
import org.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.pdfbox.pdmodel.font.PDType1Font;


/**
 * This will take a text file and ouput a pdf with that text.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.1 $
 */
public class TextToPDF
{
    private int fontSize = 10;
    private PDSimpleFont font = PDType1Font.HELVETICA;
    
    /**
     * Create a PDF document with some text.
     *
     * @param doc The document to add the text to.
     * @param text The stream of text data.
     * 
     * @return The document with the text in it.
     *
     * @throws IOException If there is an error writing the data.
     */
    public PDDocument createPDFFromText( PDDocument doc, Reader text ) throws IOException
    {
        try
        {
            
            int margin = 40;
            float height = font.getFontDescriptor().getFontBoundingBox().getHeight()/1000;
            
            //calculate font height and increase by 5 percent.
            height = height*fontSize*1.05f;
            doc = new PDDocument();
            BufferedReader data = new BufferedReader( text );
            String nextLine = null;
            PDPage page = new PDPage();
            PDPageContentStream contentStream = null;
            float y = -1;
            float maxStringLength = page.getMediaBox().getWidth() - 2*margin;
            while( (nextLine = data.readLine()) != null )
            {
                
                String[] lineWords = nextLine.trim().split( " " );
                int lineIndex = 0;
                while( lineIndex < lineWords.length )
                {   
                    StringBuffer nextLineToDraw = new StringBuffer();
                    float lengthIfUsingNextWord = 0;
                    do
                    {
                        nextLineToDraw.append( lineWords[lineIndex] );
                        nextLineToDraw.append( " " );
                        lineIndex++;
                        if( lineIndex < lineWords.length )
                        {
                            String lineWithNextWord = nextLineToDraw.toString() + lineWords[lineIndex];
                            lengthIfUsingNextWord = 
                                (font.getStringWidth( lineWithNextWord )/1000) * fontSize;
                        }
                    }
                    while( lineIndex < lineWords.length && 
                           lengthIfUsingNextWord < maxStringLength );
                    if( y < margin )
                    {
                        page = new PDPage();
                        doc.addPage( page );
                        if( contentStream != null )
                        {
                            contentStream.endText();
                            contentStream.close();
                        }
                        contentStream = new PDPageContentStream(doc, page);
                        contentStream.setFont( font, fontSize );
                        contentStream.beginText();
                        y = page.getMediaBox().getHeight() - margin + height;
                        contentStream.moveTextPositionByAmount( 
                            margin, y );
                        
                    }
                    //System.out.println( "Drawing string at " + x + "," + y );
                    
                    
                    contentStream.moveTextPositionByAmount( 0, -height);
                    y -= height;
                    contentStream.drawString( nextLineToDraw.toString() );
                }
                
                
            }  
            if( contentStream != null )
            {
                contentStream.endText();
                contentStream.close();
            }
        }
        catch( IOException io )
        {
            if( doc != null )
            {
                doc.close();
            }
            throw io;
        }
        return doc;
    }

    /**
     * This will create a PDF document with a single image on it.
     * <br />
     * see usage() for commandline
     *
     * @param args Command line arguments.
     * 
     * @throws IOException If there is an error with the PDF.
     */
    public static void main(String[] args) throws IOException
    {
        TextToPDF app = new TextToPDF();
        PDDocument doc = null;
        try
        {
            doc = new PDDocument();
            if( args.length < 2 )
            {
                app.usage();
            }
            else
            {
                for( int i=0; i<args.length-2; i++ )
                {
                    if( args[i].equals( "-standardFont" ))
                    {
                        i++;
                        app.setFont( PDType1Font.getStandardFont( args[i] ));
                    }
                    else if( args[i].equals( "-ttf" ))
                    {
                        i++;
                        PDTrueTypeFont font = PDTrueTypeFont.loadTTF( doc, new File( args[i]));
                        app.setFont( font );
                    }
                    else if( args[i].equals( "-fontSize" ))
                    {
                        i++;
                        app.setFontSize( Integer.parseInt( args[i] ) );
                    }
                    else
                    {
                        throw new IOException( "Unknown argument:" + args[i] );
                    }
                }
                doc = app.createPDFFromText( doc, new FileReader( args[args.length-1] ) );
                doc.save( args[args.length-2] );
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if( doc != null )
            {
                doc.close();
            }
        }
    }

    /**
     * This will print out a message telling how to use this example.
     */
    private void usage()
    {
        String[] std14 = PDType1Font.getStandard14Names();
        System.err.println( "usage: " + this.getClass().getName() + " [options] <output-file> <text-file>" );
        System.err.println( "    -standardFont <name>    default:" + PDType1Font.HELVETICA.getBaseFont() );
        for( int i=0; i<std14.length; i++ )
        {
            System.err.println( "                                    " + std14[i] );
        }
        System.err.println( "    -ttf <ttf file>         The TTF font to use.");
        System.err.println( "    -fontSize <fontSize>    default:10" );
        
        
    }
    /**
     * @return Returns the font.
     */
    public PDSimpleFont getFont()
    {
        return font;
    }
    /**
     * @param aFont The font to set.
     */
    public void setFont(PDSimpleFont aFont)
    {
        this.font = aFont;
    }
    /**
     * @return Returns the fontSize.
     */
    public int getFontSize()
    {
        return fontSize;
    }
    /**
     * @param aFontSize The fontSize to set.
     */
    public void setFontSize(int aFontSize)
    {
        this.fontSize = aFontSize;
    }
}