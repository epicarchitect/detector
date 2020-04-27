package kolmachikhin.alexander.detector.database.entities.scanned_tests

import androidx.lifecycle.LiveData
import androidx.room.*
import kolmachikhin.alexander.detector.database.entities.tests.TestEntity

@Dao
interface ScannedTestsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(entity: ScannedTestEntity)

    @Delete
    fun delete(entity: ScannedTestEntity)

    @Query("SELECT * FROM scanned_tests")
    fun getLiveEntities(): LiveData<List<ScannedTestEntity>>

    @Query("SELECT * FROM scanned_tests")
    fun getEntities(): List<ScannedTestEntity>

    @Query("SELECT * FROM scanned_tests WHERE id = :id")
    fun getLiveEntityById(id: Int): LiveData<ScannedTestEntity?>

    @Query("SELECT * FROM scanned_tests WHERE id = :id")
    fun getEntityById(id: Int): ScannedTestEntity?

    @Query("SELECT * FROM scanned_tests WHERE testId = :testId")
    fun getLiveEntityByTestId(testId: Int): LiveData<ScannedTestEntity?>

    @Query("SELECT * FROM scanned_tests WHERE testId = :testId")
    fun getEntityByTestId(testId: Int): ScannedTestEntity?

}