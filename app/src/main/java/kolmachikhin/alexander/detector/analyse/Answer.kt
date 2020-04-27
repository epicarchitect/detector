package kolmachikhin.alexander.detector.analyse

enum class Answer {
    A, B, C, D,
    NULL { override fun toString() = "N" };

    companion object {

        operator fun get(position: Int) = when (position) {
            0 -> A
            1 -> B
            2 -> C
            3 -> D
            else -> NULL
        }

        operator fun get(answer: Answer) = when (answer) {
            A -> 0
            B -> 1
            C -> 2
            D -> 3
            else -> 4
        }

    }

}

