package wf.garnier.springboot.multiproject.firstservice

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class FirstServiceApplication

@RestController
class HelloController {
    @GetMapping("/hello")
    fun greet() = "Hello !"
}

fun main(args: Array<String>) {
    SpringApplication.run(FirstServiceApplication::class.java, *args)
}
