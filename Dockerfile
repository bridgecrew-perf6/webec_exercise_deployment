FROM openjdk:17
EXPOSE 8080
COPY build/libs/booklist-0.0.1-SNAPSHOT.jar booklist.jar
ENTRYPOINT ["java","-jar","/booklist.jar"]