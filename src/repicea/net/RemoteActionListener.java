/*
 * This file is part of the repicea-util library.
 *
 * Copyright (C) 2009-2012 Mathieu Fortin for Rouge Epicea.
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
package repicea.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Deprecated
public class RemoteActionListener extends RemoteListener implements	ActionListener {

	private static final long serialVersionUID = 20111227L;

	
	protected RemoteActionListener(int remoteOwnerHashCode,	Class<?> remoteOwnerClass) {
		super(remoteOwnerHashCode, remoteOwnerClass);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		sendEventToOriginalListener(evt);
	}

}
