rootProject.name = "algo-trade"
include(
    "api-server:web-mvc",
    "api-server:web-flux",
    "api-server:core:domain",
    "api-server:core:blocking-based-application",
    "api-server:core:coroutine-based-application",
)
