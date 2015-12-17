package com.infinities.skyport.ui;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JList;
import javax.swing.SwingConstants;

public class ScrollableList extends JList<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public ScrollableList(Object[] data) {
		super(data);
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {

		int row = getFirstVisibleIndex();
		if (orientation == SwingConstants.VERTICAL && direction < 0 && row != -1) {
			Rectangle r = getCellBounds(row, row);
			if ((r.y == visibleRect.y) && (row != 0)) {
				Point loc = r.getLocation();
				loc.y--;
				int prevIndex = locationToIndex(loc);

				Rectangle prevR = getCellBounds(prevIndex, prevIndex);
				if (prevR == null || prevR.y >= r.y) {
					return 0;
				}
				return prevR.height;
			}

		}

		return super.getScrollableUnitIncrement(visibleRect, orientation, direction);

	}

}
