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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Represents a JAR file containing embedded security information. 
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
class JarFile {
	// CLASS SCOPE =============================================================
	/** Embedded resource inside a JAR file. */
	public static final String EMBEDDED_XML = "META-INF/" + CreatetMojo.OUTPUT_FILENAME;
	
	/**
	 * Returns security information contained in jar file
	 * @param jarFile JAR file
	 * @return security information contained in jar file or null if this jar file does not contain security information
	 * @throws ParsingException if there was an error while processing the file
	 */
	public static SecurityInfo getSecurityInfo(File jarFile) throws ParsingException {
		return new JarFile(jarFile).getSecurityInfo();
	}
	// =========================================================================
	
	// INSTANCE SCOPE ==========================================================
	private final SecurityInfo securityInfo;
	
	/**
	 * Constructor.
	 * @param jarFile JAR file
	 * @throws ParsingException if there was an error while processing the file
	 */
	private JarFile(File jarFile) throws ParsingException {
		InputStream is = null;
		try {
			URL[] urls = {jarFile.toURI().toURL()};
			ClassLoader cl = new URLClassLoader(urls);
			
			is = cl.getResourceAsStream(EMBEDDED_XML);
			
			if (is == null) {
				securityInfo = null;
				return;
			}
			
			securityInfo = SecurityInfo.read(is);
						
		} catch (MalformedURLException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (is != null)
				try {
					is.close();
			} catch (IOException ex) {
				throw new ParsingException(ex);
			}
		}
	}
	
	/**
	 * Returns security information contained in jar file
	 * @return security information contained in jar file or null if this jar file does not contain security information
	 */
	private SecurityInfo getSecurityInfo() {
		return securityInfo;
	}
	// =========================================================================
}