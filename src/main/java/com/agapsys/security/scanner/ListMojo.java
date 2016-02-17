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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

@Mojo(name = "list", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class ListMojo extends AbstractMojo {
	// STATIC SCOPE ============================================================
	static SecurityInfo getSecurityInfo(MavenProject mavenProject, boolean includeDependencies, boolean includeTest) {
		List<SecurityInfo> securityInfoList = new LinkedList<SecurityInfo>();
		
		// Scan sources...
		List<String> srcDirList = new LinkedList<String>();
		srcDirList.add(mavenProject.getBuild().getSourceDirectory());
		
		if (includeTest) 
			srcDirList.add(mavenProject.getBuild().getTestSourceDirectory());

		for (String srcDir : srcDirList) {
			securityInfoList.add(SourceDirectory.getSecurityInfo(new File(srcDir)));
		}

		// Scan dependencies...
		if (includeDependencies) {
			Set<Artifact> dependencies = new LinkedHashSet<Artifact>();
			dependencies.addAll(mavenProject.getArtifacts());
			
			if (includeTest) 
				dependencies.addAll(mavenProject.getTestArtifacts());

			for (Artifact artifact : dependencies) {
				SecurityInfo tmpInfo = JarFile.getSecurityInfo(artifact.getFile());
				if (tmpInfo != null)
					securityInfoList.add(tmpInfo);
			}
		}
		
		// Global Security information...
		return new SecurityInfo(securityInfoList.toArray(new SecurityInfo[securityInfoList.size()]));
	}
	// =========================================================================
	
	// INSTANCE SCOPE ==========================================================
	@Parameter(property = "project", readonly = true)
	private MavenProject mavenProject;

	@Parameter(defaultValue = "secured-classes")
	private String filterProperty;
	
	@Parameter(defaultValue = "true")
	private boolean processDependencies;
	
	@Parameter(defaultValue = "false")
	private boolean test;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			SecurityInfo globalSecurityInfo = getSecurityInfo(mavenProject, processDependencies, test);
			
			// output class list...
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (String clazz : globalSecurityInfo.getSecuredClasses()) {
				if (!first)
					sb.append("\n");
				
				sb.append(clazz);
				first = false;
			}

			mavenProject.getProperties().setProperty(filterProperty, sb.toString());
		} catch (ParsingException ex) {
			throw new MojoExecutionException(ex.getMessage());
		}
	}
	// =========================================================================
}
