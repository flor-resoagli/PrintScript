plugins {
    `kotlin-dsl`
    jacoco
    `maven-publish`
    application
    id("cz.alenkacz.gradle.scalafmt") version "1.16.2"
    id("com.github.alisiikh.scalastyle") version "3.4.1"
}

buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
}


tasks.check {
    scalastyle
}

dependencies {
//    implementation("cz.alenkacz.gradle.scalafmt:1.16.2")
    implementation("com.github.alisiikh:gradle-scalastyle-plugin:3.4.1")
}

apply(plugin = "com.github.alisiikh.scalastyle")

scalastyle {
    failOnWarning.set(true)
    verbose.set(false)
    quiet.set(true)

    // source sets must be defined in the project
    sourceSets {

        main {
            output.dir("${projectDir}/scalastyle-main-report.xml") // output the main report to a specific location
        }

    }
}

scalafmt {
    configFilePath = ".scalafmt.conf"
}

repositories {
    gradlePluginPortal()

    maven {
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
        url = uri("https://maven.pkg.github.com/flor-resoagli/PrintScript")
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/flor-resoagli/PrintScript")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            version = "1.1.7"
            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

jacoco {
    toolVersion = "0.8.7"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports"))
    }
}

application {
    mainClass.set("cli.main.scala.Main")
}
