package com.newy.algotrade.web_flux.run_strategy.adapter.out.volatile_storage

import com.newy.algotrade.run_strategy.adapter.out.volatile_storage.InMemoryStrategyStoreAdapter
import com.newy.algotrade.web_flux.common.annotation.PersistenceAdapter

@PersistenceAdapter
class SpringInMemoryStrategyStoreAdapter : InMemoryStrategyStoreAdapter()