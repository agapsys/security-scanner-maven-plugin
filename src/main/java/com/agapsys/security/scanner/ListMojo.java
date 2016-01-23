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

	@Parameter(property = "project", readonly = true)
	private MavenProject mavenProject;

	@Parameter(defaultValue = "secured-classes")
	private String filterProperty;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			// Source directory...
			SecurityInfo srcDirInfo = SourceDirectory.getSecurityInfo(new File(mavenProject.getBuild().getSourceDirectory()));

			// JAR files with security information...
			List<SecurityInfo> securityInfoList = new LinkedList<SecurityInfo>();

			Set<Artifact> dependencies = mavenProject.getArtifacts();
			for (Artifact artifact : dependencies) {
				SecurityInfo tmpInfo = JarFile.getSecurityInfo(artifact.getFile());
				if (tmpInfo != null)
					securityInfoList.add(tmpInfo);
			}
			
			// Global information...
			SecurityInfo[] infoArray = new SecurityInfo[securityInfoList.size() + 1];
			infoArray[0] = srcDirInfo;
			for (int i = 1; i < infoArray.length; i++) {
				infoArray[i] = securityInfoList.get(i - 1);
			}
			SecurityInfo globalSecurityInfo = new SecurityInfo(infoArray);
			
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
