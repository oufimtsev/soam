# SOAM
## Stakeholder & Objective Analysis and Management



## Based off the Spring Pet Clinic application
Petclinic is a [Spring Boot](https://spring.io/guides/gs/spring-boot) application built using [Maven](https://spring.io/guides/gs/maven/)  You can build a jar file and run it from the command line (it should work just as well with __Java 17__ or newer):
<a href="https://speakerdeck.com/michaelisvy/spring-petclinic-sample-application">See the presentation here</a>

## Running the SOAM prototype locally
> NOTE: SOAM has been updated to Spring Boot 3 and requires Java 17 to run.
```
git clone https://github.com/alecode84/soam-prototype.git
cd soam-prototype
mvnw package
java -jar target/SOAM-0.0.1-SNAPSHOT.jar
```

You can then access SOAM prototype here: http://localhost:8080/


Or you can run it from Maven directly using the Spring Boot Maven plugin. If you do this it will pick up changes that you make in the project immediately (changes to Java source files require a compile as well - most people use an IDE for this):

```
mvnw spring-boot:run
```

> NOTE: Windows users should set `git config core.autocrlf true` to avoid format assertions failing the build (use `--global` to set that flag globally).


## Building a Container

There is no `Dockerfile` in this project. You can build a container image (if you have a docker daemon) using the Spring Boot build plugin:

```
mvnw spring-boot:build-image
```


## Compiling the CSS
There is a `soam.css` in `src/main/resources/static/resources/css`. It was generated from the `soam.scss` source, combined with the [Bootstrap](https://getbootstrap.com/) library. If you make changes to the `scss`, or upgrade Bootstrap, you will need to re-compile the CSS resources using the Maven profile "css", i.e. `./mvnw package -P css`. There is no build profile for Gradle to compile the CSS.
