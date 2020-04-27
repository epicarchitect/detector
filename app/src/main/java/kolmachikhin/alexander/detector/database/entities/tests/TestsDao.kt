package kolmachikhin.alexander.detector.database.entities.tests

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TestsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(entity: TestEntity)

    @Delete
    fun delete(entity: TestEntity)

    @Query("SELECT * FROM tests")
    fun getLiveEntities(): LiveData<List<TestEntity>>

    @Query("SELECT * FROM tests")
    fun getEntities(): List<TestEntity>

    @Query("SELECT * FROM tests WHERE id = :id")
    fun getLiveEntityById(id: Int): LiveData<TestEntity?>

    @Query("SELECT * FROM tests WHERE id = :id")
    fun getEntityById(id: Int): TestEntity?

}