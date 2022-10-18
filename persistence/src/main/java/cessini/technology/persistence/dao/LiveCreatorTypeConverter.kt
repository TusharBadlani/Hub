package cessini.technology.persistence.dao

import androidx.room.TypeConverter
import cessini.technology.model.Listener
import cessini.technology.model.LiveCreator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LiveCreatorTypeConverter {

    @TypeConverter
    fun fromListToJson(liveCreator: LiveCreator): String? {
        return Gson().toJson(liveCreator)
    }

    @TypeConverter
    fun fromJsonToList(liveCreator: String) : LiveCreator{
        return Gson().fromJson(liveCreator,LiveCreator::class.java)
    }
}

class ListenerTypeConverter{
    @TypeConverter
    fun fromListToJson(listeners: List<Listener>?): String? {
        return Gson().toJson(listeners)
    }

    @TypeConverter
    fun fromJsonToList(listeners: String) : List<Listener>?{
        return Gson().fromJson(listeners, object : TypeToken<List<Listener>>(){}.type)
    }
}

class StringTypeConverter{

    @TypeConverter
    fun fromListToJson(strings: List<String>?): String? {
        return Gson().toJson(strings)
    }

    @TypeConverter
    fun fromJsonToList(strings: String) : List<String>?{
        return Gson().fromJson(strings, object : TypeToken<List<String>>(){}.type)
    }
}