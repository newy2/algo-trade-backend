package helpers

fun getSystemProperty(name: String): String =
    System.getProperty(name, System.getenv(name))