package com.newy.webflux.controller

import com.newy.common.HelloWorld
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class SimpleController {

    @GetMapping("/test2")
    suspend fun getTest2(): String {
        return HelloWorld().hello()
    }
}