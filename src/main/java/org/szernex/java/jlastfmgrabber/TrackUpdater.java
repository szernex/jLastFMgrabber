package org.szernex.java.jlastfmgrabber;

import de.umass.lastfm.PaginatedResult;
import de.umass.lastfm.Track;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TrackUpdater {
	private Track previousTrack;
	private String nowPlaying = "";

	public TrackUpdater() {

	}

	public String getNowPlaying() {
		return nowPlaying;
	}

	public boolean updateNowPlaying(PaginatedResult<Track> result, Path file, String format) {
		Track current_track = getCurrentTrack(result);

		if (previousTrack == null && current_track == null)
			return false;

		if (compareTracks(current_track, previousTrack))
			return false;

		if (format == null)
			format = "$artist - $track";

		String output;

		if (current_track == null)
			output = "";
		else
			output = formatTrack(format, current_track);

		System.out.println("- " + output);
		nowPlaying = output;

		try (BufferedWriter writer = Files.newBufferedWriter(file)) {
			writer.write(output);
			previousTrack = current_track;

			return true;
		} catch (IOException ex) {
			System.err.println("Error writing to file: " + ex.getMessage());
			ex.printStackTrace();
		}

		return false;
	}

	private boolean compareTracks(Track a, Track b) {
		if (a == null || b == null)
			return false;

		String track_a = String.format("%s %s %s", a.getArtist(), a.getName(), a.getAlbum()).toLowerCase();
		String track_b = String.format("%s %s %s", b.getArtist(), b.getName(), b.getAlbum()).toLowerCase();

		return (track_a.equals(track_b));
	}

	private Track getCurrentTrack(PaginatedResult<Track> result) {
		if (result == null)
			return null;

		for (Track track : result) {
			if (track.isNowPlaying())
				return track;
		}

		return null;
	}

	private String formatTrack(String format, Track track) {
		if (track == null)
			return format;

		String output = format;

		output = output.replaceAll("\\$artist", track.getArtist());
		output = output.replaceAll("\\$track", track.getName());
		output = output.replaceAll("\\$album", track.getAlbum());

		return output;
	}
}
