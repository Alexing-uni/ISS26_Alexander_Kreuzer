plugins {
    java
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // Reutilización binaria del Sprint 1
    implementation(files("libs/conway26Java-1.0.jar"))
}

application {
    mainClass.set("conway.domain.MainConwaySwing")
}