// Define Java Library conventions for this organization.
// Projects need to use the organization's Java conventions and publish using Maven Publish

plugins {
    `java-library`
    id("printscript.java-conventions")

    scala


    application
}

// Projects have the 'com.example' group by convention
group = "com.example"

