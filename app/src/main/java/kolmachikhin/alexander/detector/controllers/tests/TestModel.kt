package kolmachikhin.alexander.detector.controllers.tests

import androidx.room.PrimaryKey
import kolmachikhin.alexander.detector.analyse.Answer
import kolmachikhin.alexander.detector.constants.TEST_QUESTIONS_COUNT
import kolmachikhin.alexander.detector.constants.VOID_ID
import kolmachikhin.alexander.detector.constants.emptyAnswerList
import kolmachikhin.alexander.detector.controllers.Model

data class TestModel(
    override val id: Int = VOID_ID,
    val name: String = "",
    val answers: ArrayList<Answer> = emptyAnswerList()
) : Model(id)