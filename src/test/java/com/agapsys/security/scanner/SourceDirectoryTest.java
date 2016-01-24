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

import org.junit.Assert;
import org.junit.Test;

public class SourceDirectoryTest {
	
	@Test
	public void test() {
		SecurityInfo srcDirInfo = SourceDirectory.getSecurityInfo(Defs.LIB_SRC_DIR);
		
		SecurityInfo expectedInfo = new SecurityInfo(
			"com.example.SecuredClass1",
			"com.example.SecuredClass2",
			"com.example.SecuredClass2$InnerSecuredClass1",
			"com.example.SecuredClass2$InnerSecuredClass2",
			"com.example.UnsecuredClass$InnerSecuredClass1",
			"com.example.UnsecuredClass$InnerSecuredClass2"
		);
		
		Assert.assertEquals(expectedInfo, srcDirInfo);
	}
}
