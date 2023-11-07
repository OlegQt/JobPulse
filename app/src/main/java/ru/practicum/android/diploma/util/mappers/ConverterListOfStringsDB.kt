package ru.practicum.android.diploma.util.mappers

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.practicum.android.diploma.db.entity.PhoneEntity

class ConverterListOfStringsDB {
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: List<String>): String {
        val gson = Gson()
        return gson.toJson(list)
    }


    @TypeConverter
    fun fromPhoneList(phones: List<PhoneEntity?>?): String? {
        if (phones == null) return null
        val gson = Gson()
        val type = object : TypeToken<List<PhoneEntity?>>() {}.type
        return gson.toJson(phones, type)
    }

    @TypeConverter
    fun toPhoneList(phoneString: String?): List<PhoneEntity?>? {
        if (phoneString == null) return null
        val gson = Gson()
        val type = object : TypeToken<List<PhoneEntity?>>() {}.type
        return gson.fromJson(phoneString, type)
    }
}
