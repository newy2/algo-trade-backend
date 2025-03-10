package com.newy.algotrade.product.service

import com.newy.algotrade.product.port.`in`.RegisterProductsInPort
import com.newy.algotrade.product.port.`in`.model.RegisterProductResult
import org.springframework.stereotype.Service

@Service
class RegisterProductsFacadeService(
    val queryService: RegisterProductQueryService,
    val commandService: RegisterProductCommandService,
) : RegisterProductsInPort {
    override suspend fun registerProducts(userId: Long): RegisterProductResult {
        val fetchedProducts = queryService.getProducts(userId)
        return commandService.setProducts(fetchedProducts)
    }
}