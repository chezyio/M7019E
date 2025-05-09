package com.m7019e.couchpotato.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [MovieEntity::class, PopularMovieEntity::class, TopRatedMovieEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    // interface containing methods for performing CRUD operations on the MovieEntity table
    abstract fun movieDao(): MovieDAO
}