package cloud.mike.divelog.ui.common

/** Decimates a list to [n] elements via downsampling. */
fun <E> List<E>.sample(n: Int): List<E> {
    if (isEmpty() || n > size) return this
    val steps = (size / n) + 1
    return windowed(size = 1, step = steps) { it.single() }
}
