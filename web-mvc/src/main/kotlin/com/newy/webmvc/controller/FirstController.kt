package com.newy.webmvc.controller

import com.newy.common.HelloWorld
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FirstController {
    @GetMapping("/test")
    fun getTest(): String {
        return HelloWorld().hello()
    }
}