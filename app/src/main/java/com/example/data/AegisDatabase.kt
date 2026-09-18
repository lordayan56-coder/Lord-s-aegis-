package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [MemoryEntity::class, AuditLogEntity::class, ProjectModuleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AegisDatabase : RoomDatabase() {

    abstract fun aegisDao(): AegisDao

    companion object {
        @Volatile
        private var INSTANCE: AegisDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AegisDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AegisDatabase::class.java,
                    "aegis_master_database"
                )
                    .addCallback(AegisDatabaseCallback(scope))
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AegisDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.aegisDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: AegisDao) {
                // Initial Owner Identity & Core Memory
                dao.insertMemory(
                    MemoryEntity(
                        category = "PREFERENCE",
                        key = "owner_title",
                        content = "Lord",
                        isPrivileged = true
                    )
                )
                dao.insertMemory(
                    MemoryEntity(
                        category = "PREFERENCE",
                        key = "voice_mode",
                        content = "Deep Calm Authoritative (Pitch 0.72x, Speed 0.95x)",
                        isPrivileged = false
                    )
                )
                dao.insertMemory(
                    MemoryEntity(
                        category = "PREFERENCE",
                        key = "wake_phrase",
                        content = "LORD'S AEGIS / AEGIS",
                        isPrivileged = false
                    )
                )
                dao.insertMemory(
                    MemoryEntity(
                        category = "PROJECT",
                        key = "directive",
                        content = "Act as Lord's personal intelligent guardian, tactical HUD, and executive command system.",
                        isPrivileged = false
                    )
                )
                dao.insertMemory(
                    MemoryEntity(
                        category = "CONFIG",
                        key = "security_clearance",
                        content = "CLEARANCE ALPHA-ZERO (Exclusive to Lord)",
                        isPrivileged = true
                    )
                )

                // Initial Subsystem Modules
                val initialModules = listOf(
                    ProjectModuleEntity(
                        id = "mod_ai_brain",
                        name = "Aegis Neural Brain",
                        category = "Intelligence",
                        version = "3.5.2",
                        status = "ONLINE",
                        description = "Context reasoning, multi-turn analysis & Gemini intelligence matrix.",
                        isPrivileged = false,
                        latencyMs = 42
                    ),
                    ProjectModuleEntity(
                        id = "mod_voice",
                        name = "Acoustic Vocal Core",
                        category = "Audio",
                        version = "2.1.0",
                        status = "ONLINE",
                        description = "Low-latency deep male TTS & dynamic spectral RMS reactive stream.",
                        isPrivileged = false,
                        latencyMs = 18
                    ),
                    ProjectModuleEntity(
                        id = "mod_wake_word",
                        name = "Wake Phrase Monitor",
                        category = "Sensory",
                        version = "1.8.4",
                        status = "ONLINE",
                        description = "Continuous listening detector for 'LORD'S AEGIS' and 'AEGIS'.",
                        isPrivileged = false,
                        latencyMs = 12
                    ),
                    ProjectModuleEntity(
                        id = "mod_memory",
                        name = "Quantum Memory Bank",
                        category = "Storage",
                        version = "2.4.0",
                        status = "ONLINE",
                        description = "Encrypted local Room database preserving owner preferences & context.",
                        isPrivileged = false,
                        latencyMs = 4
                    ),
                    ProjectModuleEntity(
                        id = "mod_security",
                        name = "Owner Security Matrix",
                        category = "Security",
                        version = "4.0.1",
                        status = "ONLINE",
                        description = "Biometric thumbprint authorization & cryptographic owner cipher locks.",
                        isPrivileged = true,
                        latencyMs = 6
                    ),
                    ProjectModuleEntity(
                        id = "mod_self_improve",
                        name = "Self-Improvement Engine",
                        category = "Evolution",
                        version = "1.2.0",
                        status = "STANDBY",
                        description = "Project architecture inspection, code diagnostic & safe mutation workflow.",
                        isPrivileged = true,
                        latencyMs = 85
                    ),
                    ProjectModuleEntity(
                        id = "mod_system_telemetry",
                        name = "System Telemetry Daemon",
                        category = "Sensors",
                        version = "2.0.0",
                        status = "ONLINE",
                        description = "Live battery, RAM, network latency & thread load monitor.",
                        isPrivileged = false,
                        latencyMs = 8
                    ),
                    ProjectModuleEntity(
                        id = "mod_tools",
                        name = "Tactical Tool Suite",
                        category = "Execution",
                        version = "2.1.1",
                        status = "ONLINE",
                        description = "Multi-step tool runner: time, calculations, memory control & diagnostics.",
                        isPrivileged = false,
                        latencyMs = 15
                    )
                )
                dao.insertModules(initialModules)

                // Initial Audit Log
                dao.insertAuditLog(
                    AuditLogEntity(
                        action = "AEGIS_BOOT_SEQUENCE",
                        details = "All primary defense, sensory, and neural systems initialized for Lord.",
                        authorizedByLord = true,
                        severity = "INFO"
                    )
                )
            }
        }
    }
}
