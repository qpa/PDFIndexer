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
package org.pdfbox.pdmodel.font;

import org.pdfbox.afmparser.AFMParser;

import org.pdfbox.afmtypes.FontMetric;

import org.pdfbox.cmapparser.CMapParser;

import org.pdfbox.cmaptypes.CMap;

import org.pdfbox.encoding.AFMEncoding;
import org.pdfbox.encoding.DictionaryEncoding;
import org.pdfbox.encoding.Encoding;
import org.pdfbox.encoding.EncodingManager;

import org.pdfbox.cos.COSArray;
import org.pdfbox.cos.COSBase;
import org.pdfbox.cos.COSDictionary;
import org.pdfbox.cos.COSFloat;
import org.pdfbox.cos.COSName;
import org.pdfbox.cos.COSNumber;
import org.pdfbox.cos.COSStream;


import org.pdfbox.pdmodel.common.COSArrayList;
import org.pdfbox.pdmodel.common.COSObjectable;
import org.pdfbox.pdmodel.common.PDMatrix;
import org.pdfbox.pdmodel.common.PDRectangle;

import org.pdfbox.util.Matrix;
import org.pdfbox.util.ResourceLoader;

import org.apache.log4j.Logger;

import java.awt.Graphics;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * This is the base class for all PDF fonts.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.29 $
 */
public abstract class PDFont implements COSObjectable
{
    private static Logger log = Logger.getLogger(PDFont.class);

    /**
     * The cos dictionary for this font.
     */
    protected COSDictionary font;

    /**
     * This is only used if this is a font object and it has an encoding.
     */
    private Encoding fontEncoding = null;
    /**
     * This is only used if this is a font object and it has an encoding and it is
     * a type0 font with a cmap.
     */
    private CMap cmap = null;

    private static Map afmResources = null;
    private static Map cmapObjects = null;
    private static Map afmObjects = null;
    private static Map cmapSubstitutions = null;

    static
    {
        afmResources = Collections.synchronizedMap( new HashMap() );
        cmapObjects = Collections.synchronizedMap( new HashMap() );
        afmObjects = Collections.synchronizedMap( new HashMap() );
        cmapSubstitutions = Collections.synchronizedMap( new HashMap() );

        afmResources.put( COSName.getPDFName( "Courier-Bold" ), "Resources/afm/Courier-Bold.afm" );
        afmResources.put( COSName.getPDFName( "Courier-BoldOblique" ), "Resources/afm/Courier-BoldOblique.afm" );
        afmResources.put( COSName.getPDFName( "Courier" ), "Resources/afm/Courier.afm" );
        afmResources.put( COSName.getPDFName( "Courier-Oblique" ), "Resources/afm/Courier-Oblique.afm" );
        afmResources.put( COSName.getPDFName( "Helvetica" ), "Resources/afm/Helvetica.afm" );
        afmResources.put( COSName.getPDFName( "Helvetica-Bold" ), "Resources/afm/Helvetica-Bold.afm" );
        afmResources.put( COSName.getPDFName( "Helvetica-BoldOblique" ), "Resources/afm/Helvetica-BoldOblique.afm" );
        afmResources.put( COSName.getPDFName( "Helvetica-Oblique" ), "Resources/afm/Helvetica-Oblique.afm" );
        afmResources.put( COSName.getPDFName( "Symbol" ), "Resources/afm/Symbol.afm" );
        afmResources.put( COSName.getPDFName( "Times-Bold" ), "Resources/afm/Times-Bold.afm" );
        afmResources.put( COSName.getPDFName( "Times-BoldItalic" ), "Resources/afm/Times-BoldItalic.afm" );
        afmResources.put( COSName.getPDFName( "Times-Italic" ), "Resources/afm/Times-Italic.afm" );
        afmResources.put( COSName.getPDFName( "Times-Roman" ), "Resources/afm/Times-Roman.afm" );
        afmResources.put( COSName.getPDFName( "ZapfDingbats" ), "Resources/afm/ZapfDingbats.afm" );

        cmapSubstitutions.put( "ETenms-B5-H", "ETen-B5-H" );
        cmapSubstitutions.put( "ETenms-B5-V", "ETen-B5-V" );
    }

