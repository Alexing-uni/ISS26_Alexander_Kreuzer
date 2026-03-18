# Usamos una imagen oficial de Java 21
FROM eclipse-temurin:21-jdk

# Creamos la carpeta donde vivirá tu app en el contenedor
WORKDIR /app

# Copiamos todo tu código al contenedor
COPY . /app

# Damos permisos al ejecutable de Gradle por si acaso
RUN chmod +x ./gradlew

# Exponemos el puerto 8080 para que el navegador pueda entrar
EXPOSE 8080

# Le decimos a Docker que arranque tu servidor usando Gradle
CMD ["./gradlew", "run"]