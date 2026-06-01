plugins {
    kotlin("jvm") version "2.2.20"
    `maven-publish`
}

group = "com.github.afeibaili"
version = "0.0.1"
val mavenPackageName = "wrapgl"

val lwjglVersion = "3.3.6"
val jomlVersion = "1.10.8"
val lwjglNatives = "natives-windows"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-freetype")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-stb")
    implementation("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    implementation("org.lwjgl", "lwjgl-freetype", classifier = lwjglNatives)
    implementation("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    implementation("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
    implementation("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
    implementation("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
    implementation("org.joml", "joml", jomlVersion)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("gpr") {
            groupId = project.group.toString()
            artifactId = mavenPackageName
            version = project.version.toString()

            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/afeibaili/wrapgl")
            credentials {
                username = System.getenv("GITHUB_ACCOUNT")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

kotlin {
    jvmToolchain(17)
}