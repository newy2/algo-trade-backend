package com.newy.algotrade.web_flux.product_price.adapter.out.volatile_storage

import com.newy.algotrade.product_price.adapter.out.volatile_storage.InMemoryCandlesStoreAdapter
import com.newy.algotrade.web_flux.common.annotation.PersistenceAdapter

@PersistenceAdapter
class SpringInMemoryCandlesStoreAdapter : InMemoryCandlesStoreAdapter()