FROM maven:3.9.4-amazoncorretto-17 AS builder
WORKDIR /app

# Копіюємо проект
COPY . .

# Збираємо проект
RUN mvn clean package -DskipTests

# Фінальний образ
FROM amazoncorretto:17
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
