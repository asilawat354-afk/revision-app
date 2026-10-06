package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: String)

    @Query("SELECT COUNT(*) FROM projects")
    fun getProjectCount(): Flow<Int>
}

@Dao
interface BuildDao {
    @Query("SELECT * FROM builds ORDER BY createdAt DESC")
    fun getAllBuilds(): Flow<List<BuildEntity>>

    @Query("SELECT * FROM builds WHERE id = :id LIMIT 1")
    suspend fun getBuildById(id: String): BuildEntity?

    @Query("SELECT * FROM builds WHERE status IN ('QUEUED', 'PREPARING', 'CONFIGURING', 'RUNNING_GRADLE', 'SIGNING', 'VERIFYING') ORDER BY createdAt DESC LIMIT 1")
    fun getActiveBuild(): Flow<BuildEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuild(build: BuildEntity)

    @Update
    suspend fun updateBuild(build: BuildEntity)

    @Query("DELETE FROM builds WHERE id = :id")
    suspend fun deleteBuildById(id: String)

    @Query("DELETE FROM builds")
    suspend fun clearAllBuilds()

    @Query("SELECT COUNT(*) FROM builds")
    fun getTotalBuildsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM builds WHERE status = 'COMPLETED'")
    fun getSuccessfulBuildsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM builds WHERE status = 'FAILED'")
    fun getFailedBuildsCount(): Flow<Int>
}
