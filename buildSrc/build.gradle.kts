plugins {
    `kotlin-dsl`
    jacoco
    `maven-publish`
    application
    id("cz.alenkacz.gradle.scalafmt") version "1.16.2"
    id("com.github.alisiikh.scalastyle") version "3.4.1"
    id("io.github.cosmicsilence.scalafix") version "0.1.13"

}

dependencies {
    implementation("org.scala-lang:scala3-library_3:3.0.0")
//    implementation("cz.alenkacz.gradle.scalafmt:1.16.2")
    implementation("com.github.alisiikh:gradle-scalastyle-plugin:3.4.1")
    implementation("io.github.cosmicsilence:gradle-scalafix:0.1.13")
//    implementation("ch.epfl.scala:scalafix-diff_2.11:0.4.2+66-0f3e3187")
}

repositories {
    gradlePluginPortal()
    mavenCentral()
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

tasks {
    scalafix
}

scalafix {

}

application {
    mainClass.set("cli.main.scala.Main")
}
