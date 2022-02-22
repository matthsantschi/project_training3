FROM tomcat:jdk17-temurin-focal
EXPOSE 8080:8080
# uncomment the following line to install the app-manager etc.
# RUN mv webapps webapps2 && mv webapps.dist webapps
RUN rm -rf /usr/local/tomcat/webapps/ROOT
COPY ./tomcat-config/server.xml /usr/local/tomcat/conf/server.xml
COPY ./tomcat-config/tomcat-users.xml /usr/local/tomcat/conf/tomcat-users.xml
