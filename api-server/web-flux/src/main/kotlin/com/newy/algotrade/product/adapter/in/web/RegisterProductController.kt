package com.newy.algotrade.product.adapter.`in`.web

import com.newy.algotrade.product.adapter.`in`.web.model.RegisterProductResponse
import com.newy.algotrade.product.port.`in`.RegisterProductsInPort
import com.newy.algotrade.spring.auth.annotation.AdminOnly
import com.newy.algotrade.spring.auth.annotation.LoginUserInfo
import com.newy.algotrade.spring.auth.model.LoginUser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RegisterProductController(
    private val registerProductsInPort: RegisterProductsInPort
) {
    @AdminOnly
    @PostMapping("/products")
    suspend fun registerProducts(
        @LoginUserInfo loginUser: LoginUser,
    ): ResponseEntity<RegisterProductResponse> {
        return ResponseEntity.ok(RegisterProductResponse(registerProductsInPort.registerProducts(loginUser.id)))
    }
}