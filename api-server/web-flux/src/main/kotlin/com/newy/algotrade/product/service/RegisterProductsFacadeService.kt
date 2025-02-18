package com.newy.algotrade.product.service

import com.newy.algotrade.product.port.`in`.GetRegisterProductsInPort
import com.newy.algotrade.product.port.`in`.RegisterProductsInPort
import com.newy.algotrade.product.port.`in`.SetRegisterProductsInPort
import com.newy.algotrade.product.port.`in`.model.RegisterProductResult
import org.springframework.stereotype.Service

@Service
class RegisterProductsFacadeService(
    val getRegisterProductsInPort: GetRegisterProductsInPort,
    val setRegisterProductsInPort: SetRegisterProductsInPort,
) : RegisterProductsInPort {
    override suspend fun registerProducts(userId: Long): RegisterProductResult {
        return setRegisterProductsInPort.setProducts(getRegisterProductsInPort.getProducts(userId))
    }
}