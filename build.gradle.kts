plugins {
	id("net.fabricmc.fabric-loom") version "1.16-SNAPSHOT"
	id("maven-publish")
}

version = project.property("mod_version")!!
group = project.property("maven_group")!!

base {
	archivesName.set(project.property("archives_base_name") as String)
}

repositories {
	// Add repositories here if needed
}

loom {
	splitEnvironmentSourceSets()

	mods {
		create("holdingskull") {
			sourceSet(sourceSets["main"])
			sourceSet(sourceSets["client"])
		}
	}
}

dependencies {

	minecraft("com.mojang:minecraft:${property("minecraft_version")}")
	implementation("net.fabricmc:fabric-loader:${property("loader_version")}")

	// Fabric API (optional but recommended)
	implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
}

tasks.processResources {
	inputs.property("version", project.version)

	filesMatching("fabric.mod.json") {
		expand(mapOf("version" to project.version))
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release.set(25)
}

java {
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_25
	targetCompatibility = JavaVersion.VERSION_25
}

tasks.jar {
	inputs.property("archivesName", project.extensions.getByType<BasePluginExtension>().archivesName)

	from("LICENSE") {
		rename { "${it}_${project.property("archives_base_name")}" }
	}
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = project.property("archives_base_name") as String
			from(components["java"])
		}
	}

	repositories {
		// Add publishing repositories here
	}
}