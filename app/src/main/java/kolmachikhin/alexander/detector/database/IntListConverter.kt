package kolmachikhin.alexander.detector.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class IntListConverter {

    @TypeConverter
    fun toJson(list: ArrayList<Int>) = Gson().toJson(list)

    @TypeConverter
    fun fromJson(json: String) = Gson().fromJson(json, object : TypeToken<ArrayList<Int>>() {}.type) as ArrayList<Int>

}