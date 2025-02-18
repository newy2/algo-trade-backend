package com.newy.algotrade.product.service

import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.product.domain.RegisterProducts
import com.newy.algotrade.product.port.`in`.GetRegisterProductsInPort
import com.newy.algotrade.product.port.out.FetchProductsOutPort
import com.newy.algotrade.product.port.out.FindPrivateApiInfoOutPort
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service

@Service
class RegisterProductQueryService(
    private val findPrivateApiInfoOutPort: FindPrivateApiInfoOutPort,
    private val fetchProductsOutPort: FetchProductsOutPort,
) : GetRegisterProductsInPort {
    override suspend fun getProducts(userId: Long): RegisterProducts = coroutineScope {
        val privateApiInfoMap = findPrivateApiInfoOutPort.findPrivateApiInfos(userId)

        return@coroutineScope listOf(
            async {
                fetchProductsOutPort.fetchProducts(
                    marketCode = MarketCode.LS_SEC,
                    productType = ProductType.SPOT,
                    privateApiInfos = privateApiInfoMap,
                )
            },
            async {
                fetchProductsOutPort.fetchProducts(
                    marketCode = MarketCode.BY_BIT,
                    productType = ProductType.SPOT,
                    privateApiInfos = privateApiInfoMap,
                )
            },
            async {
                fetchProductsOutPort.fetchProducts(
                    marketCode = MarketCode.BY_BIT,
                    productType = ProductType.PERPETUAL_FUTURE,
                    privateApiInfos = privateApiInfoMap,
                )
            },
        ).awaitAll().reduce { result, each ->
            result.add(each)
        }
    }
}
