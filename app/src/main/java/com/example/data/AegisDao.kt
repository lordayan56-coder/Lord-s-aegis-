package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AegisDao {

    // --- MEMORIES ---
    @Query("SELECT * FROM aegis_memories ORDER BY createdAt DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM aegis_memories WHERE category = :category ORDER BY createdAt DESC")
    fun getMemoriesByCategory(category: String): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM aegis_memories WHERE `key` LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun searchMemories(query: String): Flow<List<MemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity): Long

    @Query("DELETE FROM aegis_memories WHERE id = :id")
    suspend fun deleteMemoryById(id: Long)

    @Query("DELETE FROM aegis_memories")
    suspend fun clearAllMemories()

    // --- AUDIT LOGS ---
    @Query("SELECT * FROM aegis_audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity): Long

    // --- PROJECT MODULES ---
    @Query("SELECT * FROM aegis_modules ORDER BY name ASC")
    fun getAllModules(): Flow<List<ProjectModuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModules(modules: List<ProjectModuleEntity>)

    @Update
    suspend fun updateModule(module: ProjectModuleEntity)

    @Query("UPDATE aegis_modules SET status = :status WHERE id = :id")
    suspend fun updateModuleStatus(id: String, status: String)
}
