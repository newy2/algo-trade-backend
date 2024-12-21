package com.newy.algotrade.web_flux.user_strategy.adapter.`in`.web

import com.newy.algotrade.user_strategy.port.`in`.SetUserStrategyUseCase
import com.newy.algotrade.web_flux.user_strategy.adapter.`in`.web.model.SetUserStrategyRequest
import com.newy.algotrade.web_flux.user_strategy.adapter.`in`.web.model.SetUserStrategyResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user-strategy")
class SetUserStrategyController(
    private val setUserStrategyUseCase: SetUserStrategyUseCase
) {
    @PostMapping
    suspend fun setMarketAccount(@RequestBody request: SetUserStrategyRequest): ResponseEntity<SetUserStrategyResponse> {
        val userStrategyId = setUserStrategyUseCase.setUserStrategy(request.toIncomingPortModel())
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(SetUserStrategyResponse(userStrategyId > 0))
    }
}