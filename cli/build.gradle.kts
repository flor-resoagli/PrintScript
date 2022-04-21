plugins {
    id("printscript.library-conventions")
}

dependencies {
    implementation(project(":lexer"))
    implementation(project(":parser"))
    implementation(project(":interpreter"))

}




