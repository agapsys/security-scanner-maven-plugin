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

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.LinkedHashSet;
import java.util.Set;

class SecurityVisitor extends VoidVisitorAdapter {
	// CLASS SCOPE =============================================================
	private static final String SECURED_ANNOTATION       = "Secured";
	private static final String SECURED_ANNOTATION_CLASS = "com.agapsys.security." + SECURED_ANNOTATION;
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	public boolean securedAnnotationImport;
	public boolean isSecuredClass;
	public String currentClassName;
	private String cuPackage;

	public final Set<String> securedClasses = new LinkedHashSet<String>();

	private void prepare() {
		securedAnnotationImport = false;
		isSecuredClass = false;
		currentClassName = "";
		cuPackage = "";
		securedClasses.clear();
	}
	
	@Override
	public void visit(CompilationUnit n, Object arg) {
		prepare();
		super.visit(n, arg);
	}
	
	@Override
	public void visit(PackageDeclaration n, Object arg) {
		cuPackage = n.getName().toString();
	}
	
	@Override
	public void visit(ImportDeclaration n, Object arg) {
		String importStr = n.getName().toString();
		
		if (importStr.equals(SECURED_ANNOTATION_CLASS)) {
			securedAnnotationImport = true;
		}
	}
	
	@Override
	public void visit(ClassOrInterfaceDeclaration n, Object arg) {
		isSecuredClass = false;
		
		// Recursive workaround to fix inner classes detection:
		for (BodyDeclaration bd : n.getMembers()) {
			if (bd instanceof ClassOrInterfaceDeclaration) {
				visit((ClassOrInterfaceDeclaration) bd, null);
			}
		}
		
		currentClassName = cuPackage.isEmpty() ? getClassName(n) : cuPackage + "." + getClassName(n);
		
		if (n.getAnnotations() != null) {
			for (AnnotationExpr annotation : n.getAnnotations()) {
				String annotationName = annotation.getName().getName();
				
				boolean isSecuredAnnotation = (annotationName.equals(SECURED_ANNOTATION) && securedAnnotationImport) || annotationName.equals(SECURED_ANNOTATION_CLASS);
				
				if (isSecuredAnnotation) {
					isSecuredClass = true;
					securedClasses.add(currentClassName);
				}
			}
		}
		super.visit(n, arg);
	}
	
	@Override
	public void visit(MethodDeclaration n, Object arg) {
		if (!isSecuredClass) {
			if (n.getAnnotations() != null) {
				for (AnnotationExpr annotation : n.getAnnotations()) {
					String annotationName = annotation.getName().getName();

					boolean isSecuredAnnotation = (annotationName.equals(SECURED_ANNOTATION) && securedAnnotationImport) || annotationName.equals(SECURED_ANNOTATION_CLASS);

					if (isSecuredAnnotation) {
						isSecuredClass = true;
						securedClasses.add(currentClassName);
					}
				}
			}
		}
	}
	
	private String getClassName(TypeDeclaration n) {
		String className = n.getName();
		Node parentNode = n.getParentNode();
		
		if (parentNode instanceof CompilationUnit)
			return className;
				
		return getClassName((TypeDeclaration) parentNode) + "$" + className;
	}
	
	public Set<String> getSecuredClasses() {
		return securedClasses;
	}
	// =========================================================================
}
