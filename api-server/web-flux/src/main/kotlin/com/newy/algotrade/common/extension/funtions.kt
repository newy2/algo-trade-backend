package com.newy.algotrade.common.extension

import java.math.BigDecimal

fun BigDecimal.compare(other: BigDecimal): Boolean = this.compareTo(other) == 0

fun String.toMaskedString() = "${this.first()}*****${this.last()}"