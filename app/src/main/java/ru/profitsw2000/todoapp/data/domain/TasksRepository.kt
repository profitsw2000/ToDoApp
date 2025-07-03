package ru.profitsw2000.todoapp.data.domain

import ru.profitsw2000.todoapp.data.room.model.TaskModel

interface TasksRepository {

    suspend fun getAllTasks(): List<TaskModel>

    suspend fun insertTask(taskModel: TaskModel)

    suspend fun updateTask(taskModel: TaskModel)

    suspend fun updateTasksList(tasksModelList: List<TaskModel>)

    suspend fun deleteTask(taskModel: TaskModel)
}