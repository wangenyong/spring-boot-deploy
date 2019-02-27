<!-- PROJECT SHIELDS -->
[![Build Status][build-shield]]()
[![Contributors][contributors-shield]]()
[![MIT License][license-shield]][license-url]

<!-- ABOUT THE PROJECT -->
## About The Project
I am a novice on [Sprint Boot](https://spring.io/projects/spring-boot). I want to deploy Spring Boot by docker, however,  I didn't find tutorial that really suit all my needs so I create this project to instruct it. 

### Built With
* [Spring Boot](https://spring.io/projects/spring-boot) `v2.1.3`
* [IntelliJ IDEA](https://www.jetbrains.com/idea/)
* [Gradle](https://gradle.org/)
* [Mysql](https://www.mysql.com) `v5.7`
* [Nginx](https://www.nginx.com/) `stable`
* [Docker](https://www.docker.com/)
* [CentOS](https://www.centos.org/) `7`
* [wait-for-it](https://github.com/vishnubob/wait-for-it)

<!-- GETTING STARTED -->
## Getting Started

### Create project
With IntelliJ IDEA, click "File -> New -> Project...", select "Spring Initializr" to create the project.

Add jpa dependencies to connect Mysql database.
```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    // JPA Data (We are going to use Repositories, Entities, Hibernate, etc...)
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    // Use MySQL Connector-J
    implementation 'mysql:mysql-connector-java'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

### Core function
The core function is simple. Insert user and get all.
```java
@RestController
@RequestMapping(path = "/")
public class MainController {

    @Autowired
    UserRepository userRepository;

    @RequestMapping(path = "/add")
    public @ResponseBody String addNewUser (@RequestParam String name, @RequestParam String email) {
        User n = new User();
        n.setName(name);
        n.setEmail(email);
        userRepository.save(n);
        return "Saved";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
}
```

### Jpa configure
This configure file is for development.
```
# application-dev.properties
spring.jpa.hibernate.ddl-auto=create
spring.datasource.url=jdbc:mysql://localhost:3306/db_example
spring.datasource.username=springuser
spring.datasource.password=ThePassword
spring.jpa.show-sql=true

```

### Run project
Build project by command `./gradlew build`, and Run it by command `java -jar build/libs/spring-boot-deploy-0.0.1-SNAPSHOT.jar`.
Then test it by [curl](https://curl.haxx.se/).

```bash
$ curl 'localhost:8080/add?name=First&email=someemail@someemailprovider.com'
```
The reply should be
```bash
Saved
```

```bash
$ curl 'localhost:8080/all'
```
The reply should be
```json
[{"id":1,"name":"First","email":"someemail@someemailprovider.com"}]
```

## Deploy project by Docker

### Create Dockerfile
```dockerfile
FROM openjdk:8-jdk-alpine

RUN echo "https://mirror.tuna.tsinghua.edu.cn/alpine/v3.4/main/" > /etc/apk/repositories
RUN apk add --no-cache bash

VOLUME /tmp
COPY ./build/libs/spring-boot-deploy-0.0.1-SNAPSHOT.jar app.jar
COPY ./wait-for-it.sh wait-for-it.sh
RUN chmod +x /wait-for-it.sh

CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
```
### Create docker-compose.yaml
```dockerfile
version: '3'
services:
  nginx:
    container_name: v-nginx
    image: nginx:stable
    restart: always
    ports:
      - 8088:80
      - 443:443
    volumes:
      - ./nginx/conf.d:/etc/nginx/conf.d

  mysql:
    container_name: v-mysql
    image: mysql/mysql-server:5.7
    environment:
      MYSQL_DATABASE: db_example
      MYSQL_USER: springuser
      MYSQL_PASSWORD: ThePassword
      MYSQL_ROOT_PASSWORD: root
    ports:
      - 3306:3306
    volumes:
      - ./data:/var/lib/mysql
    restart: always

  app:
    restart: always
    build: .
    container_name: v-springbootdeploy
    image: springbootdeploy:0.0.2
    expose:
      - "8080"
    depends_on:
      - nginx
      - mysql
    command: ["/wait-for-it.sh","mysql:3306","--","java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","--spring.profiles.active=prod"]
```

### Create production jpa configure file
Set `spring.jpa.hibernate.ddl-auto` to `create` when the project first run. Then set it `none`. The reason can reference document [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/).
```
# # application-prod.properties
spring.jpa.hibernate.ddl-auto=create
spring.datasource.url=jdbc:mysql://mysql:3306/db_example
spring.datasource.username=springuser
spring.datasource.password=ThePassword
```

### Run project by docker-compose
```bash
$ docker-compose up
```

### Deploy image on centOS by SSH
```bash
#!/bin/bash

set -e

REMOTE_USERNAME="root"
REMOTE_HOST="192.168.43.219"
IMAGE_REPOSITORY="springbootdeploy"

function upload_image_if_needed {
    if [[ $(ssh $REMOTE_USERNAME@$REMOTE_HOST "docker images $IMAGE_REPOSITORY | grep $1 | tr -s ' ' | cut -d ' ' -f 3") != $(docker images $IMAGE_REPOSITORY | grep $1 | tr -s ' ' | cut -d ' ' -f 3) ]]
	then
		echo "$1 image changed, updating..."
		docker save $IMAGE_REPOSITORY:$1 | bzip2 | pv | ssh $REMOTE_USERNAME@$REMOTE_HOST 'bunzip2 | docker load'
	else
		echo "$1 image did not change"
	fi
}

#function build_image {
#	docker build -t $IMAGE_REPOSITORY:$1 $2
#}

#build_image 0.0.1 .
upload_image_if_needed $1
```

```bash
$ ./deploy.sh 0.0.1
```

<!-- MARKDOWN LINKS & IMAGES -->
[build-shield]: https://img.shields.io/badge/build-passing-brightgreen.svg?style=flat-square
[contributors-shield]: https://img.shields.io/badge/contributors-1-orange.svg?style=flat-square
[license-shield]: https://img.shields.io/badge/license-MIT-blue.svg?style=flat-square
[license-url]: https://choosealicense.com/licenses/mit