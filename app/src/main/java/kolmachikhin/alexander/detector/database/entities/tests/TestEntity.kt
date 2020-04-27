package kolmachikhin.alexander.detector.database.entities.tests

import androidx.room.Entity
import androidx.room.PrimaryKey
import kolmachikhin.alexander.detector.constants.TEST_QUESTIONS_COUNT
import kolmachikhin.alexander.detector.constants.VOID_ID

@Entity(tableName = "tests")
data class TestEntity(
    @PrimaryKey
    val id: Int = VOID_ID,
    val name: String = "",
    val answers: ArrayList<Int> = ArrayList()
)