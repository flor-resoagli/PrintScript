plugins {
    java
    scala
    application
    `maven-publish`
}

repositories {
    mavenCentral()

    gradlePluginPortal()

    maven {
        credentials {
            username = System.getenv("GITHUB_ACTOR") as? String
            password = System.getenv("GITHUB_TOKEN") as? String
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
                username = System.getenv("GITHUB_ACTOR") as? String
                password = System.getenv("GITHUB_TOKEN") as? String
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}


tasks {
    publish
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

    // linter
    implementation("com.github.eugenesy.scapegoat:gradle-scapegoat-plugin:0.2.0")
    // coverage
    implementation("org.scoverage:gradle-scoverage:7.0.0")

}

tasks.test {
    useJUnitPlatform()
}
