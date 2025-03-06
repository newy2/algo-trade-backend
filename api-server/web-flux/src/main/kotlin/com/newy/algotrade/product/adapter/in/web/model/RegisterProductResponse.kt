package com.newy.algotrade.product.adapter.`in`.web.model

import com.newy.algotrade.product.port.`in`.model.RegisterProductResult

data class RegisterProductResponse(
    val savedCount: Int,
    val deletedCount: Int,
) {
    constructor(domainModel: RegisterProductResult) : this(
        savedCount = domainModel.savedCount,
        deletedCount = domainModel.deletedCount,
    )
}