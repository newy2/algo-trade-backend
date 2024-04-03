package com.newy.algotrade.domain.chart

interface Rule {
    fun isSatisfied(index: Int): Boolean
}