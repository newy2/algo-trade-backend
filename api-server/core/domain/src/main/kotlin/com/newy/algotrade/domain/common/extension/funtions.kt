package com.newy.algotrade.domain.common.extension

import java.math.BigDecimal

fun BigDecimal.compare(other: BigDecimal): Boolean = this.compareTo(other) == 0