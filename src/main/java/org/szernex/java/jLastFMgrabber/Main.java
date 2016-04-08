package org.szernex.java.jlastfmgrabber;

import de.umass.lastfm.PaginatedResult;
import de.umass.lastfm.Track;
import de.umass.lastfm.User;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Main {
	private static final String CONFIG_FILE = "jlastfmgrabber.cfg";
	private static final String OUTPUT_FILE = "now_playing.txt";

	private static final String CFG_USER = "user";
	private static final String CFG_API_KEY = "api_key";

	public static void main(String[] args) throws IOException {
		HashMap<String, String> config = new HashMap<>();

		config.putAll(getConfig());

		String user = config.get(CFG_USER);
		String api_key = config.get(CFG_API_KEY);

		PaginatedResult<Track> track_result = User.getRecentTracks(user, 1, 1, api_key);
		Track current_track = (Track) track_result.getPageResults().toArray()[0];

		System.out.println(String.format("%s - %s - %s - %b", current_track.getArtist(), current_track.getName(), current_track.getAlbum(), current_track.isNowPlaying()));

		if (current_track.isNowPlaying()) {
			String output = String.format("%s - %s", current_track.getArtist(), current_track.getName());
			BufferedWriter writer = Files.newBufferedWriter(Paths.get(".", OUTPUT_FILE));

			System.out.println(output);
			writer.write(output);
			writer.close();
		}
	}

	private static Map<String, String> getConfig() throws IOException {
		HashMap<String, String> output = new HashMap<>();
		Stream<String> lines = Files.lines(Paths.get(".", CONFIG_FILE));

		lines
				.filter(s -> s.contains("="))
				.forEach(s -> {
					String[] parts = s.split("=");
					output.put(parts[0], parts[1]);
				});

		lines.close();

		return output;
	}
}
