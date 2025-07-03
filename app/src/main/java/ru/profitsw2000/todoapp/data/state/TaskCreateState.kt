package ru.profitsw2000.todoapp.data.state

sealed class TaskCreateState {
    data object Success: TaskCreateState()
    data class Error(val errorMessage: String): TaskCreateState()
    data object Loading: TaskCreateState()
}