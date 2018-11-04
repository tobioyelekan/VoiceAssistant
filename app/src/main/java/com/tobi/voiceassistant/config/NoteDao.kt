package com.tobi.voiceassistant.config

import android.arch.persistence.room.*

@Dao
interface NoteDao {

    @Query("SELECT * FROM NOTES ORDER BY date DESC")
    fun getNotes(): MutableList<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note)

    @Query("UPDATE notes SET title = :title, body =:body WHERE id =:id")
    fun update(id: Int, title: String, body: String)

    @Update
    fun update(note: Note)

    @Query("DELETE FROM notes WHERE id =:id")
    fun delete(id: Int)

    @Delete
    fun delete(note: Note)
}