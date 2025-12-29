FROM maven:3-eclipse-temurin-24

COPY . /build
WORKDIR /build

RUN mvn -B clean package appassembler:assemble -DskipTests && chmod +x bin/*
RUN sed -i 's/\r$//' run.sh

CMD ["./run.sh"]