    /**
     * This will clear AFM resources that are stored statically.
     * This is usually not a problem unless you want to reclaim
     * resources for a long running process.
     *
     * SPECIAL NOTE: The font calculations are currently in COSObject, which
     * is where they will reside until PDFont is mature enough to take them over.
     * PDFont is the appropriate place for them and not in COSObject but we need font
     * calculations for text extractaion.  THIS METHOD WILL BE MOVED OR REMOVED
     * TO ANOTHER LOCATION IN A FUTURE VERSION OF PDFBOX.
     */
    public static void clearResources()
    {
        afmObjects.clear();
        cmapObjects.clear();
    }

    /**
     * Constructor.
     */
    public PDFont()
    {
        font = new COSDictionary();
        font.setItem( COSName.TYPE, COSName.FONT );
    }

    /**
     * Constructor.
     *
     * @param fontDictionary The font dictionary according to the PDF specification.
     */
    public PDFont( COSDictionary fontDictionary )
    {
        font = fontDictionary;
    }

    /**
     * @see COSObjectable#getCOSObject()
     */
    public COSBase getCOSObject()
    {
        return font;
    }

    /**
     * This will get the font width for a character.
     *
     * @param c The character code to get the width for.
     * @param offset The offset into the array.
     * @param length The length of the data.
     *
     * @return The width is in 1000 unit of text space, ie 333 or 777
     *
     * @throws IOException If an error occurs while parsing.
     */
    public abstract float getFontWidth( byte[] c, int offset, int length ) throws IOException;

    /**
     * This will get the width of this string for this font.
     *
     * @param string The string to get the width of.
     *
     * @return The width of the string in 1000 units of text space, ie 333 567...
     *
     * @throws IOException If there is an error getting the width information.
     */
    public float getStringWidth( String string ) throws IOException
    {
        byte[] data = string.getBytes();
        float totalWidth = 0;
        for( int i=0; i<data.length; i++ )
        {
            totalWidth+=getFontWidth( data, i, 1 );
        }
        return totalWidth;
    }

    /**
     * This will get the average font width for all characters.
     *
     * @return The width is in 1000 unit of text space, ie 333 or 777
     *
     * @throws IOException If an error occurs while parsing.
     */
    public abstract float getAverageFontWidth() throws IOException;

    /**
     * This will draw a string on a canvas using the font.
     *
     * @param string The string to draw.
     * @param g The graphics to draw onto.
     * @param fontSize The size of the font to draw.
     * @param xScale The x scaling percent.
     * @param yScale The y scaling percent.
     * @param x The x coordinate to draw at.
     * @param y The y coordinate to draw at.
     *
     * @throws IOException If there is an error drawing the specific string.
     */
    public abstract void drawString( String string, Graphics g, float fontSize, 
        float xScale, float yScale, float x, float y ) throws IOException;

    /**
     * Used for multibyte encodings.
     *
     * @param data The array of data.
     * @param offset The offset into the array.
     * @param length The number of bytes to use.
     *
     * @return The int value of data from the array.
     */
    protected int getCodeFromArray( byte[] data, int offset, int length )
    {
        int code = 0;
        for( int i=0; i<length; i++ )
        {
            code <<= 8;
            code = (data[offset+i]+256)%256;
        }
        return code;
    }

    /**
     * This will attempt to get the font width from an AFM file.
     *
     * @param code The character code we are trying to get.
     *
     * @return The font width from the AFM file.
     *
     * @throws IOException if we cannot find the width.
     */
    protected float getFontWidthFromAFMFile( int code ) throws IOException
    {
        float retval = 0;
        FontMetric metric = getAFM();
        if( metric != null )
        {
            Encoding encoding = getEncoding();
            COSName characterName = encoding.getName( code );
            retval = metric.getCharacterWidth( characterName.getName() );
        }
        return retval;
    }

    /**
     * This will attempt to get the average font width from an AFM file.
     *
     * @return The average font width from the AFM file.
     *
     * @throws IOException if we cannot find the width.
     */
    protected float getAverageFontWidthFromAFMFile() throws IOException
    {
        float retval = 0;
        FontMetric metric = getAFM();
        if( metric != null )
        {
            retval = metric.getAverageCharacterWidth();
        }
        return retval;
    }

