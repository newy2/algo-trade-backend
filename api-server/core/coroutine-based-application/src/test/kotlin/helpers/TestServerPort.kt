package helpers

object TestServerPort {
    @Volatile
    private var port = 9000

    fun nextValue() = port++
}
