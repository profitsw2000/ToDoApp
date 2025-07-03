package ru.profitsw2000.todoapp.data.data

import ru.profitsw2000.todoapp.data.domain.TasksRepository
import ru.profitsw2000.todoapp.data.room.database.AppDatabase
import ru.profitsw2000.todoapp.data.room.model.TaskModel

class TasksRepositoryImpl(
    private val appDatabase: AppDatabase
) : TasksRepository {
    override suspend fun getAllTasks(): List<TaskModel> {
        return appDatabase.taskDao.all()
    }

    override suspend fun insertTask(taskModel: TaskModel) {
        appDatabase.taskDao.insert(taskModel)
    }

    override suspend fun updateTask(taskModel: TaskModel) {
        appDatabase.taskDao.update(taskModel)
    }

    override suspend fun deleteTask(taskModel: TaskModel) {
        appDatabase.taskDao.delete(taskModel)
    }
}