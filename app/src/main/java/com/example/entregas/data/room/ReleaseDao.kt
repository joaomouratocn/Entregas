package com.example.entregas.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReleaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelease(releaseEntity: ReleaseEntity)

    @Query("SELECT * FROM ReleaseEntity ORDER BY releaseDate DESC, releaseId DESC")
    fun getAllReleases():Flow<List<ReleaseEntity>?>

    @Query("SELECT * FROM ReleaseEntity WHERE releaseOp = :releaseOp ORDER BY releaseDate DESC, releaseId DESC")
    fun getAllReleaseBy(releaseOp:String):Flow<List<ReleaseEntity>?>
}