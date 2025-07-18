package ru.profitsw2000.todoapp.data.state

sealed class TaskCreateState {
    data class LoadSuccess(val tasksListSize: Int): TaskCreateState()
    data object CreateSuccess: TaskCreateState()
    data class Error(val errorMessage: String): TaskCreateState()
    data object Loading: TaskCreateState()
}