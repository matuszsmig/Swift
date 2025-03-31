FROM openjdk:17-jdk-slim as build
WORKDIR /app

COPY gradlew ./
COPY gradle/ ./gradle/
COPY build.gradle.kts settings.gradle.kts ./
RUN chmod +x gradlew
COPY . .
RUN ./gradlew build -x test --no-daemon

FROM openjdk:17-jdk-slim
WORKDIR /app

RUN apt-get update && apt-get install -y python3 python3-pip
RUN ln -s /usr/bin/python3 /usr/bin/python
RUN pip install psycopg2-binary pandas openpyxl

COPY --from=build /app/build/libs/swift-0.0.1-SNAPSHOT.jar app.jar
COPY script.py /app/script.py
COPY db.xlsx /app/db.xlsx

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]