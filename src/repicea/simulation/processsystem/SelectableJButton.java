/*
 * This file is part of the repicea-simulation library.
 *
 * Copyright (C) 2009-2014 Mathieu Fortin for Rouge-Epicea
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed with the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * Please see the license at http://www.gnu.org/copyleft/lesser.html.
 */
package repicea.simulation.processsystem;

import java.awt.Dimension;

import javax.swing.Icon;

import repicea.gui.permissions.REpiceaGUIPermission;

@SuppressWarnings("serial")
public abstract class SelectableJButton extends AbstractPermissionProviderButton {

	protected SelectableJButton(REpiceaGUIPermission permission) {
		super(permission);
		setIcon();
		setToolTip();
		Dimension dim;
		if (getIcon() != null) {
			dim = new Dimension(getIcon().getIconWidth(), getIcon().getIconHeight());
		} else {
			dim = new Dimension(30,30);
		}
		setPreferredSize(dim);
		setMinimumSize(dim);
		setMaximumSize(dim);
		setSize(dim);
		setBorder(UISetup.ButtonDefaultBorder);
		setFocusable(false);
	}

	private void setToolTip() {
		String toolTip = UISetup.ToolTips.get(getClass().getName());
		if (toolTip != null) {
			setToolTipText(toolTip);
		}
	}

	private void setIcon() {
		String className = getClass().getName();
		Icon icon = UISetup.Icons.get(className);
		if (icon == null) {
			icon = getDefaultIcon();	// default value
		}
		setIcon(icon);
	}

	protected abstract Icon getDefaultIcon();
	
	@Override
	public void setSelected(boolean bool) {
		super.setSelected(bool);
		if (bool) {
			setBorder(UISetup.ButtonSelectedBorder);
		} else {
			setBorder(UISetup.ButtonDefaultBorder);
		}
	}


}