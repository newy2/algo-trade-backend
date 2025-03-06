package com.newy.algotrade.product.adapter.out.external_system.model

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.product.domain.RegisterProduct

private const val PRODUCT_TYPE = "productType"

@JsonIgnoreProperties(ignoreUnknown = true)
data class ByBitProductHttpResponse(val products: List<ByBitProduct>) {
    companion object {
        fun jsonExtraValues(productType: ProductType) =
            mapOf(PRODUCT_TYPE to productType)
    }

    @JsonCreator
    constructor(
        @JacksonInject(PRODUCT_TYPE) productType: ProductType,
        @JsonProperty("result") node: JsonNode,
    ) : this(
        node["list"].map {
            ByBitProduct(
                productType = productType,
                name = it["symbol"].textValue(),
                code = it["symbol"].textValue(),
            )
        }
    )
}

data class ByBitProduct(
    val productType: ProductType,
    val name: String,
    val code: String,
) {
    fun toDomainModel(): RegisterProduct {
        return RegisterProduct(
            marketCode = MarketCode.BY_BIT,
            type = productType,
            code = code,
            name = name
        )
    }
}