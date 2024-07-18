package helpers

object TestEnv {
    object ByBit {
        const val url: String = "https://api-testnet.bybit.com"
        const val socketUrl: String = "wss://stream.bybit.com/v5/public/spot"
        val apiKey: String = System.getenv("X_BY_BIT_API_KEY")
        val apiSecret: String = System.getenv("X_BY_BIT_API_SECRET")
    }

    object LsSec {
        const val url: String = "https://openapi.ls-sec.co.kr:8080"
        val apiKey: String = System.getenv("X_LS_SEC_API_KEY")
        val apiSecret: String = System.getenv("X_LS_SEC_API_SECRET")
    }
}
