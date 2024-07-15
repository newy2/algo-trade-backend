package com.newy.algotrade.web_flux.user_strategy.adapter.`in`.web.model

import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.model.SetUserStrategyCommand
import com.newy.algotrade.domain.common.consts.ProductCategory
import com.newy.algotrade.domain.common.consts.ProductType

data class SetUserStrategyRequest(
    val marketAccountId: Long,
    val strategyClassName: String,
    val productCategory: String,
    val productType: String,
    val productCodes: List<String>,
) {
    fun toDomainModel() =
        SetUserStrategyCommand(
            marketAccountId,
            strategyClassName,
            ProductCategory.valueOf(productCategory),
            ProductType.valueOf(productType),
            productCodes,
        )
}