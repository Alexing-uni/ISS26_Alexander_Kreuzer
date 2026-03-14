FROM eclipse-temurin:17.0.5_8-jre-focal
ADD build/distributions/conway26GuiHtml-1.0.tar /
WORKDIR /conway26GuiHtml-1.0/bin
# Forzamos el Classpath a la carpeta de librerías donde Gradle mete las clases
CMD ["java", "-cp", "../lib/*", "conway26appl.MainConwayGui"]