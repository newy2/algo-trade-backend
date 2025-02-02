package com.newy.algotrade.market_account.adapter.`in`.web

import com.newy.algotrade.market_account.adapter.`in`.web.model.RegisterMarketAccountRequest
import com.newy.algotrade.market_account.port.`in`.RegisterMarketAccountInPort
import com.newy.algotrade.spring.auth.annotation.AdminOnly
import com.newy.algotrade.spring.auth.annotation.AdminUser
import com.newy.algotrade.spring.auth.model.LoginUser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class RegisterMarketAccountController(
    private val registerMarketAccountInPort: RegisterMarketAccountInPort
) {
    @AdminOnly
    @PostMapping("/setting/market-account")
    suspend fun registerMarketAccount(
        @AdminUser currentUser: LoginUser,
        @RequestBody request: RegisterMarketAccountRequest
    ): ResponseEntity<Unit> {
        registerMarketAccountInPort.registerMarketAccount(request.toInPortModel(currentUser.id))
        return ResponseEntity.created(URI.create("/setting")).build()
    }
}
