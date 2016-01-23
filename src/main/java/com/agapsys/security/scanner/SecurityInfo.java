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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class SecurityInfo {
	// CLASS SCOPE =============================================================	
	private static Set<String> readSecuredClasses(Reader reader) {
		try {
			BufferedReader in = new BufferedReader(reader);
			Set<String> classes = new LinkedHashSet<String>();
			String readLine;

			while ((readLine = in.readLine()) != null) {
				readLine = readLine.trim();
				
				if (readLine.isEmpty())
					continue;
				
				if (!classes.add(readLine)) {
					throw new RuntimeException("Duplicate definition of " + readLine);
				}
			}

			return classes;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private static Set<String> readSecuredClasses(InputStream is, String encoding) {
		try {
			Reader reader = new InputStreamReader(is, encoding);
			return readSecuredClasses(reader);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	
	public static SecurityInfo read(InputStream inputStream) {
		return new SecurityInfo(readSecuredClasses(inputStream, "utf-8"));
	}
	
	public static SecurityInfo read(Reader reader) {
		return new SecurityInfo(readSecuredClasses(reader));
	}
	
	public static SecurityInfo read(String string) {
		Set<String> classes = new LinkedHashSet<String>();
		String[] classArray = string.split(Pattern.quote("\n"));
		
		for (int i = 0; i < classArray.length; i++) {
			classArray[i] = classArray[i].trim();
			if (!classArray[i].isEmpty()) {
				if (!classes.add(classArray[i]))
					throw new IllegalArgumentException("Duplicate definition of " + classArray[i]);
			}
		}
		
		return new SecurityInfo(classes);
	}
	// =========================================================================
	
	// INSTANCE SCOPE ==========================================================
	private final Set<String> securedClasses;
	
	private SecurityInfo() {
		securedClasses = new LinkedHashSet<String>();
	}
	
	private SecurityInfo(Set<String> securedClasses) {
		this.securedClasses = securedClasses;
	}
	
	public SecurityInfo(String...securedClasses) {
		this();
		for (int i = 0; i < securedClasses.length; i++) {
			String className = securedClasses[i];
			if (className == null) 
				throw new IllegalArgumentException("Null class at index " + i);
			
			if (!this.securedClasses.add(className))
				throw new IllegalArgumentException("Duplicate definition of " + className);
		}
	}
			
	public SecurityInfo(SecurityInfo...others) {
		this();
		for (SecurityInfo other : others) {
			for (String className : other.getSecuredClasses()) {
				if (!this.securedClasses.add(className))
					throw new IllegalArgumentException("Duplicate definition of " + className);
			}
		}
	}
	
	public Set<String> getSecuredClasses() {
		return securedClasses;
	}
	
	public String toContentString() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String className : getSecuredClasses()) {
			if (!first)
				sb.append("\n");
			
			sb.append(className);
			first = false;
		}
		return sb.toString();
	}
	
	public void write(OutputStream outputStream) {
		PrintWriter pw = new PrintWriter(outputStream);
		write(pw);
	}
	
	public void write(Writer writer) {
		PrintWriter pw = new PrintWriter(writer);
		for (String className : getSecuredClasses()) {
			pw.println(className);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String className : getSecuredClasses()) {
			if (i > 0) sb.append(", ");
			sb.append(className);
			i++;
		}
		return String.format("[%s]", sb.toString());
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + (this.securedClasses != null ? this.securedClasses.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SecurityInfo other = (SecurityInfo) obj;
		if (this.securedClasses != other.securedClasses && (this.securedClasses == null || !this.securedClasses.equals(other.securedClasses))) {
			return false;
		}
		return true;
	}
	// =========================================================================
}