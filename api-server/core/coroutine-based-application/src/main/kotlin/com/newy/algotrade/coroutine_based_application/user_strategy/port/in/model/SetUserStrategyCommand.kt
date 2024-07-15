package com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.model

import com.newy.algotrade.domain.common.consts.ProductCategory
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.helper.SelfValidating
import com.newy.algotrade.domain.common.libs.validation.NotBlankElements
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class SetUserStrategyCommand(
    @field:Min(1) val marketAccountId: Long,
    @field:NotBlank val strategyClassName: String,
    val productCategory: ProductCategory,
    val productType: ProductType,
    @field:NotBlankElements val productCodes: List<String>,
) : SelfValidating() {
    init {
        validate()
        if (productCategory == ProductCategory.USER_PICK && productCodes.isEmpty()) {
            throw IllegalArgumentException("productCategory 가 'USER_PICK'인 경우, productCodes 를 필수로 입력해야 합니다.")
        }
        if (productCategory == ProductCategory.TOP_TRADING_VALUE && productCodes.isNotEmpty()) {
            throw IllegalArgumentException("productCategory 가 'TOP_TRADING_VALUE'인 경우, productCodes 는 emptyList 여야 합니다.")
        }
    }
}