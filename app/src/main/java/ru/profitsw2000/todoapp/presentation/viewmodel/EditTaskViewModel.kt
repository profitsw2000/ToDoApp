package ru.profitsw2000.todoapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.profitsw2000.todoapp.data.domain.TasksRepository
import ru.profitsw2000.todoapp.data.room.model.TaskModel
import ru.profitsw2000.todoapp.data.state.TaskCreateState

class EditTaskViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _tasksLiveData = MutableLiveData<TaskCreateState>()
    val tasksLiveData: LiveData<TaskCreateState> by this::_tasksLiveData

    fun editTask(taskModel: TaskModel) {
        _tasksLiveData.value = TaskCreateState.Loading
        viewModelScope.launch {
            _tasksLiveData.value = updateTask(taskModel)
        }
    }

    private suspend fun updateTask(taskModel: TaskModel): TaskCreateState {
        val deferred: Deferred<TaskCreateState> = viewModelScope.async{
            try {
                tasksRepository.updateTask(taskModel)
                TaskCreateState.CreateSuccess
            } catch (exception: Exception) {
                val message = exception.message ?: ""
                TaskCreateState.Error(message)
            }
        }
        return deferred.await()
    }
}