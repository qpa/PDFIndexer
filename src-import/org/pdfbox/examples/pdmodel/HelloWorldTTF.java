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

package org.pdfbox.examples.pdmodel;

import java.io.IOException;

import org.pdfbox.exceptions.COSVisitorException;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.edit.PDPageContentStream;
import org.pdfbox.pdmodel.font.PDFont;
import org.pdfbox.pdmodel.font.PDTrueTypeFont;

/**
 * This is an example that creates a simple document
 * with a ttf-font.
 *
 * @author <a href="mailto:m.g.n@gmx.de">Michael Niedermair</a>
 * @version $Revision$
 */
public class HelloWorldTTF {

    /**
     * create the second sample document from the PDF file format specification.
     *
     * @param file      The file to write the PDF to.
     * @param message   The message to write in the file.
     * @param fontfile  The ttf-font file.
     *
     * @throws IOException If there is an error writing the data.
     * @throws COSVisitorException If there is an error writing the PDF.
     */
    public void doIt(final String file, final String message,
            final String fontfile) throws IOException, COSVisitorException {

        // the document
        PDDocument doc = null;
        try {
            doc = new PDDocument();

            PDPage page = new PDPage();
            doc.addPage(page);
            PDFont font = PDTrueTypeFont.loadTTF(doc, fontfile);

            PDPageContentStream contentStream = new PDPageContentStream(doc,
                    page);
            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.moveTextPositionByAmount(100, 700);
            contentStream.drawString(message);
            contentStream.endText();
            contentStream.close();
            doc.save(file);
            System.out.println(file + " created!");
        } finally {
            if (doc != null) {
                doc.close();
            }
        }
    }

    /**
     * This will create a hello world PDF document
     * with a ttf-font.
     * <br />
     * see usage() for commandline
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {

        HelloWorldTTF app = new HelloWorldTTF();
        try {
            if (args.length != 3) {
                app.usage();
            } else {
                app.doIt(args[0], args[1], args[2]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This will print out a message telling how to use this example.
     */
    private void usage() {

        System.err.println("usage: " + this.getClass().getName()
                + " <output-file> <Message> <ttf-file>");
    }
}