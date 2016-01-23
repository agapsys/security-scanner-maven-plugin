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
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

class SecurityVisitor extends VoidVisitorAdapter {
	// CLASS SCOPE =============================================================
	private static final String ENTITY_ANNOTATION_CLASS            = "javax.persistence.Entity";
	private static final String CONVERTER_ANNOTATION_CLASS        = "javax.persistence.Converter";
	private static final String ATTRIBUTE_CONVERTER_INTERFACE_CLASS = "javax.persistence.AttributeConverter";
	
	private static final String ENTITY_ANNOTATION     = "Entity";
	private static final String CONVERTER_ANNOTATION = "Converter";
	
	private static final String ATTRIBUTE_CONVERTER_INTERFACE = "AttributeConverter";
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	public boolean entityAnnotationImport;
	public boolean converterAnnotationImport;
	public boolean attributeConverterInterfaceImport;
	private String cuPackage;

	public final Set<String> securedClasses = new LinkedHashSet<String>();

	private void prepare() {
		cuPackage = "";
		entityAnnotationImport = false;
		converterAnnotationImport =  false;
		attributeConverterInterfaceImport = false;
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
		
		if (importStr.equals(ENTITY_ANNOTATION_CLASS)) {
			entityAnnotationImport = true;
		} else if (importStr.equals(CONVERTER_ANNOTATION_CLASS)) {
			converterAnnotationImport = true;
		} else if (importStr.equals(ATTRIBUTE_CONVERTER_INTERFACE_CLASS)) {
			attributeConverterInterfaceImport = true;
		}
	}
	
	private boolean passModifiers(String packageName, String className, TypeDeclaration n) {
		String classNameWithoutPackage = packageName.isEmpty() ? className : className.replaceFirst(Pattern.quote(packageName + "."), "");
		boolean innerClass = classNameWithoutPackage.split(Pattern.quote(".")).length > 1;
		
		if (!innerClass)
			return ModifierSet.hasModifier(n.getModifiers(), ModifierSet.PUBLIC);
		
		return 
			ModifierSet.hasModifier(n.getModifiers(), ModifierSet.PUBLIC)
			&& ModifierSet.hasModifier(n.getModifiers(), ModifierSet.STATIC);
	}
	
	private String getClassName(TypeDeclaration n) {
		String className = n.getName();
		Node parentNode = n.getParentNode();
		
		if (parentNode instanceof CompilationUnit)
			return className;
				
		return getClassName((TypeDeclaration) parentNode) + "." + className;
	}
	
	
	@Override
	public void visit(ClassOrInterfaceDeclaration n, Object arg) {
		// Recursive workaround to fix inner classes detection:
		for (BodyDeclaration bd : n.getMembers()) {
			if (bd instanceof ClassOrInterfaceDeclaration) {
				visit((ClassOrInterfaceDeclaration) bd, null);
			}
		}
		
		String currentClassName = cuPackage.isEmpty() ? getClassName(n) : cuPackage + "." + getClassName(n);
		
		if (n.getAnnotations() != null) {
			for (AnnotationExpr annotation : n.getAnnotations()) {
				String annotationName = annotation.getName().getName();
				
				boolean isEntityAnnotation = (annotationName.equals(ENTITY_ANNOTATION) && entityAnnotationImport) || annotationName.equals(ENTITY_ANNOTATION_CLASS);
				boolean isConverterAnnotation = (annotationName.equals(CONVERTER_ANNOTATION) && converterAnnotationImport) || annotationName.equals(CONVERTER_ANNOTATION_CLASS);
				
				String modifiersErrMsg = String.format("Invalid modifiers for class '%s'", currentClassName);
				
				if (isEntityAnnotation) {
					if (!passModifiers(cuPackage, currentClassName, n))
						throw new RuntimeException(modifiersErrMsg);
					
					securedClasses.add(currentClassName);
					
				} else if (isConverterAnnotation) {
					String errMsg = String.format("Converter class '%s' does not implement '%s'", currentClassName, ATTRIBUTE_CONVERTER_INTERFACE_CLASS);
					
					if (!attributeConverterInterfaceImport)
						throw new ParsingException(errMsg);
					
					for (ClassOrInterfaceType cOrI : n.getImplements()) {
						boolean isAttributeConverterInterface = (cOrI.getName().equals(ATTRIBUTE_CONVERTER_INTERFACE) && attributeConverterInterfaceImport) || cOrI.getName().equals(ATTRIBUTE_CONVERTER_INTERFACE_CLASS);
						
						if (isAttributeConverterInterface) {
							if (!passModifiers(cuPackage, currentClassName, n))
								throw new ParsingException(modifiersErrMsg);
							securedClasses.add(currentClassName);
							break;
						}
						
						throw new ParsingException(errMsg);
					}
				}
			}
		}
	}
	
	public Set<String> getSecuredClasses() {
		return securedClasses;
	}
	// =========================================================================
}
