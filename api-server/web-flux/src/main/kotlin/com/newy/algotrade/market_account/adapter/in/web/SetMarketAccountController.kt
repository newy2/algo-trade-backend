package com.newy.algotrade.market_account.adapter.`in`.web

import com.newy.algotrade.market_account.adapter.`in`.web.model.SetMarketAccountRequest
import com.newy.algotrade.market_account.adapter.`in`.web.model.SetMarketAccountResponse
import com.newy.algotrade.market_account.port.`in`.SetMarketAccountUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/market/account")
class SetMarketAccountController(
    private val setMarketAccountUseCase: SetMarketAccountUseCase
) {

    @PostMapping
    suspend fun setMarketAccount(@RequestBody request: SetMarketAccountRequest): ResponseEntity<SetMarketAccountResponse> {
        val domainEntity = setMarketAccountUseCase.setMarketAccount(request.toIncomingPortModel())
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(SetMarketAccountResponse(domainEntity.id > 0))
    }
}