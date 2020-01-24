FROM openjdk:8u242
WORKDIR /usr/local/simple-file-server/
COPY target/*.jar application.jar
COPY entrypoint.sh entrypoint.sh
RUN chmod 777 entrypoint.sh
RUN mkdir persistence
ENTRYPOINT ["./entrypoint.sh"]