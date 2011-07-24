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

package gecv.alg.filter.binary;

import gecv.alg.InputSanityCheck;
import gecv.alg.filter.binary.impl.ImplBinaryBlobLabeling;
import gecv.alg.filter.binary.impl.ImplBinaryBorderOps;
import gecv.alg.filter.binary.impl.ImplBinaryInnerOps;
import gecv.alg.filter.binary.impl.ImplBinaryNaiveOps;
import gecv.struct.GrowingArrayInt;
import gecv.struct.image.ImageSInt32;
import gecv.struct.image.ImageUInt8;

/**
 * <p>
 * Contains a standard set of operations performed on binary images. A pixel has a value of false if it is equal
 * to zero or true equal to one.
 * </p>
 * <p/>
 * <p>
 * NOTE: If an element's value is not zero or one then each function's behavior is undefined.
 * </p>
 *
 * @author Peter Abeles
 */
/*
 * DESIGN NOTE: 8-bit integer images ({@link ImageUInt8}) are used instead of images composed of boolean values because
 * there is no performance advantage.  According to the virtual machines specification binary arrays are stored as
 * byte arrays with 1 representing true and 0 representing false.

 * DESIGN NOTE: Restricting input values to zero and one was tested was compared against defining true as not zero.
 * The former allowed a 2x to 3x performance boost by allowing numbers to be summed instead of compared.
 */
public class BinaryImageOps {

	/**
	 * <p>
	 * Erodes an image according to a 4-neighborhood.  Unless a pixel is connected to all its neighbors its value
	 * is set to zero.
	 * </p>
	 *
	 * @param input  Input image. Not modified.
	 * @param output If not null, the output image.  If null a new image is declared and returned.  Modified.
	 * @return Output image.
	 */
	public static ImageUInt8 erode4(ImageUInt8 input, ImageUInt8 output) {
		output = InputSanityCheck.checkDeclare(input, output);

		ImplBinaryInnerOps.erode4(input, output);
		ImplBinaryBorderOps.erode4(input, output);

		return output;
	}

	/**
	 * <p>
	 * Dilates an image according to a 4-neighborhood.  If a pixel is connected to any other pixel then its output
	 * value will be one.
	 * </p>
	 *
	 * @param input  Input image. Not modified.
	 * @param output If not null, the output image.  If null a new image is declared and returned.  Modified.
	 * @return Output image.
	 */
	public static ImageUInt8 dilate4(ImageUInt8 input, ImageUInt8 output) {
		output = InputSanityCheck.checkDeclare(input, output);

		ImplBinaryInnerOps.dilate4(input, output);
		ImplBinaryBorderOps.dilate4(input, output);

		return output;
	}

	/**
	 * <p>
	 * Binary operation which is designed to remove all pixels but ones which are on the edge of an object.
	 * The edge is defined as lying on the object and not being surrounded by a pixel along a 4-neighborhood.
	 * </p>
	 * <p/>
	 * <p>
	 * NOTE: There are many ways to define an edge, this is just one of them.
	 * </p>
	 *
	 * @param input  Input image. Not modified.
	 * @param output If not null, the output image.  If null a new image is declared and returned.  Modified.
	 * @return Output image.
	 */
	public static ImageUInt8 edge4(ImageUInt8 input, ImageUInt8 output) {
		output = InputSanityCheck.checkDeclare(input, output);

		ImplBinaryNaiveOps.edge4(input, output);
		ImplBinaryInnerOps.edge4(input, output);
		ImplBinaryBorderOps.edge4(input, output);

		return output;
	}

	/**
	 * <p>
	 * Erodes an image according to a 8-neighborhood.  Unless a pixel is connected to all its neighbors its value
	 * is set to zero.
	 * </p>
	 *
	 * @param input  Input image. Not modified.
	 * @param output If not null, the output image.  If null a new image is declared and returned.  Modified.
	 * @return Output image.
	 */
	public static ImageUInt8 erode8(ImageUInt8 input, ImageUInt8 output) {
		output = InputSanityCheck.checkDeclare(input, output);

		ImplBinaryInnerOps.erode8(input, output);
		ImplBinaryBorderOps.erode8(input, output);

		return output;
	}

