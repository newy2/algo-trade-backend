rootProject.name = "algo-trade"
include(
    "api-server:web-flux",
    "api-server:core:domain",
    "api-server:core:coroutine-based-application",
    "ddl:liquibase"
)
