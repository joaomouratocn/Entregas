package com.example.entregas.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.entregas.util.getDate

@Entity
data class ReleaseEntity(
    @PrimaryKey(autoGenerate = true)
    val releaseId:Int,
    val releaseOp:String,
    val releaseValue:Double,
    val releaseDesc:String,
    val releaseDate:String = getDate()
)
