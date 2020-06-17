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
    $ mvn clean package
    # output will be in each respective project, e.g. first-service/target/first-service-1.0.jar
```


## Building only one project
All projects can be built with `mvn package`, either from their own folder or from
the root folder. From the root folder, you run maven with the `-pl` flag, to select
the projects you want to build.

```bash
    # From the root folder
    $ mvn clean package -pl first-service

    # From a subfolder
    $ cd first-service
    $ mvn clean package
```

## Running only one project with the Spring-Boot plugin
You easily can run a single project by using the Maven spring-boot plugin.
```bash
    $ mvn clean spring-boot:run -pl first-service
```

## Running all projects with the Spring-Boot plugin
If you want to start all projects at the same time, you would need to do several
things :
1. Tell the spring-boot plugin to fork each run into it's own process. Otherwise,
the app runs in the same process as maven, and really weird stuff happen when
you try to run several apps in parallel. To do so, you can update your spring-boot
config in the parent pom like so :

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
    $ mvn -T 2 -pl first-service,second-service clean spring-boot:run
    ```

## Running all projects in one JVM
The previous example is convenient to run everything in parallel, but what if you're
trying to run several Spring Boot apps at the same time on your tiny laptop ? The
JVM is quite memory hungry.

You can create a new project, here called dev-server, that is also a submodule of the
parent pom. It imports both first-service and second-service. You can configure it to
have it's own endpoint, here we will add `/dev`, but it could be more useful, like
mock endpoints.

1. First, you update the pom with the correct dependencies :

    ```xml
    <project ...>

        [...]
        <parent>
            <groupId>wf.garnier.springboot.multiproject</groupId>
            <artifactId>parent</artifactId>
            <version>1.0</version>
            <relativePath>..</relativePath>
        </parent>

        <dependencies>
            <dependency>
                <groupId>wf.garnier.springboot.multiproject</groupId>
                <artifactId>first-service</artifactId>
                <version>${project.version}</version>
            </dependency>

            <dependency>
                <groupId>wf.garnier.springboot.multiproject</groupId>
                <artifactId>second-service</artifactId>
                <version>${project.version}</version>
            </dependency>
        </dependencies>
    </project>
    ```

2. You then tell Spring you want it to import other projects while auto-confguring :

    ```kotlin
    @SpringBootApplication
    @Import(FirstServiceApplication::class, SecondServiceApplication::class)
    class DevServerApplication
 
    fun main(args: Array<String>) {
        SpringApplication.run(DevServerApplication::class.java, *args)
    }
    ```

3. Now to build the project, you need to tell maven to "also-make" the dependencies,
i.e. to compile them and make them available for your dev server. This is achieved
with the `-am` flag. If you're working with an IDE, this is usually done for you
automatically. In the root directory :

    ```bash
    $ mvn clean package -am -pl dev-server
    ```

4. You can push it one step further and run it with the maven Spring Boot plugin.
However, if you just try to run `mvn spring-boot:run -am -pl dev-server`, maven
will blindly try to `spring-boot:run` ALL the projects involved : the dev-server
of course, but also the parent and both services. And you don't want to run the
services in the first place ! Moreover, the parent just can't run. A few steps
are required to fix that.
    1. First, configure the spring-boot plugin in the parent pom, telling it to
    "skip" the execution, so spring-boot:run won't run the projects. To avoid
    breaking the previous examples, we are going to put it in a maven profile,
    called `dev`, like so :
 
        ```xml
        <profiles>
            <profile>
                <id>dev</id>
                <build>
                    <plugins>
                        <plugin>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-maven-plugin</artifactId>
                            <configuration>
                                <skip>true</skip>
                            </configuration>
                        </plugin>
                    </plugins>
                </build>
            </profile>
        </profiles>
        ```

    2. Second, you configure the dev-server, to *not* skip :

        ```xml
        <build>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <configuration>
                        <skip>false</skip>
                    </configuration>
                </plugin>
            </plugins>
        </build>
        ```

    3. You can then launch the dev-server only, from the root folder, by telling
    maven to use your newly created `dev` profile with `-P dev` :

        ```bash
        $ mvn clean spring-boot:run -pl dev-server -am -P dev
        ```

    4. It should *just work*©, and you should see in your console the mappings from
    the three projects being logged :

        ```
        ... Mapped "{[/dev],methods=[GET]}" onto public java.lang.String wf.garnier.springboot.multiproject.devserver.DevServerController.hi()
        ... Mapped "{[/hello],methods=[GET]}" onto public java.lang.String wf.garnier.springboot.multiproject.firstservice.HelloController.greet()
        ... Mapped "{[/hola],methods=[GET]}" onto public java.lang.String wf.garnier.springboot.multiproject.secondservice.HolaController.greet()

        ```

    5. The project s compatible with [Spring dev tools](https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-devtools.html) 
    If one of the submodules has the dev-tools as a dependency, the dev-server will
    have them as well. To add the dev tools. Remember to build the dev-server to
    have changes Spring reload the context. The dependency would be :

        ```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
        </dependency>
        ```

## Using property files :
Properties can be distributed among several modules, but to avoid conflicts, each
submodule must have it's own property file, specifically named. Otherwise, only one
of the property files will be taken into account.

Within those property files, variables with the same name will be shadowed, and
only one will be taken into account. To avoid collisions, you can prefix the names
of properties, e.g. `first-service.message` and `second-service.message`. On the
hand, the dev-server can re-define some of the specific properties, e.g. when mocking
a service or something similar.


## TODO :
- [ ] Understand why forking is necessary in multi-threaded maven environment ?
