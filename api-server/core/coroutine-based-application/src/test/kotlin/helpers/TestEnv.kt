package helpers

object TestEnv {
    object ByBit {
        const val url: String = "https://api-testnet.bybit.com"
        const val socketUrl: String = "wss://stream.bybit.com/v5/public/spot"
        val apiKey: String = System.getenv("X-BY-BIT-API-KEY")
        val apiSecret: String = System.getenv("X-BY-BIT-API-SECRET")
    }

    object LsSec {
        const val url: String = "https://openapi.ls-sec.co.kr:8080"
        val apiKey: String = System.getenv("X-LS-SEC-API-KEY")
        val apiSecret: String = System.getenv("X-LS-SEC-API-SECRET")
    }
}
