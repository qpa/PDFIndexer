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
package org.pdfbox.util.operator;

import java.util.List;
import org.apache.log4j.Logger;
import org.pdfbox.cos.COSName;
import org.pdfbox.cos.COSNumber;
import org.pdfbox.pdmodel.font.PDFont;
import java.io.IOException;

/**
 * <p>Titre : PDFEngine Modification.</p>
 * <p>Description : Structal modification of the PDFEngine class :
 * the long sequence of conditions in processOperator is remplaced by
 * this strategy pattern</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Soci�t� : DBGS</p>
 * @author Huault : huault@free.fr
 * @version $Revision: 1.2 $
 */

public class SetTextFont extends OperatorProcessor 
{
    private static final Logger LOG = Logger.getLogger(SetTextFont.class);

    /**
     * Tf selectfont Set text font and size.
     * @param arguments List
     * @throws IOException If an error occurs while processing the font.
     */
    public void process(List arguments) throws IOException
    {
        //there are some documents that are incorrectly structured and 
        //arguments are in the wrong spot, so we will silently ignore them
        //if there are no arguments
        if( arguments.size() >= 2 )
        {
            //set font and size
            COSName fontName = (COSName)arguments.get( 0 );
            float fontSize = ((COSNumber)arguments.get( 1 ) ).floatValue();
            context.getGraphicsState().getTextState().setFontSize( fontSize );
    
            if (LOG.isDebugEnabled())
            {
                LOG.debug("<Tf font=\"" + fontName.getName() + "\" size=\"" +
                          context.getGraphicsState().getTextState().getFontSize() + "\">");
            }
    
            //old way
            //graphicsState.getTextState().getFont() = (COSObject)stream.getDictionaryObject( fontName );
            //if( graphicsState.getTextState().getFont() == null )
            //{
            //    graphicsState.getTextState().getFont() = (COSObject)graphicsState.getTextState().getFont()
            //                                           Dictionary.getItem( fontName );
            //}
            context.getGraphicsState().getTextState().setFont( (PDFont)context.getFonts().get( fontName.getName() ) );
            if( context.getGraphicsState().getTextState().getFont() == null )
            {
                throw new IOException( "Error: Could not find font(" + fontName + ") in map=" + context.getFonts() );
            }
        }
    }

}
