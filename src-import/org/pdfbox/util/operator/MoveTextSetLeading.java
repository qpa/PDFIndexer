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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.pdfbox.cos.COSFloat;
import org.pdfbox.cos.COSNumber;

/**
 *
 * <p>Titre : PDFEngine Modification.</p>
 * <p>Description : Structal modification of the PDFEngine class :
 * the long sequence of conditions in processOperator is remplaced by
 * this strategy pattern</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Soci�t� : DBGS</p>
 * @author Huault : huault@free.fr
 * @version $Revision: 1.2 $
 */
public class MoveTextSetLeading extends OperatorProcessor 
{

    private static final Logger LOG = Logger.getLogger(MoveTextSetLeading.class);

    /**
     * process : TD Move text position and set leading.
     *
     * @param arguments List
     * @throws IOException If there is an error during processing.
     */
    public void process(List arguments) throws IOException
    {
        //move text position and set leading
        COSNumber y = (COSNumber)arguments.get( 1 );
        if (LOG.isDebugEnabled())
        {
            COSNumber x = (COSNumber)arguments.get( 0 );
            LOG.debug("<TD x=\"" + x.floatValue() + "\" y=\"" + y.floatValue() + "\">");
        }
        
        ArrayList args = new ArrayList();
        args.add(new COSFloat(-1*y.floatValue()));
        context.processOperator("TL", args);
        context.processOperator("Td", arguments);

    }
}
