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

import org.junit.Test;

public class SecurityInfoDemo {
	
	private void println(String msg, Object...args) {
		if (args.length > 0)
			msg = String.format(msg, args);
		
		System.out.println(msg);
	}
	
	@Test
	public void test() {
		SecurityInfo lib1 = new SecurityInfo("com.project1.Class1", "com.project1.Class2");
		SecurityInfo lib2 = new SecurityInfo("com.project2.Class1", "com.project2.Class2");
		
		SecurityInfo lib3 = new SecurityInfo(lib1, lib2);
		
		String securityString;
		
		securityString = lib1.toContentString();
		println("==== lib1====\n%s", securityString);
		println(SecurityInfo.read(securityString).toString());
		
		securityString = lib2.toContentString();
		println("\n==== lib2====\n%s", securityString);
		println(SecurityInfo.read(securityString).toString());
		
		securityString = lib3.toContentString();
		println("\n==== lib3====\n%s", securityString);
		println(SecurityInfo.read(securityString).toString());
	}
}