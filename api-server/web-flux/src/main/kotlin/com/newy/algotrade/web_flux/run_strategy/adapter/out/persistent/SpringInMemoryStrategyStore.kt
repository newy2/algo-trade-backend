package com.newy.algotrade.web_flux.run_strategy.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.run_strategy.adapter.out.persistent.InMemoryStrategyStore
import com.newy.algotrade.web_flux.common.annotation.PersistenceAdapter

@PersistenceAdapter
class SpringInMemoryStrategyStore : InMemoryStrategyStore()