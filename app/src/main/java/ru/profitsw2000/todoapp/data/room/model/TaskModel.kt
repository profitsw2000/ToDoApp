package ru.profitsw2000.todoapp.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TaskModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val priority: Int,
    val taskText: String
)
