package helpers.spring

fun getSystemProperty(name: String): String =
    System.getProperty(name, System.getenv(name))