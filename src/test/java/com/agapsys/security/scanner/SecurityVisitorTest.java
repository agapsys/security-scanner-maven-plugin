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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class SecurityVisitorTest {
	
	@Test
	public void test() throws FileNotFoundException, ParseException, IOException {
		String fileSeparator = System.getProperty("file.separator");
		
		Set<String> expectedClasses = new LinkedHashSet<String>();
		expectedClasses.add("com.example.SecuredClass1");
		expectedClasses.add("com.example.SecuredClass1.InnerSecuredClass");
		
		File srcFile = new File(Defs.LIB_SRC_DIR, String.format("com%sexample%sConverter2.java", fileSeparator, fileSeparator));

		FileInputStream fis = new FileInputStream(srcFile);

		CompilationUnit cu;
		try {
			cu = JavaParser.parse(fis);
		} finally {
			fis.close();
		}

		SecurityVisitor cv = new SecurityVisitor();
		cv.visit(cu, null);
		
		Assert.assertEquals(expectedClasses, cv.getSecuredClasses());
	}
}
