package helpers

object TestEnv {
    object ByBit {
        const val url: String = "https://api-testnet.bybit.com"
        const val socketUrl: String = "wss://stream.bybit.com/v5/public/spot"
        val apiKey: String = getSystemProperty("X_BY_BIT_API_KEY")
        val apiSecret: String = getSystemProperty("X_BY_BIT_API_SECRET")
    }

    object LsSec {
        const val url: String = "https://openapi.ls-sec.co.kr:8080"
        val apiKey: String = getSystemProperty("X_LS_SEC_API_KEY")
        val apiSecret: String = getSystemProperty("X_LS_SEC_API_SECRET")
    }
}
