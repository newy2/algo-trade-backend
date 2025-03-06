package com.newy.algotrade.product.adapter.out.external_system.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.product.domain.RegisterProduct

@JsonIgnoreProperties(ignoreUnknown = true)
data class LsSecProductHttpResponse(
    @JsonProperty("t8436OutBlock") val products: List<LsProduct>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LsProduct(
    @JsonProperty("hname") val name: String,
    @JsonProperty("shcode") val code: String,
    @JsonProperty("etfgubun") private val etfCode: String,
) {
    fun isStock(): Boolean {
        return etfCode == "0"
    }

    fun toDomainModel(): RegisterProduct {
        if (!isStock()) {
            throw IllegalArgumentException("지원하지 않는 유형입니다")
        }

        return RegisterProduct(
            marketCode = MarketCode.LS_SEC,
            type = ProductType.SPOT,
            code = this.code,
            name = this.name,
        )
    }
}