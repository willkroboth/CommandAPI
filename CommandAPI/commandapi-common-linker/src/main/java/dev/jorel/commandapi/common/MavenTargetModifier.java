package dev.jorel.commandapi.common;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

//import com.sun.tools.classfile.Dependency;

@Mojo(name = "modify-target")
public class MavenTargetModifier extends AbstractMojo {

	@Parameter(defaultValue = "${project}", readonly = true)
	private MavenProject project;

	@Parameter(defaultValue = "${project.build.directory}", readonly = true)
	private File target;

	public void execute() throws MojoExecutionException, MojoFailureException {

		/*
		 * getLog() from AbstractMojo provides logger of maven so that we can see it in
		 * build log.
		 */
		getLog().info("Execution for maven target modifier started");

		/*
		 * Iterate dependencies & collect information about dependencies
		 */
//		String dependenciesText = "";
//		for (Object dep : project.getDependencies()) {
//			Dependency dependency = (Dependency) dep;
//			dependenciesText = dependenciesText + " Group = " + dependency.getGroupId() + " Artifact = "
//					+ dependency.getArtifactId() + " Version = " + dependency.getVersion() + " \n";
//
//		}

		/*
		 * Create a new file in target & add above dependency information to that.
		 */

//		File newFile = new File(target, "generatedFile.txt");
//		try {
//
//			newFile.createNewFile();
//			Files.write(newFile.toPath(), dependenciesText.getBytes(), StandardOpenOption.APPEND);
//			getLog().info("Generated - " + newFile.getAbsolutePath());
//		} catch (IOException e) {
//			getLog().error("Failed to generate new file", e);
//			throw new MojoExecutionException("Failed to generate new file", e);
//		}

		/*
		 * Modify existing file from target & append text to it. Assume that
		 * test.properties exists in project for example purpose.
		 */
		File classes = new File(target.getAbsolutePath() + System.getProperty("file.separator") + "classes");
		File testProp = Arrays.stream(classes.listFiles()).filter(f -> f.getName().equalsIgnoreCase("test.properties"))
				.collect(Collectors.toList()).get(0);
		try {

			Files.write(testProp.toPath(), "Added by plugin".getBytes(), StandardOpenOption.APPEND);
			getLog().info("Updated - " + testProp.getAbsolutePath());
		} catch (IOException e) {
			getLog().error("Failed to modify properties", e);
			throw new MojoExecutionException("Failed to modify properties", e);
		}

	}

}