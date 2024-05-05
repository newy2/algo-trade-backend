package helpers

object TestEnv {
    object ByBit {
        const val url: String = "https://api-testnet.bybit.com"
        val apiKey: String = System.getenv("X-BY-BIT-API-KEY")
        val apiSecret: String = System.getenv("X-BY-BIT-API-SECRET")
    }

    object EBest {
        const val url: String = "https://openapi.ebestsec.co.kr:8080"
        val apiKey: String = System.getenv("X-E-BEST-API-KEY")
        val apiSecret: String = System.getenv("X-E-BEST-API-SECRET")
    }
}
