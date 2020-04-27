package kolmachikhin.alexander.detector.controllers.tests

import kolmachikhin.alexander.detector.analyse.Answer
import kolmachikhin.alexander.detector.database.entities.tests.TestEntity

object Mapper {

    fun toModel(entity: TestEntity) =
        TestModel(
            entity.id,
            entity.name,
            entity.answers.map { Answer[it] } as ArrayList<Answer>
        )

    fun toEntity(model: TestModel) =
        TestEntity(
            model.id,
            model.name,
            model.answers.map { Answer[it] } as ArrayList<Int>
        )

    fun toModelList(list: List<TestEntity>) = list.map { toModel(it) }

}