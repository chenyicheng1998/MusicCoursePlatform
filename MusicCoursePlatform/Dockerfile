# Use a known-good OpenJDK base image with JDK (needed for Maven build inside container)
FROM eclipse-temurin:21-jdk

# Set DISPLAY for Windows (Xming) - forward GUI to host
ENV DISPLAY=host.docker.internal:0.0

# Database connection via host machine (override with -e flags or docker-compose)
ENV DB_HOST=host.docker.internal
ENV DB_PORT=3306
ENV DB_USER=root
ENV DB_PASSWORD=123456

# Install GUI/X11 libraries + Maven + wget/unzip for JavaFX SDK download + Chinese fonts
RUN apt-get update && \
    apt-get install -y maven wget unzip \
    libx11-6 libxext6 libxrender1 libxtst6 libxi6 \
    libgtk-3-0 libgbm1 mesa-utils \
    fonts-noto-cjk fonts-noto-cjk-extra fonts-dejavu-core fontconfig && \
    fc-cache -fv && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Verify Chinese fonts are available
RUN fc-list :lang=zh

# Download JavaFX SDK 21 (Linux x64)
RUN wget https://download2.gluonhq.com/openjfx/21/openjfx-21_linux-x64_bin-sdk.zip -O /tmp/openjfx.zip && \
    unzip /tmp/openjfx.zip -d /opt && \
    rm /tmp/openjfx.zip

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the shaded JAR (skip test compilation AND execution to avoid GUI test issues)
RUN mvn clean package -Dmaven.test.skip=true

# List target to verify JAR was created
RUN ls -l target/

# Run the JavaFX app using the JavaFX SDK module path
# -Dprism.order=sw forces software rendering, eliminating GLX version warnings in Docker
# Font-related JVM args to ensure Chinese font support
CMD ["java", \
     "--module-path", "/opt/javafx-sdk-21/lib", \
     "--add-modules", "javafx.controls,javafx.fxml", \
     "-Dprism.order=sw", \
     "-Dprism.verbose=false", \
     "-Dglass.gtk.uiScale=1", \
     "-Djava.awt.headless=false", \
     "-Dfile.encoding=UTF-8", \
     "-Dsun.jnu.encoding=UTF-8", \
     "-Dprism.text=t2k", \
     "-Dprism.fontdir=/usr/share/fonts", \
     "-Djavafx.embed.singleThread=true", \
     "-Dprism.allowhidpi=false", \
     "-jar", "target/MusicCoursePlatform-1.0-SNAPSHOT.jar"]
