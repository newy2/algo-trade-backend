package helpers

object TestEnv {
    object ByBit {
        const val url: String = "https://api-testnet.bybit.com"
        val apiKey: String = System.getenv("X-BY-BIT-API-KEY")
        val apiSecret: String = System.getenv("X-BY-BIT-API-SECRET")
    }

    object KIS {
        const val url: String = "https://openapivts.koreainvestment.com:29443"
        val apiKey: String = System.getenv("X-KIS-API-KEY")
        val apiSecret: String = System.getenv("X-KIS-API-SECRET")
    }
}
