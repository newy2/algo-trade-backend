package helpers

interface BaseDisabledTest {
    fun hasNotLsSecApiInfo() = listOf(TestEnv.LsSec.apiKey, TestEnv.LsSec.apiSecret).contains("")
    fun hasNotByBitApiInfo() = listOf(TestEnv.ByBit.apiKey, TestEnv.ByBit.apiSecret).contains("")
    fun hasNotApiInfo() = hasNotLsSecApiInfo() || hasNotByBitApiInfo()
}