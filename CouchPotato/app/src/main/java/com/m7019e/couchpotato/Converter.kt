package com.m7019e.couchpotato.database

import androidx.room.TypeConverter
import com.m7019e.couchpotato.Genre
import org.json.JSONArray
import org.json.JSONObject

class Converters {
    @TypeConverter

    // converts list into json array
    fun fromGenreList(genres: List<Genre>): String {
        val jsonArray = JSONArray()
        genres.forEach { genre ->
            val json = JSONObject().apply {
                put("id", genre.id)
                put("name", genre.name)
            }
            jsonArray.put(json)
        }
        // '[{"id":1,"name":"Action"},{"id":2,"name":"Comedy"}]'
        return jsonArray.toString()
    }

    @TypeConverter

    // convert JSON string back into a list of Genre objects
    fun toGenreList(genresString: String): List<Genre> {
        val jsonArray = JSONArray(genresString)
        val genres = mutableListOf<Genre>()
        for (i in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(i)
            genres.add(
                Genre(
                    id = json.getInt("id"),
                    name = json.getString("name")
                )
            )
        }
        // List<Genre> = [Genre(id=1, name="Action"), Genre(id=2, name="Comedy")]
        return genres
    }
}