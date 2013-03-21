/**
 * Copyright (c) 2004-2005, www.pdfbox.org
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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.pdfbox.afmparser.AFMParser;
import org.pdfbox.afmtypes.FontMetric;
import org.pdfbox.encoding.AFMEncoding;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.common.PDRectangle;
import org.pdfbox.pdmodel.common.PDStream;
import org.pdfbox.pfb.PfbParser;

/**
 * This is implementation of the Type1 Font
 * with a afm and a pfb file.
 *
 * @author <a href="mailto:m.g.n@gmx.de">Michael Niedermair</a>
 * @version $Revision$
 */
public class PDType1AfmPfbFont extends PDType1Font {

    /**
     * Create a new object.
     * @param doc       The PDF document that will hold the embedded font.
     * @param afmname   The font filename.
     * @throws IOException If there is an error loading the data.
     */
    public PDType1AfmPfbFont(final PDDocument doc, final String afmname)
            throws IOException {

        super();

        InputStream afmin = new BufferedInputStream(
                new FileInputStream(afmname));
        String pfbname = afmname.replaceAll(".AFM", "").replaceAll(".afm", "")
                + ".pfb";
        InputStream pfbin = new BufferedInputStream(
                new FileInputStream(pfbname));

        load(doc, afmin, pfbin);
    }

    /**
     * Create a new object.
     * @param doc       The PDF document that will hold the embedded font.
     * @param afm       The afm input.
     * @param pfb       The pfb input.
     * @throws IOException If there is an error loading the data.
     */
    public PDType1AfmPfbFont(final PDDocument doc, final InputStream afm,
            final InputStream pfb) throws IOException {

        super();

        load(doc, afm, pfb);
    }

    /**
     * The font descriptor
     */
    private PDFontDescriptorDictionary fd;

    /**
     * the font metric
     */
    private FontMetric metric;

    /**
     * This will load a afm and pfb to be embedding into a document.
     * 
     * @param doc   The PDF document that will hold the embedded font. 
     * @param afm   The afm input.
     * @param pfb   The pfb input.
     * @throws IOException If there is an error loading the data.
     */
    private void load(final PDDocument doc, final InputStream afm,
            final InputStream pfb) throws IOException {

        fd = new PDFontDescriptorDictionary();
        setFontDescriptor(fd);

        // read the pfb 
        PfbParser pfbparser = new PfbParser(pfb);
        pfb.close();

        PDStream fontStream = new PDStream(doc, pfbparser.getInputStream(),
                false);
        fontStream.getStream().setInt("Length", pfbparser.size());
        int[] lengths = pfbparser.getLengths();
        for (int i = 0; i < lengths.length; i++) {
            fontStream.getStream().setInt("Length" + (i + 1),
                    lengths[i]);
        }
        fontStream.addCompression();
        fd.setFontFile(fontStream);

        // read the afm
        AFMParser parser = new AFMParser(afm);
        parser.parse();
        metric = parser.getResult();
        setEncoding(new AFMEncoding(metric));

        // set the values
        setBaseFont(metric.getFontName());
        fd.setFontName(metric.getFontName());
        fd.setFontFamily(metric.getFamilyName());
        fd.setNonSymbolic(true);
        fd.setFontBoundingBox(new PDRectangle(metric.getFontBBox()));
        fd.setItalicAngle(metric.getItalicAngle());
        fd.setAscent(metric.getAscender());
        fd.setDescent(metric.getDescender());
        fd.setCapHeight(metric.getCapHeight());
        fd.setXHeight(metric.getXHeight());
        fd.setAverageWidth(metric.getAverageCharacterWidth());
        fd.setCharacterSet(metric.getCharacterSet());

    }

    /**
     * @see org.pdfbox.pdmodel.font.PDSimpleFont#getFontDescriptor()
     */
    public PDFontDescriptor getFontDescriptor() throws IOException {

        return fd;
    }


}