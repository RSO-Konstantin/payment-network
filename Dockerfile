FROM eclipse-temurin:17-jre

RUN mkdir /app

WORKDIR /app

ADD ./target/payment-network-0.0.1-SNAPSHOT.jar /app

EXPOSE 8081

CMD ["java", "-jar", "payment-network-0.0.1-SNAPSHOT.jar"]