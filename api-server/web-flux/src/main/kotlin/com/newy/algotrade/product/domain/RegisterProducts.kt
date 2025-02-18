package com.newy.algotrade.product.domain

data class RegisterProducts(val products: List<RegisterProduct> = emptyList()) {
    fun subtract(other: RegisterProducts): RegisterProducts {
        return RegisterProducts(
            products.subtract(other.products).toList()
        )
    }

    fun add(other: RegisterProducts): RegisterProducts {
        return RegisterProducts(
            products + other.products
        )
    }
}