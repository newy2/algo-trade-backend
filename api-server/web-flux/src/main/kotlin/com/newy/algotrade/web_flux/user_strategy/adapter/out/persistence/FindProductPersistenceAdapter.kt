package com.newy.algotrade.web_flux.user_strategy.adapter.out.persistence

import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.user_strategy.Product
import com.newy.algotrade.user_strategy.port.out.ProductPort
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistence.repository.ProductRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component

@Component
class FindProductPersistenceAdapter(
    private val productRepository: ProductRepository
) : ProductPort {
    override suspend fun findProducts(
        marketIds: List<Long>,
        productType: ProductType,
        productCodes: List<String>
    ): List<Product> =
        productRepository.findByMarketIdInAndTypeAndCodeIn(
            marketIds = marketIds,
            type = productType.name,
            codes = productCodes,
        ).map {
            Product(
                id = it.id,
                code = it.code
            )
        }.toList()
}