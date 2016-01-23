/*
 * Copyright 2016 Agapsys Tecnologia Ltda-ME.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.agapsys.security.scanner;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class SourceDirectory {
	// CLASS SCOPE =============================================================
	/**
	 * Parses a folder
	 * @param srcDir source directory.
	 * @return security information contained in given directory.
	 * @throws ParsingException if there is a syntax error while processing the directory
	 */
	public static SecurityInfo getSecurityInfo(File srcDir) throws ParsingException {
		SourceDirectory _srcDir = new SourceDirectory(srcDir);
		Set<String> classes = _srcDir.getSecuredClasses();
		String[] classArray = classes.toArray(new String[classes.size()]);
		return new SecurityInfo(classArray);
	}
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	private final Set<String> securedClasses = new LinkedHashSet<String>();

	/**
	 * Constructor.
	 * @param srcDir source directory.
	 */
	private SourceDirectory(File srcDir) {
		parseDirectory(srcDir, false);
	}

	/**
	 * Parses a directory
	 * @param directory directory to be analyzed
	 * @param recursive flag indicating a recursive call
	 * @throws ParsingException if there was an error while processing the directory.
	 */
	private void parseDirectory(File directory, boolean recursive) throws ParsingException {
		if (!recursive) {
			securedClasses.clear();
		}

		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				parseDirectory(file, true);
			} else {
				parseFile(file);
			}
		}
	}

	/**
	 * Parses a file
	 * @param file file to be parsed
	 * @throws ParsingException if there was an error while processing the file.
	 */
	private void parseFile(File file) throws ParsingException {
		FileInputStream fis = null;
		CompilationUnit cu = null;
		
		try {
			fis = new FileInputStream(file);
			cu = JavaParser.parse(fis);
		} catch (ParseException ex) {
			throw new ParsingException(ex);
		} catch (IOException ex) {
			throw new ParsingException(ex);
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ex) {
				throw new ParsingException(ex);
			}
		}

		SecurityVisitor cv = new SecurityVisitor();
		cv.visit(cu, null);

		securedClasses.addAll(cv.getSecuredClasses());
	}

	/** 
	 * Returns detected classes.
	 * @return detected classes.
	 */
	private Set<String> getSecuredClasses() {
		return securedClasses;
	}
	// =========================================================================
}
