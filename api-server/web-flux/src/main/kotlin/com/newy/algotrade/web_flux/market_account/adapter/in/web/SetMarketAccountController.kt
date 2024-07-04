package com.newy.algotrade.web_flux.market_account.adapter.`in`.web

import com.newy.algotrade.coroutine_based_application.market_account.application.port.`in`.SetMarketAccountUseCase
import com.newy.algotrade.web_flux.market_account.adapter.`in`.web.model.SetMarketAccountRequest
import com.newy.algotrade.web_flux.market_account.adapter.`in`.web.model.SetMarketAccountResponse
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
        val isSaved = setMarketAccountUseCase.setMarketAccount(request.toDomainModel())
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(SetMarketAccountResponse(isSaved))
    }
}