package com.tobi.voiceassistant.config

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun getNoteDao(): NoteDao

    companion object {
        private var INSTANCE: NoteDatabase? = null

        fun getInstance(context: Context): NoteDatabase? {
            if (INSTANCE == null) {
                synchronized(NoteDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            NoteDatabase::class.java, "note.db")
                            .build()
                }
            }

            return INSTANCE
        }
    }

    fun destroyInstance() {
        INSTANCE = null
    }
}