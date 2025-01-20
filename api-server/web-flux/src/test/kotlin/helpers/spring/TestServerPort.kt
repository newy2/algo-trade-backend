package helpers.spring

object TestServerPort {
    @Volatile
    private var port = 9200

    fun nextValue() = port++
}
