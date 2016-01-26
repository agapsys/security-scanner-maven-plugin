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
import java.io.IOException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

@Mojo(name = "create", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class CreatetMojo extends AbstractMojo {
	// CLASS SCOPE =============================================================
	static final String OUTPUT_FILENAME = "security.info";
	// =========================================================================
	
	// INSTANCE SCOPE ==========================================================	
	@Parameter(property = "project", readonly = true)
	private MavenProject mavenProject;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			File sourceDir = new File(mavenProject.getBuild().getSourceDirectory());
			SecurityInfo srcDirInfo = SourceDirectory.getSecurityInfo(sourceDir);
			
			String fileSeparator = FileUtils.FOLDER_DELIMITER;
			String outputDirectoryPath = String.format("%s%sMETA-INF", mavenProject.getBuild().getOutputDirectory(), fileSeparator);
			
			File outputDirectory = FileUtils.getOrCreateDirectory(outputDirectoryPath);
			File outputFile = new File(outputDirectory, OUTPUT_FILENAME);
			
			String ioErrMsg = "Error generating file: " + outputFile.getAbsolutePath();
			try {
				srcDirInfo.write(outputFile);
			} catch (IOException ex) {
				throw new MojoFailureException(ioErrMsg);
			}
		} catch (ParsingException ex) {
			throw new MojoFailureException(ex.getMessage());
		}
	}
	// =========================================================================
}
