package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "aegis_memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // "PREFERENCE", "PROJECT", "FACT", "CONVERSATION", "SYSTEM"
    val key: String,
    val content: String,
    val isPrivileged: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "aegis_audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String,
    val details: String,
    val authorizedByLord: Boolean,
    val severity: String = "INFO", // "INFO", "PRIVILEGED", "WARNING", "SECURITY"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "aegis_modules")
data class ProjectModuleEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val version: String,
    val status: String, // "ONLINE", "STANDBY", "OPTIMIZING", "DISABLED"
    val description: String,
    val isPrivileged: Boolean,
    val latencyMs: Int,
    val lastCheck: Long = System.currentTimeMillis()
)
