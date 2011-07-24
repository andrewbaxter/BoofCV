/*
 * Copyright 2011 Peter Abeles
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package gecv.gui.binary;

import gecv.struct.image.ImageSInt32;
import gecv.struct.image.ImageUInt8;
import sun.awt.image.ByteInterleavedRaster;
import sun.awt.image.IntegerInterleavedRaster;

import java.awt.image.BufferedImage;

/**
 * @author Peter Abeles
 */
public class VisualizeBinaryData {

	public static BufferedImage renderLabeled( ImageSInt32 labelImage , BufferedImage out , int colors[] ) {

		if( out == null ) {
			out = new BufferedImage(labelImage.getWidth(),labelImage.getHeight(),BufferedImage.TYPE_INT_RGB);
		}

		if( out.getRaster() instanceof IntegerInterleavedRaster) {
			IntegerInterleavedRaster raster = (IntegerInterleavedRaster)out.getRaster();

			int rasterIndex = 0;
			int data[] = raster.getDataStorage();

			int w = labelImage.getWidth();
			int h = labelImage.getHeight();


			for( int y = 0; y < h; y++ ) {
				int indexSrc = labelImage.startIndex + y*labelImage.stride;
				for( int x = 0; x < w; x++ ) {
					data[rasterIndex++] = colors[labelImage.data[indexSrc++]];
				}
			}
		} else {
			throw new RuntimeException("Raster not supported yet");
		}
		return out;
	}

	public static BufferedImage renderBinary( ImageUInt8 binaryImage , BufferedImage out ) {

		if( out == null ) {
			out = new BufferedImage(binaryImage.getWidth(),binaryImage.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
		}

		if( out.getRaster() instanceof ByteInterleavedRaster ) {
			ByteInterleavedRaster raster = (ByteInterleavedRaster)out.getRaster();

			int rasterIndex = 0;
			byte data[] = raster.getDataStorage();

			int w = binaryImage.getWidth();
			int h = binaryImage.getHeight();


			for( int y = 0; y < h; y++ ) {
				int indexSrc = binaryImage.startIndex + y*binaryImage.stride;
				for( int x = 0; x < w; x++ ) {
					data[rasterIndex++] = binaryImage.data[indexSrc++] > 0 ? (byte)255 : (byte)0;
				}
			}
		} else {
			throw new RuntimeException("Raster not supported yet");
		}
		return out;
	}
}
