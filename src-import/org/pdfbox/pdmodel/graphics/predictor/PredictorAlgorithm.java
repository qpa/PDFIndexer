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
package org.pdfbox.pdmodel.graphics.predictor;

import java.util.Random;

/**
 * Implements different PNG predictor algorithms that is used in PDF files
 * 
 * @author xylifyx@yahoo.co.uk
 * @see http://www.w3.org/TR/PNG-Filters.html
 */
public abstract class PredictorAlgorithm {
	private int width;

	private int height;

	private int bpp;

	/**
	 * check that buffer sizes matches width,height,bpp. This implementation is
	 * used by most of the filters, but not Uptimum.
	 * 
	 * @param src
	 * @param dest
	 * @param width
	 * @param height
	 * @param bpp
	 */
	public void checkBufsiz(byte[] src, byte[] dest) {
		if (src.length != dest.length) {
			throw new IllegalArgumentException("src.length != dest.length");
		}
		if (src.length != getWidth() * getHeight() * getBpp()) {
			throw new IllegalArgumentException(
					"src.length != width * height * bpp");
		}
	}

	/**
	 * encode line of pixel data in src from src_offset and width*bpp bytes
	 * forward, put the decoded bytes into dest
	 * 
	 * @param src
	 *            raw image data
	 * @param dest
	 *            encoded data
	 * @param y
	 *            the line in question
	 * @param src_dy
	 *            byte offset between lines
	 * @param src_offset
	 *            beginning of line data
	 * @param dest_dy
	 *            byte offset between lines
	 * @param dest_offset
	 *            beginning of line data
	 */
	public abstract void encodeLine(byte[] src, byte[] dest, int src_dy,
			int src_offset, int dest_dy, int dest_offset);

	/**
	 * decode line of pixel data in src from src_offset and width*bpp bytes
	 * forward, put the decoded bytes into dest
	 * 
	 * @param src
	 *            encoded image data
	 * @param dest
	 *            raw data
	 * @param y
	 *            the line in question
	 * @param src_dy
	 *            byte offset between lines
	 * @param src_offset
	 *            beginning of line data
	 * @param dest_dy
	 *            byte offset between lines
	 * @param dest_offset
	 *            beginning of line data
	 */
	public abstract void decodeLine(byte[] src, byte[] dest, int src_dy,
			int src_offset, int dest_dy, int dest_offset);

	public static void main(String[] args) {
		Random rnd = new Random();
		int width = 5;
		int height = 5;
		int bpp = 3;
		byte raw[] = new byte[width * height * bpp];
		rnd.nextBytes(raw);
		System.out.println("raw:   ");
		dump(raw);
		for (int i = 10; i < 15; i++) {
			byte decoded[] = new byte[width * height * bpp];
			byte encoded[] = new byte[width * height * bpp];

			PredictorAlgorithm filter = PredictorAlgorithm.getFilter(i);
			filter.setWidth(width);
			filter.setHeight(height);
			filter.setBpp(bpp);
			filter.encode(raw, encoded);
			filter.decode(encoded, decoded);
			System.out.println(filter.getClass().getName());
			dump(decoded);
		}
	}

	public int leftPixel(byte[] buf, int offset, int dy, int x) {
		return x >= getBpp() ? buf[offset + x - getBpp()] : 0;
	}

	public int abovePixel(byte[] buf, int offset, int dy, int x) {
		return offset >= dy ? buf[offset + x - dy] : 0;
	}

	public int aboveLeftPixel(byte[] buf, int offset, int dy, int x) {
		return offset >= dy && x >= getBpp() ? buf[offset + x - dy - getBpp()]
				: 0;
	}
	
	/**
	 * @param raw
	 */
	private static void dump(byte[] raw) {
		for (int i = 0; i < raw.length; i++)
			System.out.print(raw[i] + " ");
		System.out.println();
	}

	/**
	 * @return Returns the bpp.
	 */
	public int getBpp() {
		return bpp;
	}

	/**
	 * @param bpp
	 *            The bpp to set.
	 */
	public void setBpp(int bpp) {
		this.bpp = bpp;
	}

	/**
	 * @return Returns the height.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            The height to set.
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return Returns the width.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            The width to set.
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	

	/**
	 * encode a byte array full of image data using the filter that this object
	 * implements
	 * 
	 * @param src
	 *            buffer
	 * @param dest
	 *            buffer
	 */
	public void encode(byte[] src, byte[] dest) {
		checkBufsiz(dest, src);
		int dy = getWidth()*getBpp();
		for (int y = 0; y < height; y++) {
			int yoffset = y * dy;
			encodeLine(src, dest, dy, yoffset, dy, yoffset);
		}
	}

	/**
	 * decode a byte array full of image data using the filter that this object
	 * implements
	 * 
	 * @param src
	 *            buffer
	 * @param dest
	 *            buffer
	 */
	public void decode(byte[] src, byte[] dest) {
		checkBufsiz(src, dest);
		int dy = width * bpp;
		for (int y = 0; y < height; y++) {
			int yoffset = y * dy;
			decodeLine(src, dest, dy, yoffset, dy, yoffset);
		}
	}

	/**
	 * @param predictor
	 *            <ul>
	 *            <li>1 No prediction (the default value)
	 *            <li>2 TIFF Predictor 2
	 *            <li>10 PNG prediction (on encoding, PNG None on all rows)
	 *            <li>11 PNG prediction (on encoding, PNG Sub on all rows)
	 *            <li>12 PNG prediction (on encoding, PNG Up on all rows)
	 *            <li>13 PNG prediction (on encoding, PNG Average on all rows)
	 *            <li>14 PNG prediction (on encoding, PNG Paeth on all rows)
	 *            <li>15 PNG prediction (on encoding, PNG optimum)
	 *            </ul>
	 * 
	 * @return
	 */
	public static PredictorAlgorithm getFilter(int predictor) {
		PredictorAlgorithm filter;
		switch (predictor) {
		case 10:
			filter = new None();
			break;
		case 11:
			filter = new Sub();
			break;
		case 12:
			filter = new Up();
			break;
		case 13:
			filter = new Average();
			break;
		case 14:
			filter = new Paeth();
			break;
		case 15:
			filter = new Uptimum();
			break;
		default:
			filter = new None();
		}
		return filter;
	}
}