    /**
     * This will get an AFM object if one exists.
     *
     * @return The afm object from the name.
     *
     * @throws IOException If there is an error getting the AFM object.
     */
    protected FontMetric getAFM() throws IOException
    {
        COSName name = (COSName)font.getDictionaryObject( COSName.BASE_FONT );
        FontMetric result = null;
        if( name != null )
        {
            result = (FontMetric)afmObjects.get( name );
            if( result == null )
            {
                String resource = (String)afmResources.get( name );
                if( log.isDebugEnabled() )
                {
                    log.debug("resource: " + resource + ", name: " + name.getName());
                }
                if( resource == null )
                {
                    log.debug( "resource is null" );
                    //ok for now
                    //throw new IOException( "Unknown AFM font '" + name.getName() + "'" );
                }
                else
                {
                    InputStream afmStream = ResourceLoader.loadResource( resource );
                    if( afmStream == null )
                    {
                        throw new IOException( "Can't handle font width:" + resource );
                    }
                    AFMParser parser = new AFMParser( afmStream );
                    parser.parse();
                    result = parser.getResult();
                    afmObjects.put( name, result );
                }
            }
        }
        return result;
    }

    /**
     * This will perform the encoding of a character if needed.
     *
     * @param c The character to encode.
     * @param offset The offset into the array to get the data
     * @param length The number of bytes to read.
     *
     * @return The value of the encoded character.
     *
     * @throws IOException If there is an error during the encoding.
     */
    public String encode( byte[] c, int offset, int length ) throws IOException
    {
        String retval = null;
        COSName fontSubtype = (COSName)font.getDictionaryObject( COSName.SUBTYPE );
        if( fontSubtype.getName().equals( "Type0" ) ||
            fontSubtype.getName().equals( "Type1" ) ||
            fontSubtype.getName().equals( "TrueType" ))
        {
            if( cmap == null )
            {
                if( font.getDictionaryObject( COSName.TO_UNICODE ) != null )
                {
                    COSStream toUnicode = (COSStream)font.getDictionaryObject( COSName.TO_UNICODE );
                    if( toUnicode != null )
                    {
                        parseCmap( toUnicode.getUnfilteredStream(), null );
                        if( log.isDebugEnabled() )
                        {
                            log.debug( "Getting embedded CMAP Stream from ToUnicode" );
                        }
                    }
                }
                else
                {
                    COSBase encoding = font.getDictionaryObject( COSName.ENCODING );
                    if( encoding instanceof COSStream )
                    {
                        COSStream encodingStream = (COSStream)encoding;
                        parseCmap( encodingStream.getUnfilteredStream(), null );
                        if( log.isDebugEnabled() )
                        {
                            log.debug( "Getting embedded CMAP Stream from encoding" );
                        }
                    }
                    else if( fontSubtype.getName().equals( "Type0" ) &&
                             encoding instanceof COSName )
                    {
                        COSName encodingName = (COSName)encoding;
                        cmap = (CMap)cmapObjects.get( encodingName );
                        if( cmap != null )
                        {
                            cmap = (CMap)cmapObjects.get( encodingName );
                        }
                        else
                        {
                            if( log.isDebugEnabled() )
                            {
                                log.debug( "Getting CMAP Stream from resource" );
                            }
                            String cmapName = encodingName.getName();
                            cmapName = performCMAPSubstitution( cmapName );
                            String resourceName = "Resources/cmap/" + cmapName;
                            parseCmap( ResourceLoader.loadResource( resourceName ), encodingName );
                            if( cmap == null && !encodingName.getName().equals( COSName.IDENTITY_H.getName() ) )
                            {
                                throw new IOException( "Error: Could not find predefined " +
                                "CMAP file for '" + encodingName.getName() + "'" );
                            }
                        }
                    }
                    else if( encoding instanceof COSName ||
                             encoding instanceof COSDictionary )
                    {
                        Encoding currentFontEncoding = getEncoding();
                        if( currentFontEncoding != null )
                        {
                            retval = currentFontEncoding.getCharacter( getCodeFromArray( c, offset, length ) );
                        }
                    }
                    else
                    {
                        COSDictionary fontDescriptor =
                            (COSDictionary)font.getDictionaryObject( COSName.getPDFName( "FontDescriptor" ) );
                        if( fontSubtype.getName().equals( "TrueType" ) &&
                            fontDescriptor != null &&
                            (fontDescriptor.getDictionaryObject( COSName.getPDFName( "FontFile" ) )!= null ||
                             fontDescriptor.getDictionaryObject( COSName.getPDFName( "FontFile2" ) ) != null ||
                             fontDescriptor.getDictionaryObject( COSName.getPDFName( "FontFile3" ) ) != null ) )
                        {
                            //If we are using an embedded font then there is not much we can do besides
                            //return the same character codes.
                            //retval = new String( c,offset, length );
                            retval = getStringFromArray( c, offset, length );
                        }
                        else
                        {
                            //this case will be handled below after checking the cmap
                        }
                    }
                }


            }
        }
        log.debug( "retval=" +  retval + " cmap=" + cmap);
        if( retval == null && cmap != null )
        {
            
            retval = cmap.lookup( c, offset, length );
            if( log.isDebugEnabled() )
            {
                log.debug( "cmap.lookup(" +c + ")='" +retval + "'" );
            }
        }
        //if we havn't found a value yet and 
        //we are still on the first byte and
        //there is no cmap or the cmap does not have 2 byte mappings then try to encode
        //using fallback methods.
        if( retval == null && 
            length == 1 && 
            (cmap == null || !cmap.hasTwoByteMappings()))
        {
            log.debug( "No CMAP: Using fallback method");
            Encoding encoding = getEncoding();
            if( encoding != null )
            {
                retval = encoding.getCharacter( getCodeFromArray( c, offset, length ) );
            }
            if( retval == null )
            {
                retval = getStringFromArray( c, offset, length );
            }
        }
        return retval;
    }
    
