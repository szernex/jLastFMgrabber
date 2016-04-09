package org.szernex.java.jlastfmgrabber;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;

public class TrayIconJLFMG implements ActionListener, ItemListener {

	public TrayIconJLFMG() {
		if (!SystemTray.isSupported()) {
			System.err.println("SystemTray is not supported");
			System.exit(1);
		}

		final PopupMenu popup = new PopupMenu();
		final TrayIcon tray_icon = new TrayIcon(createImage("images/tray.png", "tray icon"));
		final SystemTray tray = SystemTray.getSystemTray();


	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

	@Override
	public void itemStateChanged(ItemEvent e) {

	}

	protected static Image createImage(String path, String description) {
		URL imageURL = TrayIconJLFMG.class.getResource(path);

		if (imageURL == null) {
			System.err.println("Resource not found: " + path);
			return null;
		} else {
			return (new ImageIcon(imageURL, description)).getImage();
		}
	}
}
