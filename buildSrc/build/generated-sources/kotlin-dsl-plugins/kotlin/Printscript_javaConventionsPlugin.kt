/**
 * Precompiled [printscript.java-conventions.gradle.kts][Printscript_java_conventions_gradle] script plugin.
 *
 * @see Printscript_java_conventions_gradle
 */
class Printscript_javaConventionsPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("Printscript_java_conventions_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
