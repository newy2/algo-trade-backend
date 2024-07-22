package com.newy.algotrade.web_flux.product.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.product.adapter.out.persistent.InMemoryCandleStore
import com.newy.algotrade.web_flux.common.annotation.PersistenceAdapter

@PersistenceAdapter
class InMemoryCandleSpringStore : InMemoryCandleStore()