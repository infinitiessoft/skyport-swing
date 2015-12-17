package com.infinities.skyport.ui.testing;

import javax.swing.tree.DefaultMutableTreeNode;

public class DisabledNode extends DefaultMutableTreeNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean enabled;

	public DisabledNode() {
		this(null, true, true);
	}

	public DisabledNode(Object userObject) {
		this(userObject, true, true);
	}

	public DisabledNode(Object userObject, boolean allowsChildren) {
		this(userObject, true, true);
	}

	public DisabledNode(Object userObject, boolean allowsChildren,
			boolean enabled) {
		super(userObject, allowsChildren);
		this.enabled = enabled;
	}

	@Override
	public int getChildCount() {
		if (enabled) {
			return super.getChildCount();
		} else {
			return 0;
		}
	}

	@Override
	public boolean isLeaf() {
		return (super.getChildCount() == 0);
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

}
