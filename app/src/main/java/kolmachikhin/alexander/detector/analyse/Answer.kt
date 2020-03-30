package kolmachikhin.alexander.detector.analyse

enum class Answer {
    A, B, C, D,
    NULL { override fun toString() = "N" };

    companion object {

        fun get(position: Int) = when (position) {
            0 -> A
            1 -> B
            2 -> C
            3 -> D
            else -> NULL
        }

    }

}