    private static final String[] SINGLE_CHAR_STRING = new String[256];
    private static final String[][] DOUBLE_CHAR_STRING = new String[256][256];
    static
    {
        for( int i=0; i<256; i++ )
        {
            SINGLE_CHAR_STRING[i] = new String( new byte[] {(byte)i} );
            for( int j=0; j<256; j++ )
            {
                DOUBLE_CHAR_STRING[i][j] = new String( new byte[] {(byte)i, (byte)j} );
            }
        }
    }
    
    private static String getStringFromArray( byte[] c, int offset, int length ) throws IOException
    {
        String retval = null;
        if( length == 1 )
        {
            retval = SINGLE_CHAR_STRING[(c[offset]+256)%256];
        }
        else if( length == 2 )
        {
            retval = DOUBLE_CHAR_STRING[(c[offset]+256)%256][(c[offset+1]+256)%256];
        }
        else
        {
            throw new IOException( "Error:Unknown character length:" + length );
        }
        return retval;
    }

    /**
     * Some cmap names are synonyms for other CMAPs.  If that is the case
     * then this method will perform that substitution.
     *
     * @param cmapName The name of the cmap to attempt to look up.
     *
     * @return Either the original name or the substituted name.
     */
    private String performCMAPSubstitution( String cmapName )
    {
        String retval = (String)cmapSubstitutions.get( cmapName );
        if( retval == null )
        {
            //if there is no substitution then just return the same value.
            retval = cmapName;
        }
        return retval;
    }

    private void parseCmap( InputStream cmapStream, COSName encodingName ) throws IOException
    {
        if( log.isDebugEnabled() )
        {
            log.debug( "Parsing a new CMAP for font:" + font );
        }
        if( cmapStream != null )
        {
            CMapParser parser = new CMapParser( cmapStream, null );
            parser.parse();
            cmap = parser.getResult();
            if( encodingName != null )
            {
                cmapObjects.put( encodingName, cmap );
            }
        }
    }
    
    /**
     * The will set the encoding for this font.
     * 
     * @param enc The font encoding.
     */
    public void setEncoding( Encoding enc )
    {
        font.setItem( COSName.ENCODING, enc );
        fontEncoding = enc;
    }

