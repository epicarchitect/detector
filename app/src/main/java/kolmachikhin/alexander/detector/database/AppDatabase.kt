package kolmachikhin.alexander.detector.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kolmachikhin.alexander.detector.database.entities.scanned_tests.ScannedTestEntity
import kolmachikhin.alexander.detector.database.entities.scanned_tests.ScannedTestsDao
import kolmachikhin.alexander.detector.database.entities.tests.TestEntity
import kolmachikhin.alexander.detector.database.entities.tests.TestsDao

@Database(entities = [
    TestEntity::class,
    ScannedTestEntity::class
], version = 1, exportSchema = false)
@TypeConverters(IntListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun testsDao(): TestsDao
    abstract fun scannedTestsDao(): ScannedTestsDao

    companion object {
        var instance: AppDatabase? = null
        val LOCK = Any()

        operator fun invoke(app: Application) = instance ?: synchronized(LOCK) {
            instance ?: build(app).also {
                instance = it
            }
        }

        fun build(app: Application) =
            Room.databaseBuilder(app, AppDatabase::class.java, "db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
    }
}