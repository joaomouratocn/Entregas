package com.example.entregas.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ReleaseEntity::class], version = 1, exportSchema = true)
abstract class AppDataBase:RoomDatabase() {
    abstract fun releaseDao():ReleaseDao
    companion object{
        @Volatile
        private var db : AppDataBase? = null
        fun getInstance(context: Context):AppDataBase{
            return db ?: Room.databaseBuilder(
                context,
                AppDataBase::class.java,
                "deliveryApp.db"
            ).build().also {
                db = it
            }
        }
    }
}