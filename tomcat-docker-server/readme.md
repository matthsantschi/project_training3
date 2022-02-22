## requirements
* docker
* docker-compose

## start
* make sure you buildet the java projekt `mvn clean && mvn package` the .war file is mounted into the tomcat server
* run `docker-compose up` you should see the output of a running tomcat server
and a running db-server, the db-server executes all .sql scripts found in ../src/etc
in alphabetical order. Those scripts are executed only the first time, when
the container is created. To relunch them, you have to destroy the containers
`docker container prune`
* do redeploy the servlet, just build it with `mvn clean && mvn package` and
wait some seconds, you can observe the redeployment in the console.

## hints
* `docker ps` shows the running 
* `docker container ls` shows genereted containers
* `docker container prune | docker container rm NAME` delete container