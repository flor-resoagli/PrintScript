plugins {
    `kotlin-dsl`
    id("com.github.eugenesy.scapegoat") version "0.2.0"
    id("org.scoverage") version "7.0.0"
    id("cz.alenkacz.gradle.scalafmt") version "1.16.2"
    jacoco
    `maven-publish`
    application
}


repositories {
    gradlePluginPortal()

    maven {
        credentials {
            username = System.getenv("GITHUB_ACTOR") as String
            password = System.getenv("GITHUB_TOKEN") as String
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

//tasks.jacocoTestReport {
//    reports {
////        xml.required.set(false)
////        csv.required.set(false)
//        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
//    }
//}

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
