package kolmachikhin.alexander.detector.database.repositories.scanned_tests

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kolmachikhin.alexander.detector.database.AppDatabase
import kolmachikhin.alexander.detector.database.entities.scanned_tests.ScannedTestEntity
import java.util.concurrent.Executors

class ScannedTestsRepository private constructor(app: Application) {

    val dao = AppDatabase(app).scannedTestsDao()
    val executor = Executors.newSingleThreadExecutor()

    fun save(entity: ScannedTestEntity, onDoneListener: () -> Unit = {}) {
        executor.execute {
            dao.save(entity)
            onDoneListener()
        }
    }

    fun delete(entity: ScannedTestEntity, onDoneListener: () -> Unit = {}) {
        executor.execute {
            dao.delete(entity)
            onDoneListener()
        }
    }

    fun getLiveEntities() = dao.getLiveEntities()

    fun getEntities() = dao.getEntities()

    fun getLiveEntityById(id: Int) = dao.getLiveEntityById(id)

    fun getEntityById(id: Int) = dao.getEntityById(id)

    fun getLiveEntityByTestId(testId: Int) = dao.getLiveEntityByTestId(testId)

    fun getEntityByTestId(testId: Int) = dao.getEntityByTestId(testId)

    companion object {
        var instance: ScannedTestsRepository? = null
        val LOCK = Any()

        operator fun invoke(app: Application) = instance ?: synchronized(LOCK) {
            instance ?: ScannedTestsRepository(app).also {
                instance = it
            }
        }
    }
}