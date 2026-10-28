package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BirthProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface BirthProfileDao {
    @Query("SELECT * FROM birth_profiles ORDER BY isFavorite DESC, createdAt DESC")
    fun getAllProfiles(): Flow<List<BirthProfile>>

    @Query("SELECT * FROM birth_profiles WHERE id = :id")
    suspend fun getProfileById(id: Long): BirthProfile?

    @Query("SELECT * FROM birth_profiles WHERE name LIKE '%' || :query || '%' OR locationName LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchProfiles(query: String): Flow<List<BirthProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: BirthProfile): Long

    @Update
    suspend fun updateProfile(profile: BirthProfile)

    @Delete
    suspend fun deleteProfile(profile: BirthProfile)

    @Query("UPDATE birth_profiles SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("SELECT COUNT(*) FROM birth_profiles")
    suspend fun getProfileCount(): Int
}
