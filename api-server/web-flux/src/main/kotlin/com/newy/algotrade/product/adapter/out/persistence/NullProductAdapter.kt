package com.newy.algotrade.product.adapter.out.persistence

import com.newy.algotrade.product.domain.RegisterProducts
import com.newy.algotrade.product.port.out.DeleteProductsOutPort
import com.newy.algotrade.product.port.out.FindAllProductsOutPort
import com.newy.algotrade.product.port.out.SaveProductsOutPort
import com.newy.algotrade.spring.annotation.PersistenceAdapter

@PersistenceAdapter
class NullProductAdapter : FindAllProductsOutPort, SaveProductsOutPort, DeleteProductsOutPort {
    override suspend fun findAllProducts(): RegisterProducts {
        TODO("Not yet implemented")
    }

    override suspend fun saveProducts(products: RegisterProducts): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProducts(products: RegisterProducts): Int {
        TODO("Not yet implemented")
    }
}
