package com.newy.algotrade.product.adapter.out.persistence

import com.newy.algotrade.product.adapter.out.persistence.repository.MarketR2dbcRepository
import com.newy.algotrade.product.adapter.out.persistence.repository.ProductR2dbcEntity
import com.newy.algotrade.product.adapter.out.persistence.repository.ProductR2dbcRepository
import com.newy.algotrade.product.domain.RegisterProducts
import com.newy.algotrade.product.port.out.DeleteProductsOutPort
import com.newy.algotrade.product.port.out.FindAllProductsOutPort
import com.newy.algotrade.product.port.out.SaveProductsOutPort
import com.newy.algotrade.spring.annotation.PersistenceAdapter
import kotlinx.coroutines.flow.toList

@PersistenceAdapter
class ProductAdapter(
    private val productR2dbcRepository: ProductR2dbcRepository,
    private val marketR2dbcRepository: MarketR2dbcRepository
) : FindAllProductsOutPort, SaveProductsOutPort, DeleteProductsOutPort {
    override suspend fun findAllProducts(): RegisterProducts {
        val products = productR2dbcRepository.findByUseYn()
        if (products.isEmpty()) {
            return RegisterProducts()
        }
        val markets = marketR2dbcRepository.findByCodeIsNotNull()

        return RegisterProducts(
            products = products.map { it.toDomainModel(markets) }
        )
    }

    override suspend fun saveProducts(products: RegisterProducts): Int {
        val markets = marketR2dbcRepository.findByCodeIsNotNull()

        val newProducts = products.products.map { ProductR2dbcEntity(domainModel = it, markets = markets) }

        return productR2dbcRepository.saveAll<ProductR2dbcEntity>(newProducts).toList<ProductR2dbcEntity>().size
    }

    override suspend fun deleteProducts(products: RegisterProducts): Int {
        val savedProducts = productR2dbcRepository.findByUseYn()
        val markets = marketR2dbcRepository.findByCodeIsNotNull()

        val deleteProducts = products.products.map { eachDeletableProduct ->
            savedProducts.find { eachSavedProduct ->
                eachSavedProduct.toDomainModel(markets) == eachDeletableProduct
            }!!.copy(useYn = "N")
        }
        
        return productR2dbcRepository.saveAll(deleteProducts).toList().size
    }
}
