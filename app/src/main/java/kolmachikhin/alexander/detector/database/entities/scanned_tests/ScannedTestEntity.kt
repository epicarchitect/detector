package kolmachikhin.alexander.detector.database.entities.scanned_tests

import androidx.room.Entity
import androidx.room.PrimaryKey
import kolmachikhin.alexander.detector.constants.TEST_QUESTIONS_COUNT
import kolmachikhin.alexander.detector.constants.VOID_ID

@Entity(tableName = "scanned_tests")
data class ScannedTestEntity(
    @PrimaryKey
    val id: Int = VOID_ID,
    val testId: Int = VOID_ID,
    val studentName: String = "",
    val answers: ArrayList<Int> = ArrayList()
)