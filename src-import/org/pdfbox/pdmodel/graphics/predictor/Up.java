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

/**
 * <code>Up(i,j) = Raw(i,j) - Raw(i,j-1)</code>
 * 
 * <code>Raw(i,j) = Up(i,j) + Raw(i,j-1)</code>
 */
public class Up extends PredictorAlgorithm {
	/**
	 * @see org.pdfbox.pdmodel.graphics.predictor.PredictorAlgorithm#encodeLine(byte[], byte[], int, int, int, int)
	 */
	public void encodeLine(byte[] src, byte[] dest, int src_dy, int src_offset,
			int dest_dy, int dest_offset) {
		int bpl = getWidth()*getBpp();
		// case: y = 0;
		if (src_offset - src_dy < 0) {
			if (0 < getHeight()) {
				for (int x = 0; x < bpl; x++) {
					dest[dest_offset + x] = src[src_offset + x];
				}
			}
		} else {
			for (int x = 0; x < bpl; x++) {
				dest[dest_offset + x] = (byte) (src[src_offset + x] - src[src_offset
						+ x - src_dy]);
			}
		}
	}

	/**
	 * @see org.pdfbox.pdmodel.graphics.predictor.PredictorAlgorithm#decodeLine(byte[], byte[], int, int, int, int)
	 */
	public void decodeLine(byte[] src, byte[] dest, int src_dy, int src_offset,
			int dest_dy, int dest_offset) {
		// case: y = 0;
		int bpl = getWidth()*getBpp();
		if (dest_offset - dest_dy < 0) {
			if (0 < getHeight()) {
				for (int x = 0; x < bpl; x++) {
					dest[dest_offset + x] = src[src_offset + x];
				}
			}
		} else {
			for (int x = 0; x < bpl; x++) {
				dest[dest_offset + x] = (byte) (src[src_offset + x] + dest[dest_offset
						+ x - dest_dy]);
			}
		}
	}
}
