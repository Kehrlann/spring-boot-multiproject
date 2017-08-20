package wf.garnier.springboot.multiproject.secondservice

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class SecondServiceApplication


@RestController
class HolaController {
    @GetMapping("/hola")
    fun greet() = "Hola ! I am the second service."
}

fun main(args: Array<String>) {
    SpringApplication.run(SecondServiceApplication::class.java, *args)
}
