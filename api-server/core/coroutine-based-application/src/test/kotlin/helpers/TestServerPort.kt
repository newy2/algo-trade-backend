package helpers

object TestServerPort {
    @Volatile
    private var port = 9500

    fun nextValue() = port++
}