	/**
	 * <p>
	 * Dilates an image according to a 8-neighborhood.  If a pixel is connected to any other pixel then its output
	 * value will be one.
	 * </p>
	 *
	 * @param input  Input image. Not modified.
	 * @param output If not null, the output image.  If null a new image is declared and returned.  Modified.
	 * @return Output image.
	 */
	public static ImageUInt8 dilate8(ImageUInt8 input, ImageUInt8 output) {
		output = InputSanityCheck.checkDeclare(input, output);

		ImplBinaryInnerOps.dilate8(input, output);
		ImplBinaryBorderOps.dilate8(input, output);

		return output;
	}

	/**
	 * <p>
	 * Binary operation which is designed to remove all pixels but ones which are on the edge of an object.
	 * The edge is defined as lying on the object and not being surrounded by 8 pixels.
	 * </p>
	 * <p/>
	 * <p>
	 * NOTE: There are many ways to define an edge, this is just one of them.
	 * </p>
	 *
	 * @param input  Input image. Not modified.
	 * @param output If not null, the output image.  If null a new image is declared and returned.  Modified.
	 * @return Output image.
	 */
	public static ImageUInt8 edge8(ImageUInt8 input, ImageUInt8 output) {
		output = InputSanityCheck.checkDeclare(input, output);

		ImplBinaryInnerOps.edge8(input, output);
		ImplBinaryBorderOps.edge8(input, output);

		return output;
	}

	/**
	 * Binary operation which is designed to remove small bits of spurious noise.  An 8-neighborhood is used.
	 * If a pixel is connected to less than 2 neighbors then its value zero.  If connected to more than 6 then
	 * its value is one.  Otherwise it retains its original value.
	 *
	 * @param input  Input image. Not modified.
	 * @param output If not null, the output image.  If null a new image is declared and returned.  Modified.
	 * @return Output image.
	 */
	public static ImageUInt8 removePointNoise(ImageUInt8 input, ImageUInt8 output) {
		output = InputSanityCheck.checkDeclare(input, output);

		ImplBinaryInnerOps.removePointNoise(input, output);
		ImplBinaryBorderOps.removePointNoise(input, output);

		return output;
	}

	/**
	 * <p>
	 * Connects pixels together using an 8-connect rule.  Each cluster of connected pixels (a blob)
	 * is given a unique number >= 1.
	 * </p>
	 *
	 * <p>
	 * The coexist table is used internally to store intermediate results.  It is used supplied because
	 * pre-allocating the array was shown to improve performance about 5 times.
	 * </p>
	 *
	 * @param input Binary input image.
	 * @param output The labeled blob image. Modified.
	 * @param work Used to store intermediate labeling results.  In general it is at most 3 or 4 times larger than the
	 * expected max number of blobs.  Worst case it will be (width*height)/4
	 * @return How many blobs were found.
	 */
	public static int labelBlobs8( ImageUInt8 input , ImageSInt32 output , GrowingArrayInt work )
	{
		InputSanityCheck.checkSameShape(input,output);
		work = checkDeclareMaxConnect(work);

		int numBlobs = ImplBinaryBlobLabeling.quickLabelBlobs8(input,output,work);
		ImplBinaryBlobLabeling.optimizeMaxConnect(work.data,numBlobs);
		int newNumBlobs = ImplBinaryBlobLabeling.minimizeBlobID(work.data,numBlobs);
		ImplBinaryBlobLabeling.relabelBlobs(output,work.data);

		return newNumBlobs;
	}


