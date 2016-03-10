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

import com.agapsys.mvn.scanner.AbstractListMojo;
import com.agapsys.mvn.scanner.ScannerDefs;
import static com.agapsys.security.scanner.SecurityScannerDefs.log;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 * Security implementation of AbstractListMojo
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
@Mojo(name = "list", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class SecurityListMojo extends AbstractListMojo {

	@Parameter(property = "project", readonly = true)
	private MavenProject mavenProject;
	
	@Override
	protected MavenProject getMavenProject() {
		return mavenProject;
	}

	@Parameter(defaultValue = "secured-classes")
	private String filterProperty;
	
	@Override
	protected String getFilterPropertyName() {
		return filterProperty;
	}

	@Override
	protected String getExposedEntry(String scanInfoEntry) {
		return scanInfoEntry + "\n";
	}

	@Override
	protected ScannerDefs getScannerDefs() {
		return SecurityScannerDefs.getInstance();
	}
	
	@Parameter(defaultValue = "false", name = SecurityScannerDefs.OPTION_INCLUDE_DEPENDENCIES)
	private boolean includeDependencies;

	@Override
	protected boolean includeDependencies() {
		return includeDependencies;
	}
	
	@Parameter(defaultValue = "false", name = SecurityScannerDefs.OPTION_INCLUDE_TESTS)
	private boolean includeTests;

	@Override
	protected boolean includeTests() {
		return includeTests;
	}
	
	@Override
	public void execute() throws MojoExecutionException {
		log("Listing secured classes...");
		super.execute();
		log("Done!");
	}
}
