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

import com.agapsys.mvn.scanner.SourceDirectoryScanner;
import com.agapsys.mvn.scanner.parser.AnnotationInfo;
import com.agapsys.mvn.scanner.parser.ClassInfo;
import com.agapsys.mvn.scanner.parser.MethodInfo;
import com.agapsys.mvn.scanner.parser.ParsingException;
import java.util.Collection;

/**
 * Security Implementation of Source Directory Scanner
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
public class SecuritySourceDirectoryScanner extends SourceDirectoryScanner {
	// STATIC SCOPE ============================================================
	private static SecuritySourceDirectoryScanner SINGLETON = new SecuritySourceDirectoryScanner();
	
	public static SecuritySourceDirectoryScanner getInstance() {
		return SINGLETON;
	}
	
	private static boolean containsAnnotationClass(Collection<AnnotationInfo> annotationInfoCollection, String annotationClassName) {
		for (AnnotationInfo annotationInfo : annotationInfoCollection) {
			if (annotationInfo.className.equals(annotationClassName))
				return true;
		}
		
		return false;
	}
	// =========================================================================
	
	// INSTANCE SCOPE ==========================================================
	private SecuritySourceDirectoryScanner() {}
	
	private static final String SECURED_ANNOTATION_CLASS = "com.agapsys.security.Secured";
	
	@Override
	protected boolean shallBeIncluded(ClassInfo classInfo) throws ParsingException {
		boolean hasSecuredAnnotation = containsAnnotationClass(classInfo.annotations, SECURED_ANNOTATION_CLASS);
		
		if (hasSecuredAnnotation)
			return true;
		
		boolean hasMethodAnnotedWithSecuredAnnotation = false;
		
		for (MethodInfo methodInfo : classInfo.methods) {
			if (containsAnnotationClass(methodInfo.annotations, SECURED_ANNOTATION_CLASS)) {
				hasMethodAnnotedWithSecuredAnnotation = true;
				break;
			}
		}
		
		return hasMethodAnnotedWithSecuredAnnotation;
	}
	// =========================================================================
}
