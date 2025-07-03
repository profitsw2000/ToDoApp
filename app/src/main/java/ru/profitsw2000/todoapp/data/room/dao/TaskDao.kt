package ru.profitsw2000.todoapp.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.profitsw2000.todoapp.data.room.model.TaskModel


@Dao
interface TaskDao {
    @Query("SELECT * FROM TaskModel")
    suspend fun all(): List<TaskModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(taskModel: TaskModel)

    @Update
    fun update(taskModel: TaskModel)

    @Delete
    fun delete(taskModel: TaskModel)
}