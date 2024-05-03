package helpers

object TestServerPort {
    @Volatile
    private var port = 9200

    fun nextValue() = port++
}
