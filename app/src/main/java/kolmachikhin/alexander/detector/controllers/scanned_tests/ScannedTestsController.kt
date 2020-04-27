package kolmachikhin.alexander.detector.controllers.scanned_tests

import android.app.Application
import android.util.Log
import androidx.lifecycle.Transformations
import kolmachikhin.alexander.detector.controllers.IdCounter
import kolmachikhin.alexander.detector.controllers.tests.TestsController
import kolmachikhin.alexander.detector.database.repositories.scanned_tests.ScannedTestsRepository

class ScannedTestsController private constructor(app: Application) {

    val idCounter = IdCounter(app)
    val testsController = TestsController(app)
    val repository = ScannedTestsRepository(app)

    fun save(model: ScannedTestModel, onDoneListener: (ScannedTestModel) -> Unit = {}) {
        val id = if (model.isVoid) idCounter.nextId() else model.id
        val newModel = model.copy(id = id)
        repository.save(Mapper.toEntity(newModel)) {
            onDoneListener(newModel)
        }
    }

    fun delete(model: ScannedTestModel, onDoneListener: () -> Unit = {}) {
        repository.delete(Mapper.toEntity(model), onDoneListener)
    }

    fun getLiveScannedTests() = Transformations.map(repository.getLiveEntities()) { list ->
        Mapper.toModelList(list) {
            testsController.getTestById(it)
        }
    }

    fun getScannedTests() = Mapper.toModelList(repository.getEntities()) {
        testsController.getTestById(it)
    }

    fun getLiveScannedTestById(id: Int) = Transformations.map(repository.getLiveEntityById(id)) {
        it?.let { entity ->
            testsController.getTestById(entity.testId)?.let { test ->
                Mapper.toModel(entity, test)
            }
        }
    }

    fun getScannedTestById(id: Int) = repository.getEntityById(id)?.let { entity ->
        testsController.getTestById(entity.testId)?.let { test ->
            Mapper.toModel(entity, test)
        }
    }

    fun getLiveScannedTestByTestId(testId: Int) = Transformations.map(repository.getLiveEntityByTestId(testId)) {
        it?.let { entity ->
            testsController.getTestById(entity.testId)?.let { test ->
                Mapper.toModel(entity, test)
            }
        }
    }

    fun getScannedTestByTestId(testId: Int) = repository.getEntityByTestId(testId)?.let { entity ->
        testsController.getTestById(entity.testId)?.let { test ->
            Mapper.toModel(entity, test)
        }
    }

    companion object {
        var instance: ScannedTestsController? = null
        val LOCK = Any()

        operator fun invoke(app: Application) = instance ?: synchronized(LOCK) {
            instance ?: ScannedTestsController(app).also {
                instance = it
            }
        }
    }
}