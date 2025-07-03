package ru.profitsw2000.todoapp.data.state

import ru.profitsw2000.todoapp.data.room.model.TaskModel

sealed class TasksRequestState {
    data class Success(val taskModelList: List<TaskModel>) : TasksRequestState()
    data class Error(val errorMessage: String) : TasksRequestState()
    data object Loading : TasksRequestState()
}