    /**
     * This will get or create the encoder.
     *
     * modified by Christophe Huault : DGBS Strasbourg huault@free.fr october 2004
     *
     * @return The encoding to use.
     *
     * @throws IOException If there is an error getting the encoding.
     */
    public Encoding getEncoding() throws IOException
    {
        EncodingManager manager = new EncodingManager();
        if( fontEncoding == null )
        {
            COSBase encoding = font.getDictionaryObject( COSName.ENCODING );
            if( encoding == null )
            {
                FontMetric metric = getAFM();
                if( metric != null )
                {
                    fontEncoding = new AFMEncoding( metric );
                }
                if( fontEncoding == null )
                {
                    fontEncoding = manager.getStandardEncoding();
                }
            }
            /**
             * Si la cl� /Encoding existe dans le dictionnaire fonte il y a deux possibilit�s :
             * 1er cas : elle est associ� � une reference contenant un dictionnaire de type encoding.
             * Ce dictionnaire PDF est repr�sent� par un DictionaryEncoding.
             * If the /Encoding Key does exist in the font dictionary, there are two cases :
             * case one : The value associated with /Encoding is a reference to a dictionary.
             * This dictionary is represented by an instance of DictionaryEncoding class
             */
            else if( encoding instanceof COSDictionary )
            {
                COSDictionary encodingDic = (COSDictionary)encoding;
                //Let's see if the encoding dictionary has a base encoding
                //If it does not then we will attempt to get it from the font
                //file
                COSName baseEncodingName = (COSName) encodingDic.getDictionaryObject(
                    COSName.BASE_ENCODING);
                //on ajoute une entr�e /BaseEncoding dans /Encoding uniquement si elle en est absente
                //if not find in Encoding dictinary target, we try to find it from else where
                if( baseEncodingName == null)
                {
                    COSName fontEncodingFromFile = getEncodingFromFont();
                    encodingDic.setItem(
                        COSName.BASE_ENCODING,
                        fontEncodingFromFile );
                }
                fontEncoding = new DictionaryEncoding( encodingDic );
            }
            else if( encoding instanceof COSName )
            {
                if( !encoding.equals( COSName.IDENTITY_H ) )
                {
                    fontEncoding = manager.getEncoding( (COSName)encoding );
                }
            }
            else
            {
                throw new IOException( "Unexpected encoding type:" + encoding.getClass().getName() );
            }
        }
        return fontEncoding;
    }
    
    /**
     * This will always return "Font" for fonts.
     * 
     * @return The type of object that this is.
     */
    public String getType()
    {
        return font.getNameAsString( "Type" );
    }
    
    /**
     * This will get the subtype of font, Type1, Type3, ...
     * 
     * @return The type of font that this is.
     */
    public String getSubType()
    {
        return font.getNameAsString( "Subtype" );
    }
    
    /**
     * The PostScript name of the font.
     * 
     * @return The postscript name of the font.
     */
    public String getBaseFont()
    {
        return font.getNameAsString( "BaseFont" );
    }
    
    /**
     * Set the PostScript name of the font.
     * 
     * @param baseFont The postscript name for the font.
     */
    public void setBaseFont( String baseFont )
    {
        font.setName( "BaseFont", baseFont );
    }
    
    /**
     * The code for the first char or -1 if there is none.
     * 
     * @return The code for the first character.
     */
    public int getFirstChar()
    {
        return font.getInt( "FirstChar", -1 );
    }
    
    /**
     * Set the first character this font supports.
     * 
     * @param firstChar The first character.
     */
    public void setFirstChar( int firstChar )
    {
        font.setInt( "FirstChar", firstChar );
    }
    
    /**
     * The code for the last char or -1 if there is none.
     * 
     * @return The code for the last character.
     */
    public int getLastChar()
    {
        return font.getInt( "LastChar", -1 );
    }
    
    /**
     * Set the last character this font supports.
     * 
     * @param lastChar The last character.
     */
    public void setLastChar( int lastChar )
    {
        font.setInt( "LastChar", lastChar );
    }
    
    /**
     * The widths of the characters.  This will be null for the standard 14 fonts.
     * 
     * @return The widths of the characters.
     */
    public List getWidths()
    {
        COSArray array = (COSArray)font.getDictionaryObject( "Widths" );
        return COSArrayList.convertFloatCOSArrayToList( array );
    }
    
