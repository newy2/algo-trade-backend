package com.newy.algotrade.web_flux.price

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MarketRepository : CoroutineCrudRepository<Market, Long>