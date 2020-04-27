package kolmachikhin.alexander.detector.controllers.scanned_tests

import kolmachikhin.alexander.detector.analyse.Answer
import kolmachikhin.alexander.detector.constants.TEST_QUESTIONS_COUNT
import kolmachikhin.alexander.detector.constants.VOID_ID
import kolmachikhin.alexander.detector.constants.emptyAnswerList
import kolmachikhin.alexander.detector.controllers.Model
import kolmachikhin.alexander.detector.controllers.tests.TestModel

data class ScannedTestModel(
    override val id: Int = VOID_ID,
    val test: TestModel = TestModel(),
    val studentName: String = "",
    val answers: ArrayList<Answer> = emptyAnswerList()
) : Model(id)
