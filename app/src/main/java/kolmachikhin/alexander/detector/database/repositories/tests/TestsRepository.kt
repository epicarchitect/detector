package kolmachikhin.alexander.detector.database.repositories.tests

import android.app.Application
import kolmachikhin.alexander.detector.database.AppDatabase
import kolmachikhin.alexander.detector.database.entities.tests.TestEntity
import java.util.concurrent.Executors

class TestsRepository private constructor(app: Application) {

    val dao = AppDatabase(app).testsDao()
    val executor = Executors.newSingleThreadExecutor()

    fun save(entity: TestEntity, onDoneListener: () -> Unit = {}) {
        executor.execute {
            dao.save(entity)
            onDoneListener()
        }
    }

    fun delete(entity: TestEntity, onDoneListener: () -> Unit = {}) {
        executor.execute {
            dao.delete(entity)
            onDoneListener()
        }
    }

    fun getLiveEntities() = dao.getLiveEntities()

    fun getEntities() = dao.getEntities()

    fun getLiveEntityById(id: Int) = dao.getLiveEntityById(id)

    fun getEntityById(id: Int) = dao.getEntityById(id)

    companion object {
        var instance: TestsRepository? = null
        val LOCK = Any()

        operator fun invoke(app: Application) = instance ?: synchronized(LOCK) {
            instance ?: TestsRepository(app).also {
                instance = it
            }
        }
    }
}