plugins {
    java
    scala
    application
    `maven-publish`
    jacoco
    id("io.github.cosmicsilence.scalafix")
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