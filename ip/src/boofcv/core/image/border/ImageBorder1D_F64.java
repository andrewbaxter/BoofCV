/*
 * Copyright (c) 2011, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://www.boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.core.image.border;

import boofcv.struct.image.ImageFloat64;


/**
 * @author Peter Abeles
 */
public class ImageBorder1D_F64 extends ImageBorder_F64 {
	BorderIndex1D rowWrap;
	BorderIndex1D colWrap;

	public ImageBorder1D_F64(Class<?> type ) {
		try {
			this.rowWrap = (BorderIndex1D)type.newInstance();
			this.colWrap = (BorderIndex1D)type.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public ImageBorder1D_F64(BorderIndex1D rowWrap, BorderIndex1D colWrap) {
		this.rowWrap = rowWrap;
		this.colWrap = colWrap;
	}

	public BorderIndex1D getRowWrap() {
		return rowWrap;
	}

	public BorderIndex1D getColWrap() {
		return colWrap;
	}

	@Override
	public void setImage( ImageFloat64 image ) {
		super.setImage(image);
		colWrap.setLength(image.width);
		rowWrap.setLength(image.height);
	}

	@Override
	public double getOutside(int x, int y) {
		return image.get( colWrap.getIndex(x) , rowWrap.getIndex(y) );
	}

	@Override
	public void setOutside(int x, int y, double val) {
		image.set(colWrap.getIndex(x) , rowWrap.getIndex(y),val);
	}
}