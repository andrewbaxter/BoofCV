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

package gecv.alg.filter.derivative;

import gecv.abst.filter.FilterImageInterface;
import gecv.abst.filter.FilterSequence;
import gecv.abst.filter.convolve.FactoryConvolve;
import gecv.core.image.border.BorderType;
import gecv.struct.convolve.Kernel1D;
import gecv.struct.convolve.Kernel2D;
import gecv.struct.image.ImageBase;
import gecv.testing.GecvTesting;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Compares a specialized image derivative function to the equivalent convolution.
 * Designed to test functions from {@link HessianFromGradient}.
 *
 * @author Peter Abeles
 */
@SuppressWarnings({"unchecked"})
public class CompareHessianToConvolution {

	Method m;
	FilterImageInterface outputFilters[] = new FilterImageInterface[2];

	Class<ImageBase> inputType;

	boolean processBorder;
	int borderSize = 0;

	public void setTarget( Method m )  {
		this.m = m;
		Class<?> []param = m.getParameterTypes();

		inputType = (Class<ImageBase>)param[0];
	}

	public void setKernel( int which , Kernel1D horizontal , Kernel1D vertical ) {
		FilterImageInterface<?,?> f1 = FactoryConvolve.convolve(horizontal,inputType,inputType, BorderType.EXTENDED,true);
		FilterImageInterface<?,?> f2 = FactoryConvolve.convolve(vertical,inputType,inputType, BorderType.EXTENDED,false);

		outputFilters[which] = new FilterSequence(f1,f2);

		if( borderSize < horizontal.getRadius() )
			borderSize = horizontal.getRadius();
		if( borderSize < vertical.getRadius() )
			borderSize = vertical.getRadius();
	}

	public void setKernel( int which , Kernel1D kernel , boolean isHorizontal) {
		outputFilters[which] =
				FactoryConvolve.convolve(kernel,inputType,inputType, BorderType.EXTENDED,isHorizontal);
		if( borderSize < kernel.getRadius() )
			borderSize = kernel.getRadius();
	}

	public void setKernel( int which , Kernel2D kernel ) {
		outputFilters[which] =
				FactoryConvolve.convolve(kernel,inputType,inputType, BorderType.EXTENDED);
		if( borderSize < kernel.getRadius() )
			borderSize = kernel.getRadius();
	}
	public void compare( ImageBase ...images)  {
		compare(false,images);
		compare(true,images);
	}

	public void compare( boolean processBorder , ImageBase ...images)  {
		this.processBorder = processBorder;
		innerCompare(images);

		ImageBase subOut[] = new ImageBase[ images.length ];
		for( int i = 0; i < images.length; i++ )
			subOut[i] = GecvTesting.createSubImageOf(images[i]);
		innerCompare(subOut);
	}



	protected void innerCompare( ImageBase ...images)  {
		Class<?> []param = m.getParameterTypes();

		if( images.length != 5 )
			throw new RuntimeException("Unexpected number of outputImages passed in: "+images.length);

		int width = images[0].width;
		int height = images[0].height;

		// now compute the second derivative using provided convolution filters
		ImageBase expectedOutput[] = new ImageBase[3];
		for( int i = 0; i < expectedOutput.length; i++ ) {
			expectedOutput[i] = images[0]._createNew(width,height);
		}
		outputFilters[0].process(images[0],expectedOutput[0]);
		outputFilters[1].process(images[1],expectedOutput[1]);
		outputFilters[1].process(images[0],expectedOutput[2]);

		// compute results from the test function
		Object testInputs[] = new Object[ param.length ];
		for( int i = 0; i < 5; i++ ) {
			testInputs[i] = images[i];
		}
		testInputs[5] = processBorder;

		try {
			m.invoke(null,testInputs);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		// sanity check.  The derivatives should be the same

		// compare the results
		for( int i = 0; i < expectedOutput.length; i++ ) {
			int border = processBorder ? 0 : borderSize;
			GecvTesting.assertEqualsGeneric(expectedOutput[i],images[i+2],0,1e-4f,border);

			if( !processBorder )
				GecvTesting.checkBorderZero(images[i+2],border);
		}
	}

}