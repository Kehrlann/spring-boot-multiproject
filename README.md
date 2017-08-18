# spring-boot-multiproject
A short demo of how to launch several Spring Boot apps with just one JVM. This is
particularly useful when developing microservice-based, that are made to run in the
cloud, and your dev machine does not have enough RAM to run them all in parallel.

Please note that the project uses Kotlin rather than Java.

## Sharing maven configuration
Most of the configuration is shared between projects - kotlin compiler and plugins,
spring parent pom, etc. Therefore, it lives in the base pom, at `./pom.xml`.
Subsequently, all subprojects (also called"maven modules") use that pom as their
parent pom :

```xml
    <parent>
        <groupId>wf.garnier.springboot.multiproject</groupId>
        <artifactId>parent</artifactId>
        <version>1.0</version>
        <relativePath>..</relativePath>
    </parent>
```

## Building all projects
You can conveniently build all projects at the same time, by running your maven
commands from the root directory. Each subproject will be built, and the output will
end up in it's own directory, under `/target`

```bash
    $ mvn package
    # output will be in each respective project, e.g. first-service/target/first-service-1.0.jar
```


## Building ONE project
All projects can be built with `mvn package`, either from their own folder or from
the root folder :

```bash
    # From the root folder
    $ mvn package -pl first-service

    # From a subfolder
    $ cd first-service
    $ mvn package
```
