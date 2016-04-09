package org.szernex.java.jlastfmgrabber;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ConfigHelper {
	public static Map<String, String> getConfig(Path path) throws IOException {
		if (!Files.exists(path))
			Files.createFile(path);

		HashMap<String, String> output = new HashMap<>();
		Stream<String> lines = Files.lines(path);

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