	/**
	 * <p>
	 * Connects pixels together using an 4-connect rule.  Each cluster of connected pixels (a blob)
	 * is given a unique number >= 1.
	 * </p>
	 *
	 * <p>
	 * The coexist table is used internally to store intermediate results.  It is used supplied because
	 * pre-allocating the array was shown to improve performance about 5 times.
	 * </p>
	 *
	 * @param input Binary input image.
	 * @param output The labeled blob image. Modified.
	 * @param work Used to store intermediate labeling results.  In general it is at most 3 or 4 times larger than the
	 * expected max number of blobs.  Worst case it will be (width*height)/2
	 * @return How many blobs were found.
	 */
	public static int labelBlobs4( ImageUInt8 input , ImageSInt32 output , GrowingArrayInt work )
	{
		InputSanityCheck.checkSameShape(input,output);
		work = checkDeclareMaxConnect(work);

		int numBlobs = ImplBinaryBlobLabeling.quickLabelBlobs4(input,output,work);
		ImplBinaryBlobLabeling.optimizeMaxConnect(work.data,numBlobs);
		int newNumBlobs = ImplBinaryBlobLabeling.minimizeBlobID(work.data,numBlobs);
		ImplBinaryBlobLabeling.relabelBlobs(output,work.data);

		return newNumBlobs;
	}

	/**
	 * Used to change the labels in a labeled binary image.
	 *
	 * @param image Labeled binary image.
	 * @param labels Look up table where the indexes are the current label and the value are its new value.
	 */
	public static void relabel( ImageSInt32 image , int labels[] ) {
		ImplBinaryBlobLabeling.relabelBlobs(image,labels);
	}

	/**
	 * Converts a labeled image into a binary image by setting any non-zero value to one.
	 *
	 * @param labelImage Input image. Not modified.
	 * @param binaryImage Output image. Modified.
	 * @return The binary image.
	 */
	public static ImageUInt8 labelToBinary( ImageSInt32 labelImage , ImageUInt8 binaryImage ) {
		binaryImage = InputSanityCheck.checkDeclare(labelImage, binaryImage, ImageUInt8.class);

		for( int y = 0; y < labelImage.height; y++ ) {

			int indexIn = labelImage.startIndex + y*labelImage.stride;
			int indexOut = binaryImage.startIndex + y*binaryImage.stride;

			int end = indexIn + labelImage.width;

			for( ; indexIn < end; indexIn++, indexOut++ ) {
				if( 0 == labelImage.data[indexIn] ) {
					binaryImage.data[indexOut] = 0;
				} else {
					binaryImage.data[indexOut] = 1;
				}
			}
		}

		return binaryImage;
	}

	/**
	 * Only converts the specified blobs over into the binary image
	 *
	 * @param labelImage Input image. Not modified.
	 * @param binaryImage Output image. Modified.
	 * @param selectedBlobs Each index corresponds to a blob and specifies if it is included or not.
	 * @return The binary image.
	 */
	public static ImageUInt8 labelToBinary( ImageSInt32 labelImage , ImageUInt8 binaryImage ,
											boolean selectedBlobs[] )
	{
		binaryImage = InputSanityCheck.checkDeclare(labelImage, binaryImage, ImageUInt8.class);

		for( int y = 0; y < labelImage.height; y++ ) {

			int indexIn = labelImage.startIndex + y*labelImage.stride;
			int indexOut = binaryImage.startIndex + y*binaryImage.stride;

			int end = indexIn + labelImage.width;

			for( ; indexIn < end; indexIn++, indexOut++ ) {
				int val = labelImage.data[indexIn];
				if( 0 == val ) {
					binaryImage.data[indexOut] = 0;
				} else {
					if( selectedBlobs[val] ) {
						binaryImage.data[indexOut] = 1;
					} else {
						binaryImage.data[indexOut] = 0;
					}

				}
			}
		}

		return binaryImage;
	}

	private static GrowingArrayInt checkDeclareMaxConnect(GrowingArrayInt maxConnect) {
		if( maxConnect == null )
			maxConnect = new GrowingArrayInt(20);

		return maxConnect;
	}
}
