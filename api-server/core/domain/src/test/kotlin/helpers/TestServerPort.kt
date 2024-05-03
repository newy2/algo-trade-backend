package helpers

object TestServerPort {
    @Volatile
    private var port = 9100

    fun nextValue() = port++
}
