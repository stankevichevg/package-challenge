buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }
}

plugins {
    checkstyle
    java
    eclipse
    idea
    jacoco
}

allprojects {
    repositories {
        mavenCentral()
    }
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

group = "com.mobiquityinc"
version = "1.0-SNAPSHOT"

object Versions {
    const val junit5 = "5.3.2"
    const val assertj = "3.11.1"
    const val jacoco = "0.8.2"
}

dependencies {

    testCompile("org.junit.jupiter", "junit-jupiter-api", Versions.junit5)
    testCompile("org.junit.jupiter", "junit-jupiter-params", Versions.junit5)
    testRuntime("org.junit.jupiter", "junit-jupiter-engine", Versions.junit5)
    testCompile("org.assertj", "assertj-core", Versions.assertj)

}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
    finalizedBy("jacocoTestReport")
}

jacoco {
    toolVersion = Versions.jacoco
}

checkstyle {
    sourceSets = listOf(project.sourceSets.main.get())
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
    }
}
