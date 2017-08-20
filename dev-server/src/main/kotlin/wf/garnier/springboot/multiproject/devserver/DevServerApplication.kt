package wf.garnier.springboot.multiproject.devserver

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import wf.garnier.springboot.multiproject.firstservice.FirstServiceApplication
import wf.garnier.springboot.multiproject.secondservice.SecondServiceApplication

@SpringBootApplication
@Import(FirstServiceApplication::class, SecondServiceApplication::class)
class DevServerApplication

@RestController
class DevServerController {
    @GetMapping("/dev") fun hi() = "Hello, I'm the dev server !"
}

fun main(args: Array<String>) {
    SpringApplication.run(DevServerApplication::class.java, *args)
}
