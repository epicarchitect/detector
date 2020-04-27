package kolmachikhin.alexander.detector.constants

import kolmachikhin.alexander.detector.analyse.Answer

const val VOID_ID = -1

const val TEST_ROWS_IN_BOCK = 4
const val TEST_COLUMNS_IN_BLOCK = 14
const val TEST_QUESTIONS_COUNT = 56

fun emptyAnswerList(): ArrayList<Answer> {
    val list = ArrayList<Answer>()
    for (x in 0 until TEST_QUESTIONS_COUNT) {
        list.add(Answer.NULL)
    }
    return list
}