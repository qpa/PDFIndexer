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
package org.pdfbox.examples.util;

import org.pdfbox.exceptions.InvalidPasswordException;

import org.pdfbox.pdfparser.PDFParser;

import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.util.PDFStreamEngine;
import org.pdfbox.util.PDFTextStripper;
import org.pdfbox.util.TextPosition;

import java.io.FileInputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.List;

/**
 * This is an example on how to get some x/y coordinates of text.
 *
 * Usage: java org.pdfbox.examples.util.PrintTextLocations &lt;input-pdf&gt;
 *
 * @author Ben Litchfield (ben@benlitchfield.com)
 * @version $Revision: 1.1 $
 */
public class PrintTextLocations extends PDFTextStripper
{
    /**
     * Default constructor.
     * 
     * @throws IOException If there is an error loading text stripper properties.
     */
    public PrintTextLocations() throws IOException
    {
        //default constructor.
    }
    
    /**
     * This will print the documents data.
     *
     * @param args The command line arguments.
     *
     * @throws Exception If there is an error parsing the document.
     */
    public static void main( String[] args ) throws Exception
    {
        if( args.length != 1 )
        {
            usage();
        }
        else
        {
            PDDocument document = null;
            FileInputStream file = null;
            try
            {
                file = new FileInputStream( args[0] );
                PDFParser parser = new PDFParser( file );
                parser.parse();
                document = parser.getPDDocument();
                if( document.isEncrypted() )
                {
                    try
                    {
                        document.decrypt( "" );
                    }
                    catch( InvalidPasswordException e )
                    {
                        System.err.println( "Error: Document is encrypted with a password." );
                        System.exit( 1 );
                    }
                }
                PrintTextLocations printer = new PrintTextLocations();
                List allPages = document.getDocumentCatalog().getAllPages();
                for( int i=0; i<allPages.size(); i++ )
                {
                    PDPage page = (PDPage)allPages.get( i );
                    System.out.println( "Processing page: " + i );
                    printer.processStream( page, page.findResources(), page.getContents().getStream() );
                }
            }
            finally
            {
                if( file != null )
                {
                    file.close();
                }
                if( document != null )
                {
                    document.close();
                }
            }
        }
    }
    
    /**
     * A method provided as an event interface to allow a subclass to perform
     * some specific functionality when a character needs to be displayed.
     *
     * @param text The character to be displayed.
     */
    protected void showCharacter( TextPosition text )
    {
        System.out.println( "String[" + text.getX() + "," + text.getY() + "]" + text.getCharacter() );
    }

    /**
     * This will print the usage for this document.
     */
    private static void usage()
    {
        System.err.println( "Usage: java org.pdfbox.examples.pdmodel.PrintTextLocations <input-pdf>" );
    }

}