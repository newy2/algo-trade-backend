package com.newy.algotrade.web_flux.product.adapter.out.volatile_storage

import com.newy.algotrade.coroutine_based_application.product.adapter.out.volatile_storage.InMemoryCandleStoreAdapter
import com.newy.algotrade.web_flux.common.annotation.PersistenceAdapter

@PersistenceAdapter
class SpringInMemoryCandleStoreAdapter : InMemoryCandleStoreAdapter()