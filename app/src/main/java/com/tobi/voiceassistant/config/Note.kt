package com.tobi.voiceassistant.config

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(@PrimaryKey(autoGenerate = true) val id: Int, val title: String, val body: String, val date: String)