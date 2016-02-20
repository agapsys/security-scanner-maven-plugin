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

import com.agapsys.mvn.scanner.parser.ParsingException;
import java.io.File;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class SourceFileInfoTest {
	
	@Test
	public void test() throws ParsingException {
		String fileSeparator = System.getProperty("file.separator");
		
		Set<String> expectedClasses = TestUtils.getStringSet(
			"com.example.UnsecuredClass$InnerSecuredClass1",
			"com.example.UnsecuredClass$InnerSecuredClass2"
		);
		
		File srcFile = new File(Defs.LIB_SRC_DIR, String.format("com%sexample%sUnsecuredClass.java", fileSeparator, fileSeparator));
		Set<String> scannedClasses = TestUtils.scanJpaClasses(srcFile);
		
		Assert.assertEquals(expectedClasses, scannedClasses);
	}
}
