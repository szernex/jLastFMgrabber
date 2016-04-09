package org.szernex.java.jlastfmgrabber;

import de.umass.lastfm.PaginatedResult;
import de.umass.lastfm.Track;
import de.umass.lastfm.User;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

public class Main {
	public static void main(String[] args) throws IOException {
		HashMap<String, String> config = new HashMap<>();

		config.putAll(ConfigHelper.getConfig(Paths.get(".", R.CONFIG_FILE)));

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

		TrackUpdater updater = new TrackUpdater();
		PaginatedResult<Track> track_result = User.getRecentTracks(user, 1, 10, api_key);
		updater.updateNowPlaying(track_result, Paths.get(output_file), format);
	}
}
