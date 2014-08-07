/*
 * Copyright 2013 The British Library/SCAPE Project Consortium
 * Author: William Palmer (William.Palmer@bl.uk)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.exlibris.dps.repository.plugin.riskExtractor.drmlint;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Wrapper for Calibre - note that there are no static methods here (yet?)
 * @author wpalmer
 *
 */
public class CalibreWrapper {

	private static final String CALIBRE_DIR_WINDOWS = "c:/bin/calibre/calibre2/";
	private static final String CALIBRE_CONVERT_WINDOWS = CALIBRE_DIR_WINDOWS+"ebook-convert.exe";
	private static final String CALIBRE_DIR_LINUX = "/usr/bin/";
	private static final String CALIBRE_CONVERT_LINUX = CALIBRE_DIR_LINUX+"ebook-convert";

	private static String gVersion = null;
	private static List<String> CALIBRE_CONVERT = null;

	/**
	 * Create a new CalibreWrapper
	 */
	private CalibreWrapper() {

	}

	private static void setupCalibre() {
		String os = System.getProperty("os.name").toLowerCase();
		if(os.contains("windows")) {
			CALIBRE_CONVERT = Arrays.asList(CALIBRE_CONVERT_WINDOWS);
			return;
		}
		if(os.contains("linux")) {
			if(new File(CALIBRE_CONVERT_LINUX).exists()) {
				CALIBRE_CONVERT = Arrays.asList(CALIBRE_CONVERT_LINUX);
			} else {
				//panic
				System.err.println("Calibre not installed? (not at: "+CALIBRE_CONVERT_LINUX);
				System.err.println("try \"sudo apt-get install calibre\" or equivalent");
			}
			return;
		}
		if(os.contains("mac")) {
			System.err.println("Not yet set up for Mac");
			//panic
			return;
		}
	}

	private static void getVer() {
		if(null==CALIBRE_CONVERT) setupCalibre();
		//we need to redirect stderr to stdout otherwise bad things happen if drm is detected and stderr is written to first
		ToolRunner runner = new ToolRunner(true);
		try {
			List<String> commandLine = new ArrayList<String>();
			commandLine.addAll(CALIBRE_CONVERT);
			commandLine.add("--version");
			runner.runCommand(commandLine);
			String ver = runner.getStdout().readLine();
			gVersion = ver;//ver.substring(ver.indexOf("(")+1, ver.indexOf(")"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Get the version of Calibre
	 * @return version string from Calibre
	 */
	public static String getVersion() {
		if(null==gVersion) getVer();
		return gVersion;
	}

	/**
	 * Convert an ebook to another format
	 * @param pOriginal original file
	 * @param pType type to convert to (e.g. "epub" or "mobi")
	 * @return File for converted ebook (or null if error)
	 */
	public static File convertEbook(File pOriginal, String pType) {
		if(null==CALIBRE_CONVERT) setupCalibre();
		//we need to redirect stderr to stdout otherwise bad things happen if drm is detected and stderr is written to first
		ToolRunner runner = new ToolRunner(true);
		try {
			File newEbook = File.createTempFile(pOriginal.getName()+"-", "."+pType);
			List<String> commandLine = new ArrayList<String>();
			commandLine.addAll(CALIBRE_CONVERT);
			commandLine.add(pOriginal.getAbsolutePath());
			commandLine.add(newEbook.getAbsolutePath());
			int exitCode = runner.runCommand(commandLine);
			if(exitCode!=0) return null;
//			System.out.println("Stdout:");
//			BufferedReader o = runner.getStdout();
//			while(o.ready()) System.out.println(o.readLine());
			return newEbook;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Check to see if the file is valid by trying to convert to text
	 * @param pFile file to check
	 * @return true if valid (i.e. can be converted to text)
	 */
	public static boolean isValid(File pFile) {
		if(null==CALIBRE_CONVERT) setupCalibre();

		boolean ret = false;

		//we need to redirect stderr to stdout otherwise bad things happen if drm is detected and stderr is written to first
		ToolRunner runner = new ToolRunner(true);
		try {
			File newEbook = File.createTempFile(pFile.getName()+"-", ".txt");
			newEbook.deleteOnExit();
			List<String> commandLine = new ArrayList<String>();
			commandLine.addAll(CALIBRE_CONVERT);
			commandLine.add(pFile.getAbsolutePath());
			commandLine.add(newEbook.getAbsolutePath());
			int exitCode = runner.runCommand(commandLine);
			if(exitCode!=0) return false;
			BufferedReader o = runner.getStdout();
			while(o.ready()) {
				String line = o.readLine();
				//System.out.println(line);
				//this doesn't seem to work 100% - down to saving of buffer?
				if(line.contains("Output saved to")) {
					ret = true;
				}
				if(line.contains("TXT output written to")) {
					ret = true;
				}
//				if(line.toLowerCase().contains("drmerror")) {
//					return false;
//				}
			}

			return ret;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * Main method
	 * @param args command line arguments
	 */
	public static void main(String[] args) {

		System.out.println("Version: "+CalibreWrapper.getVersion());
		File testFile = new File("test.epub");
		boolean valid = CalibreWrapper.isValid(testFile);
		System.out.println("Valid: "+valid);
		File newFile = CalibreWrapper.convertEbook(testFile, "epub");
		if(newFile!=null) {
		System.out.println("Converted file: "+newFile.getAbsolutePath()+", size: "+newFile.length());
		valid = CalibreWrapper.isValid(newFile);
		System.out.println("Valid: "+valid);
		}

	}

}
