plugins {
    java
    scala
    application
    `maven-publish`
    jacoco
    id("io.github.cosmicsilence.scalafix")
//    id("com.github.alisiikh.scalastyle")
    id("com.github.alisiikh.scalastyle")
    id("org.scoverage")
    id("cz.alenkacz.scalafmt")
}

repositories {
    mavenCentral()

    gradlePluginPortal()

    maven {
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
        url = uri("https://maven.pkg.github.com/flor-resoagli/PrintScript")
    }
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }


}

dependencies {

    // Use Scala 3.0.0 in our library project
    implementation("org.scala-lang:scala3-library_3:3.0.0")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:30.1.1-jre")

    // Use Scalatest for testing our library
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.scalatest:scalatest_2.13:3.2.10")
    testImplementation("org.scalatestplus:junit-4-13_2.13:3.2.2.0")

    implementation("org.junit.jupiter:junit-jupiter-engine:5.7.1")
    implementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    // Need scala-xml at test runtime
    testRuntimeOnly("org.scala-lang.modules:scala-xml_2.13:1.2.0")


    implementation("cz.alenkacz.gradle.scalafmt:1.16.2")
    implementation("org.scoverage:gradle-scoverage:7.0.0")
    implementation("com.github.alisiikh:gradle-scalastyle-plugin:3.4.1")
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
            version = "1.2.0"
            from(components["java"])
        }
    }
}


tasks.test {
    useJUnitPlatform()
}

scalafix {

}

scalastyle {
    failOnWarning.set(true)
    verbose.set(false)
    quiet.set(true)

    // source sets must be defined in the project
    sourceSets {

        main {
            output.dir("./scalastyle-main-report.xml") // output the main report to a specific location
        }

    }
}

scoverage {
    minimumRate.set(0.80.toBigDecimal())
    scoverageScalaVersion.set("3.0.0")
    excludedPackages.add("cli")
}

tasks.test {
    finalizedBy(tasks.reportScoverage)
}
