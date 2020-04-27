package kolmachikhin.alexander.detector.controllers

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class IdCounter private constructor(application: Application) {

    val preferences = application.getSharedPreferences(KEY, Context.MODE_PRIVATE)

    fun nextId(): Int {
        var id = preferences.getInt(KEY, 0)
        id++
        preferences.edit().putInt(KEY, id).apply()
        return id
    }

    companion object {
        const val KEY = "IdCounter"

        @Volatile
        var instance: IdCounter? = null
        val LOCK = Any()

        operator fun invoke(application: Application) = instance ?: synchronized(LOCK) {
            instance ?: IdCounter(application).also {
                instance = it
            }
        }
    }
}