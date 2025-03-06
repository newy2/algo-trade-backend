package com.newy.algotrade.product.service

import com.newy.algotrade.product.domain.RegisterProducts
import com.newy.algotrade.product.port.`in`.SetRegisterProductsInPort
import com.newy.algotrade.product.port.`in`.model.RegisterProductResult
import com.newy.algotrade.product.port.out.DeleteProductsOutPort
import com.newy.algotrade.product.port.out.FindAllProductsOutPort
import com.newy.algotrade.product.port.out.SaveProductsOutPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterProductsCommandService(
    val findAllProductsOutPort: FindAllProductsOutPort,
    val saveProductsOutPort: SaveProductsOutPort,
    val deleteProductsOutPort: DeleteProductsOutPort,
) : SetRegisterProductsInPort {
    @Transactional
    override suspend fun setProducts(fetchedProducts: RegisterProducts): RegisterProductResult {
        val savedProducts = findAllProductsOutPort.findAllProducts()
        val insertProducts = fetchedProducts.subtract(savedProducts)
        val deleteProducts = savedProducts.subtract(fetchedProducts)

        return RegisterProductResult(
            savedCount = saveProductsOutPort.saveProducts(insertProducts),
            deletedCount = deleteProductsOutPort.deleteProducts(deleteProducts)
        )
    }
}
