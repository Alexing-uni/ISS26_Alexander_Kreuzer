plugins {
    java
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // Reutilizamos el JAR del Sprint 1 como librería (Requisito 2)
    implementation(files("libs/conway26Java-1.0.jar"))
    
    // Librerías para el Servicio Web (Javalin y logs)
    implementation("io.javalin:javalin:6.1.3")
    implementation("org.slf4j:slf4j-simple:2.0.12")
    
    // Para procesar mensajes JSON (si el profesor usa org.json.simple)
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
}

application {
    // Por ahora dejaremos tu main de Swing
    mainClass.set("conway.domain.MainConwaySwing")
}

// Tarea para crear el Fat JAR (Requisito 1)
tasks.jar {
    manifest {
        attributes["Main-Class"] = "conway.domain.MainConwaySwing"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}