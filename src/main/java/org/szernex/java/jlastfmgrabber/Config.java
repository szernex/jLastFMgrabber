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

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class Config {
	public static ConfigObject load(Path path) {
		if (!Files.exists(path)) {
			System.out.println("Creating new empty config file " + path.toString());

			try {
				Files.createFile(path);

				Gson gson = new Gson();
				ConfigObject empty_config = new ConfigObject();

				empty_config.user = "";
				empty_config.api_key = "";
				empty_config.outputs = Collections.emptyList();

				Files.write(path, gson.toJson(empty_config).getBytes());

				return empty_config;
			} catch (IOException ex) {
				System.err.println("Error creating config file " + path.toString() + ": " + ex.getMessage());
				ex.printStackTrace();

				return null;
			}
		}

		try {
			StringBuilder raw = new StringBuilder();

			Files.readAllLines(path).forEach(raw::append);

			Gson gson = new Gson();
			ConfigObject config = gson.fromJson(raw.toString(), ConfigObject.class);

			System.out.println("Config file " + path.toString() + " loaded");

			return config;
		} catch (IOException ex) {
			System.err.println("Error reading config file " + path.toString() + ": " + ex.getMessage());
			ex.printStackTrace();

			return null;
		}
	}
}

class ConfigObject {
	String user;
	String api_key;
	List<OutputObject> outputs;
}

class OutputObject {
	String file;
	String format;
}