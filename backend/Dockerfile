# Build stage
FROM maven:3-eclipse-temurin-24 AS build
COPY . /build
WORKDIR /build
RUN mvn -B clean package appassembler:assemble && chmod +x bin/*

# Package stage
FROM eclipse-temurin:24-jre
COPY --from=build /build /app
WORKDIR /app
CMD ["./run.sh"]
