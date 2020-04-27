package kolmachikhin.alexander.detector.controllers.tests

import android.app.Application
import androidx.lifecycle.Transformations
import kolmachikhin.alexander.detector.controllers.IdCounter
import kolmachikhin.alexander.detector.database.repositories.tests.TestsRepository

class TestsController private constructor(app: Application) {

    val idCounter = IdCounter(app)
    val repository = TestsRepository(app)

    fun save(model: TestModel, onDoneListener: (TestModel) -> Unit = {}) {
        val id = if (model.isVoid) idCounter.nextId() else model.id
        val newModel = model.copy(id = id)
        repository.save(Mapper.toEntity(newModel)) {
            onDoneListener(newModel)
        }
    }

    fun delete(model: TestModel, onDoneListener: () -> Unit = {}) {
        repository.delete(Mapper.toEntity(model), onDoneListener)
    }

    fun getLiveTests() = Transformations.map(repository.getLiveEntities()) { Mapper.toModelList(it) }

    fun getTests() = Mapper.toModelList(repository.getEntities())

    fun getLiveTestById(id: Int) = Transformations.map(repository.getLiveEntityById(id)) {
        it?.let { Mapper.toModel(it) }
    }

    fun getTestById(id: Int) = repository.getEntityById(id)?.let { Mapper.toModel(it) }

    companion object {
        var instance: TestsController? = null
        val LOCK = Any()

        operator fun invoke(app: Application) = instance ?: synchronized(LOCK) {
            instance ?: TestsController(app).also {
                instance = it
            }
        }
    }
}