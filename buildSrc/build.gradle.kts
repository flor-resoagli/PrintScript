plugins {
    `kotlin-dsl`

    id("com.github.eugenesy.scapegoat") version "0.2.0"
    id("org.scoverage") version "7.0.0"
    id("cz.alenkacz.gradle.scalafmt") version "1.16.2"
    id("jacoco")

    `maven-publish`
}

repositories {
    gradlePluginPortal()
}

//publishing {
//    repositories {
//        maven {
//            name = "GitHubPackages"
//            url = uri("https://maven.pkg.github.com/flor-resoagli/PrintScript")
//            credentials {
//                username = project.findProperty("USERNAME") as String?
//                password = project.findProperty("TOKEN") as String?
//            }
//        }
//    }
//    publications {
//        register<MavenPublication>("gpr") {
//            from(components["java"])
//        }
//    }
//}

tasks {
    reportScoverage
    jacocoTestReport
    publish
}

tasks.publish {
    group = "publishing"
    description = "Publish the project to GitHub"
    dependsOn(":gpr")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.jacocoTestReport {
    reports {
//        xml.required.set(false)
//        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}