    /**
     * Set the widths of the characters code.
     * 
     * @param widths The widths of the character codes.
     */
    public void setWidths( List widths )
    {
        font.setItem( "Widths", COSArrayList.converterToCOSArray( widths ) );
    }
    
    /**
     * This will get the matrix that is used to transform glyph space to
     * text space.  By default there are 1000 glyph units to 1 text space
     * unit, but type3 fonts can use any value.  
     * 
     * Note:If this is a type3 font then it can be modified via the PDType3Font.setFontMatrix, otherwise this
     * is a read-only property.
     * 
     * @return The matrix to transform from glyph space to text space.
     */
    public PDMatrix getFontMatrix()
    {
        PDMatrix matrix = null;
        COSArray array = (COSArray)font.getDictionaryObject( "FontMatrix" );
        if( array == null )
        {
            array = new COSArray();
            array.add( new COSFloat( 0.001f ) );
            array.add( COSNumber.ZERO );
            array.add( COSNumber.ZERO );
            array.add( new COSFloat( 0.001f ) );
            array.add( COSNumber.ZERO );
            array.add( COSNumber.ZERO );
        }
        matrix = new PDMatrix(array);
        
        return matrix;
    }
    
    /**
     * Try to get the encoding for the font and add it to the target
     * the target must be an an Encoding Dictionary.
     *
     * added by Christophe Huault : DGBS Strasbourg huault@free.fr october 2004
     *
     * @return The encoding from the font.
     * 
     * @throws IOException If there is an error reading the file.
     */
    private COSName getEncodingFromFont() throws IOException 
    {
        //This whole section of code needs to be replaced with an actual
        //type1 font parser!!
        
        
        COSName retvalue = null;
        //recuperer le programme de fonte dans son stream qui doit se trouver
        //dans le flux r�f�renc� par � la cl� FileFont lui m�me situ� dans
        //le dictionnaire associ� � /FontDescriptor du dictionnaire de type /Font courrant
        //get the font program in the stream which should be located in
         //the /FileFont Stream object himself in the /FontDescriptior of the current
        //font dictionary
        COSDictionary fontDescriptor = (COSDictionary) font.getDictionaryObject(
            COSName.getPDFName("FontDescriptor"));
        if( fontDescriptor != null )
        {
            COSStream fontFile = (COSStream) fontDescriptor.getDictionaryObject(
                COSName.getPDFName("FontFile"));
            if( fontFile != null )
            {
                BufferedReader in = 
                        new BufferedReader(new InputStreamReader(fontFile.getUnfilteredStream()));
                /**
                 * this section parse the FileProgram stream searching for a /Encoding entry
                 * the research stop if the entry "currentdict end" is reach or after 100 lignes
                 */
                StringTokenizer st = null;
                boolean found = false;
                String line = "";
                String key = null;
                for( int i = 0; null!=( line = in.readLine() ) &&
                                i < 40  && 
                                !line.equals("currentdict end")
                                && !found; i++) 
                {
                    st = new StringTokenizer(line);
                    if( st.hasMoreTokens() )
                    {
                        key = st.nextToken();
                        if(key.equals("/Encoding") && st.hasMoreTokens() )
                        {
                            COSName value = COSName.getPDFName( st.nextToken() );
                            found = true;
                            if( value.equals( COSName.MAC_ROMAN_ENCODING ) ||
                                value.equals( COSName.PDF_DOC_ENCODING ) ||
                                value.equals( COSName.STANDARD_ENCODING ) ||
                                value.equals( COSName.WIN_ANSI_ENCODING ) )
                            {
                                //value is expected to be one of the encodings
                                //ie. StandardEncoding,WinAnsiEncoding,MacRomanEncoding,PDFDocEncoding
                                retvalue = value;
                            }    
                        }
                    }
                }
            }
        }
        return retvalue;
    }
    
    /**
     * This will get the fonts bouding box.
     *
     * @return The fonts bouding box.
     * 
     * @throws IOException If there is an error getting the bounding box.
     */
    public abstract PDRectangle getFontBoundingBox() throws IOException;
}