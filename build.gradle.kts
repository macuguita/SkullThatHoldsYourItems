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
    maven {
        name = "Macuguita"
        url = uri("https://maven.macuguita.com/releases")
    }
    maven {
        name = "Nucleoid"
        url = uri("https://maven.nucleoid.xyz/releases")
    }
}

loom {
    accessWidenerPath.set(project.file("src/main/resources/holdingskull.classtweaker"))

    splitEnvironmentSourceSets()

    mods {
        create("holdingskull") {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }

    runs {
        create("clientMacuguita") {
            client()
            name = "Minecraft Client macuguita"
            programArgs.add("--username=macuguita")
            programArgs.add("--uuid=0e56050b-ee27-478a-a345-d2b384919081")
        }
        create("clientLadybrine") {
            client()
            name = "Minecraft Client Ladybrine"
            programArgs.add("--username=Ladybrine")
            programArgs.add("--uuid=5d66606c-949c-47ce-ba4c-a1b9339ba3c8")
        }
    }
}

dependencies {

    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${property("loader_version")}")

    // Fabric API (optional but recommended)
    implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
    implementation("folk.sisby:kaleido-config:${property("kaleido_version")}")
    include("folk.sisby:kaleido-config:${property("kaleido_version")}")

    implementation("eu.pb4:trinkets:${property("trinkets_version")}")
    implementation("com.macuguita:gbackpacks:26.1.1-beta+9") {
        isTransitive = false
    }
    implementation("com.macuguita:macu_lib-fabric:2.0.2+26.1")
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