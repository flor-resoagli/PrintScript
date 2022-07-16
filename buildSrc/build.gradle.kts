plugins {
    `kotlin-dsl`

    id("com.github.eugenesy.scapegoat") version "0.2.0"
    id("org.scoverage") version "7.0.0"
    id("cz.alenkacz.gradle.scalafmt") version "1.16.2"
    id("jacoco")

    `maven-publish`
    application
}



tasks {
    reportScoverage
    jacocoTestReport
    publish
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

application {
    mainClass.set("cli.main.scala.Main")
}
repositories{
    mavenCentral()
    jcenter()
}
