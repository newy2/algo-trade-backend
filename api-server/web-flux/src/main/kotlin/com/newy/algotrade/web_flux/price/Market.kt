package com.newy.algotrade.web_flux.price

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class Market(
    @Id val id: Long,
    val nameKo: String,
    val nameEn: String,
)