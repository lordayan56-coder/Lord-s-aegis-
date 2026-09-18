package com.example.data

import kotlinx.coroutines.flow.Flow

class AegisRepository(private val dao: AegisDao) {

    val allMemories: Flow<List<MemoryEntity>> = dao.getAllMemories()
    val allModules: Flow<List<ProjectModuleEntity>> = dao.getAllModules()
    val recentAuditLogs: Flow<List<AuditLogEntity>> = dao.getRecentAuditLogs()

    fun getMemoriesByCategory(category: String): Flow<List<MemoryEntity>> =
        dao.getMemoriesByCategory(category)

    fun searchMemories(query: String): Flow<List<MemoryEntity>> =
        dao.searchMemories(query)

    suspend fun saveMemory(category: String, key: String, content: String, isPrivileged: Boolean = false): Long {
        return dao.insertMemory(
            MemoryEntity(
                category = category,
                key = key,
                content = content,
                isPrivileged = isPrivileged
            )
        )
    }

    suspend fun deleteMemory(id: Long) {
        dao.deleteMemoryById(id)
    }

    suspend fun clearMemories() {
        dao.clearAllMemories()
    }

    suspend fun logAction(action: String, details: String, authorizedByLord: Boolean, severity: String = "INFO"): Long {
        return dao.insertAuditLog(
            AuditLogEntity(
                action = action,
                details = details,
                authorizedByLord = authorizedByLord,
                severity = severity
            )
        )
    }

    suspend fun updateModuleStatus(moduleId: String, status: String) {
        dao.updateModuleStatus(moduleId, status)
    }

    suspend fun updateModule(module: ProjectModuleEntity) {
        dao.updateModule(module)
    }
}
