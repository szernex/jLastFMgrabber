/*
The MIT License (MIT)

Copyright (c) 2016 Szernex

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package org.szernex.java.jlastfmgrabber;

import de.umass.lastfm.PaginatedResult;
import de.umass.lastfm.Track;
import de.umass.lastfm.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class TrayIconJLFMG extends TimerTask implements ActionListener {
	private MenuItem itemCurrentTrack;
	private SystemTray systemTray;
	private TrayIcon trayIcon;

	private TrackUpdater trackUpdater = new TrackUpdater();
	private HashMap<String, String> config = new HashMap<>();

	private Timer timer;

	public TrayIconJLFMG() {
		if (!loadConfig())
			exit(1);

		if (!SystemTray.isSupported()) {
			System.err.println("SystemTray is not supported");
			exit(1);
		}

		PopupMenu popup = new PopupMenu();
		trayIcon = new TrayIcon(createImage("images/tray.png", "tray icon"));
		trayIcon.setImageAutoSize(true);
		systemTray = SystemTray.getSystemTray();

		itemCurrentTrack = new MenuItem("");
		MenuItem item_refresh = new MenuItem("Refresh");
		MenuItem item_reload = new MenuItem("Reload config");
		MenuItem item_exit = new MenuItem("Exit");

		popup.add(itemCurrentTrack);
		popup.add(item_refresh);
		popup.add(item_reload);
		popup.addSeparator();
		popup.add(item_exit);

		trayIcon.setPopupMenu(popup);

		try {
			systemTray.add(trayIcon);
		} catch (AWTException ex) {
			System.err.println("Could not add TrayIcon: " + ex.getMessage());
			ex.printStackTrace();
			exit(1);
		}

		item_refresh.addActionListener(this);
		item_reload.addActionListener(this);
		item_exit.addActionListener(this);

		timer = new Timer();
		timer.schedule(this, 0, R.REFRESH_DELAY);
	}

	@Override
	public void run() {
		refresh();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		MenuItem item = (MenuItem) e.getSource();
		String label = item.getLabel().toLowerCase();

		switch (label) {
			case "reload config":
				loadConfig();
				refresh();
				break;
			case "refresh":
				refresh();
				break;
			case "exit":
				exit(0);
				break;
		}
	}

	private Image createImage(String path, String description) {
		URL image_url = ClassLoader.getSystemResource(path);

		System.out.println(image_url);

		if (image_url == null) {
			System.err.println("Resource not found: " + path);
			return null;
		} else {
			return (new ImageIcon(image_url, description)).getImage();
		}
	}

	private boolean loadConfig() {
		try {
			config.putAll(ConfigHelper.loadConfig(Paths.get(R.CONFIG_FILE)));

			return true;
		} catch (IOException ex) {
			System.err.println("Could not load config: " + ex.getMessage());
			ex.printStackTrace();
		}

		return false;
	}

	private void refresh() {
		String user = config.get(R.Config.USER);
		String api_key = config.get(R.Config.API_KEY);
		String format = config.get(R.Config.FORMAT);
		String output_file = config.get(R.Config.OUTPUT_FILE);

		if (user == null || api_key == null) {
			System.err.println("Config 'user' or 'api_key' not set");
			return;
		}

		if (output_file == null) {
			System.err.println("Config 'output_file' not set");
			return;
		}

		PaginatedResult<Track> track_result = User.getRecentTracks(user, 1, 5, api_key);
		trackUpdater.updateNowPlaying(track_result, Paths.get(output_file), format);
		itemCurrentTrack.setLabel(trackUpdater.getNowPlaying());
		trayIcon.setToolTip(trackUpdater.getNowPlaying());
	}

	private void exit(int status) {
		timer.cancel();

		systemTray.remove(trayIcon);
		System.exit(status);
	}
}
