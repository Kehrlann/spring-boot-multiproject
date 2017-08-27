package wf.garnier.springboot.multiproject.firstservice

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.PropertySource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@PropertySource("first-service.yml")
class FirstServiceApplication

@RestController
class HelloController(@Value("\${message}") val genericMessage: String,
                      @Value("\${first-service.message}") val specificMessage: String)
{
    @GetMapping("/hello")
    fun greet() = "Hello ! I am the first service."

    @GetMapping("/message")
    fun message() = "Specific message : [$specificMessage]. Generic message : [$genericMessage]."
}

fun main(args: Array<String>) {
    SpringApplication.run(FirstServiceApplication::class.java, *args)
}
