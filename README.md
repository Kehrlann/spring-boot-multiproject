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


## Building only one project
All projects can be built with `mvn package`, either from their own folder or from
the root folder. From the root folder, you run maven with the `-pl` flag, to select
the projects you want to build.

```bash
    # From the root folder
    $ mvn package -pl first-service

    # From a subfolder
    $ cd first-service
    $ mvn package
```

## Running only one project with the Spring-Boot plugin
You easily can run a single project by using the Maven spring-boot plugin.
```bash
    $ mvn spring-boot:run -pl first-service
```

## Running all projects with the Spring-Boot plugin
If you want to start all projects at the same time, you would need to do several
things :
1. Tell the spring-boot plugin to fork each run into it's own process. To do so,
you can update your spring-boot config in the parent pom like so :

    ```xml
    <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <configuration>
            <fork>true</fork>
        </configuration>
    </plugin>
    ```

2. Tell maven to run on multiple thread so it doesn't wait out on one of the runs.
To do so, you would launch maven with `-T X`, X being the number of threads. It has
to be >= to the number of projects you want to run in parallel :

    ```bash
    $ mvn -T 2 -pl first-service,second-service spring-boot:run
    ```
