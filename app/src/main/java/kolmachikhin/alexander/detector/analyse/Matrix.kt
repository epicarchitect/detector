package kolmachikhin.alexander.detector.analyse

class Matrix<T> {

    val map: HashMap<Int, HashMap<Int, T>> = HashMap()

    operator fun set(columnIndex: Int, rowIndex: Int, value: T) {
        var column = map[columnIndex]

        if (column == null) {
            column = HashMap()
            map[columnIndex] = column
        }

        column[rowIndex] = value
    }

    operator fun get(columnIndex: Int, rowIndex: Int) = map[columnIndex]?.get(rowIndex)

    fun forEach(l: (T) -> Unit) {
        map.forEach { column ->
            column.value.forEach {
                l.invoke(it.value)
            }
        }
    }

    fun isExist(x: Int, y: Int) = get(x, y) != null

}