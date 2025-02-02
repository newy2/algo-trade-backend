package com.newy.algotrade.market_account.adapter.out.external_system.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class LsSecGetWalletBalanceHttpResponse(@JsonProperty("rsp_cd") val responseCode: String) {
    fun isSuccess(): Boolean = responseCode == "00000"
}
