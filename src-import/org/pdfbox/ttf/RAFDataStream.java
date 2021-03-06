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
package org.pdfbox.ttf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.pdfbox.cos.COSStream;
import org.pdfbox.pdmodel.common.PDStream;

/**
 * An implementation of the TTFDataStream that goes against a RAF.
 * 
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.4 $
 */
public class RAFDataStream extends TTFDataStream 
{
    private RandomAccessFile raf = null;
    /**
     * Constructor.
     * 
     * @param name The raf file.
     * @param mode The mode to open the RAF.
     * 
     * @throws FileNotFoundException If there is a problem creating the RAF.
     * 
     * @see RandomAccessFile#RandomAccessFile( String, String )
     */
    public RAFDataStream(String name, String mode) throws FileNotFoundException
    {
        raf = new RandomAccessFile( name, mode );
    }
    
    /**
     * Constructor.
     * 
     * @param file The raf file.
     * @param mode The mode to open the RAF.
     * 
     * @throws FileNotFoundException If there is a problem creating the RAF.
     * 
     * @see RandomAccessFile#RandomAccessFile( File, String )
     */
    public RAFDataStream(File file, String mode) throws FileNotFoundException
    {
        raf = new RandomAccessFile( file, mode );
    }
    
    /**
     * Read an signed short.
     * 
     * @return An signed short.
     * @throws IOException If there is an error reading the data.
     */
    public short readSignedShort() throws IOException
    {
        return raf.readShort();
    }
    
    /**
     * Get the current position in the stream.
     * @return The current position in the stream.
     * @throws IOException If an error occurs while reading the stream.
     */
    public long getCurrentPosition() throws IOException
    {
        return raf.getFilePointer();
    }
    
    /**
     * Get a COSStream from this TTFDataStream
     * This permit to pass the data read from an
     * external source to the COSObjects to keep
     * a certain persistence layer between specialized
     * objects like the TTF package and the pdmodel package.
     *
     * Created by Pascal Allain
     * Vertical7 Inc.
     *
     * @return COSStream describing this stream (unfiletred)
     */
    public PDStream getPDStream()
    {
        COSStream retval = null;
        
        retval = new COSStream(raf);
        
        return new PDStream( retval );
    }
    
    /**
     * Close the underlying resources.
     * 
     * @throws IOException If there is an error closing the resources.
     */
    public void close() throws IOException
    {
        raf.close();
        raf = null;
    }
    
    /**
     * Read an unsigned byte.
     * @return An unsigned byte.
     * @throws IOException If there is an error reading the data.
     */
    public int read() throws IOException
    {
        return raf.read();
    }
    
    /**
     * Read an unsigned short.
     * 
     * @return An unsigned short.
     * @throws IOException If there is an error reading the data.
     */
    public int readUnsignedShort() throws IOException
    {
        return raf.readUnsignedShort();
    }
    
    /**
     * Read an unsigned byte.
     * @return An unsigned byte.
     * @throws IOException If there is an error reading the data.
     */
    public long readLong() throws IOException
    {
        return raf.readLong();
    }
    
    /**
     * Seek into the datasource.
     * 
     * @param pos The position to seek to.
     * @throws IOException If there is an error seeking to that position.
     */
    public void seek(long pos) throws IOException
    {
        raf.seek( pos );
    }
    
    /**
     * @see java.io.InputStream#read( byte[], int, int )
     * 
     * @param b The buffer to write to.
     * @param off The offset into the buffer.
     * @param len The length into the buffer.
     * 
     * @return The number of bytes read.
     * 
     * @throws IOException If there is an error reading from the stream.
     */
    public int read(byte[] b,
            int off,
            int len)
     throws IOException
     {
        return raf.read(b,off,len);
     }
}
