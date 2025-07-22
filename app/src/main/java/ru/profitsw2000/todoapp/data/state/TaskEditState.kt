package ru.profitsw2000.todoapp.data.state

import ru.profitsw2000.todoapp.data.room.model.TaskModel

sealed class TaskEditState {
    data class LoadSuccess(val taskModel: TaskModel, val tasksListSize: Int): TaskEditState()
    data object EditSuccess: TaskEditState()
    data class Error(val errorMessage: String): TaskEditState()
    data object Loading: TaskEditState()
}