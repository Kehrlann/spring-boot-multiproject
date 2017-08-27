package wf.garnier.springboot.multiproject.secondservice

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.PropertySource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@PropertySource("second-service.yml")
class SecondServiceApplication


@RestController
class HolaController(@Value("\${message}") val genericMessage: String,
                     @Value("\${second-service.message}") val specificMessage: String) {
    @GetMapping("/hola")
    fun greet() = "Hola ! I am the second service."

    @GetMapping("/mensaje")
    fun message() = "Specific message : [$specificMessage]. Generic message : [$genericMessage]."
}

fun main(args: Array<String>) {
    SpringApplication.run(SecondServiceApplication::class.java, *args)
}
