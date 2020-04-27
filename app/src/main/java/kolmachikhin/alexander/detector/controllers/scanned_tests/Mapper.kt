package kolmachikhin.alexander.detector.controllers.scanned_tests

import kolmachikhin.alexander.detector.analyse.Answer
import kolmachikhin.alexander.detector.controllers.tests.TestModel
import kolmachikhin.alexander.detector.database.entities.scanned_tests.ScannedTestEntity

object Mapper {

    fun toModel(entity: ScannedTestEntity, test: TestModel) =
        ScannedTestModel(
            entity.id,
            test,
            entity.studentName,
            entity.answers.map { Answer[it] } as ArrayList<Answer>
        )

    fun toEntity(model: ScannedTestModel) =
        ScannedTestEntity(
            model.id,
            model.test.id,
            model.studentName,
            model.answers.map { Answer[it] } as ArrayList<Int>
        )

    fun toModelList(list: List<ScannedTestEntity>, testIdCallback: (Int) -> TestModel?): List<ScannedTestModel> {
        val models = ArrayList<ScannedTestModel>()
        list.forEach { entity ->
            testIdCallback(entity.testId)?.let { test ->
                models.add(toModel(entity, test))
            }
        }
        return models
    }

}