FROM openjdk:21-slim
# base image version必须和当前java匹配
COPY target/*.jar app.jar
#
EXPOSE 5001
#image内部暴露端口，会有port外部端口映射该端口
ENTRYPOINT ["java","-jar","/app.jar"]

