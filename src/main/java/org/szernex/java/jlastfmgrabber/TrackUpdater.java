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
