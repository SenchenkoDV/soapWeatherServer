FROM openjdk:8
ADD target/heroes3statistic.jar heroes3statistic.jar
EXPOSE 8080
CMD java -jar heroes3statistic.jar