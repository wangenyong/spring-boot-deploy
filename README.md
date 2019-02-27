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

    @RequestMapping(path = "/test")
    public String test() {
        return  "test";
    }

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

<!-- MARKDOWN LINKS & IMAGES -->
[build-shield]: https://img.shields.io/badge/build-passing-brightgreen.svg?style=flat-square
[contributors-shield]: https://img.shields.io/badge/contributors-1-orange.svg?style=flat-square
[license-shield]: https://img.shields.io/badge/license-MIT-blue.svg?style=flat-square
[license-url]: https://choosealicense.com/licenses/mit