FROM maven:3-eclipse-temurin-25

COPY . /build
WORKDIR /build

RUN mvn -B clean package appassembler:assemble && chmod +x bin/*

CMD ["./run.sh"]
