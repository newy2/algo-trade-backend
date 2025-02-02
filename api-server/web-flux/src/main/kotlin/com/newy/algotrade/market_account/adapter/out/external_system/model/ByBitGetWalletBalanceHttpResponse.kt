package com.newy.algotrade.market_account.adapter.out.external_system.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class ByBitGetWalletBalanceHttpResponse(@JsonProperty("retCode") val responseCode: Int) {
    fun isSuccess(): Boolean = responseCode == 0
}