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
import ru.profitsw2000.todoapp.data.state.TasksRequestState

class MainViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _tasksLiveData = MutableLiveData<TasksRequestState>()
    val tasksLiveData: LiveData<TasksRequestState> by this::_tasksLiveData

    fun getTasksList() {
        _tasksLiveData.value = TasksRequestState.Loading
        viewModelScope.launch {
            try {
                val tasksList = tasksRepository.getAllTasks()
                _tasksLiveData.value = TasksRequestState.Success(tasksList)
            } catch (exception: Exception) {
                _tasksLiveData.value = exception.message?.let { TasksRequestState.Error(it) }
            }
        }
    }

    fun updateTasksList(tasksList: List<TaskModel>) {
        _tasksLiveData.value = TasksRequestState.Loading
        viewModelScope.launch {
            val isUpdated = update(tasksList)
            if (isUpdated) getTasksList()
        }
    }

    private suspend fun update(tasksList: List<TaskModel>): Boolean {
        val deferred: Deferred<Boolean> = viewModelScope.async{
            try {
                tasksRepository.updateTasksList(tasksList)
                true
            } catch (exception: Exception) {
                val errorMessage = exception.message ?: ""
                _tasksLiveData.value = TasksRequestState.Error(errorMessage)
                false
            }
        }
        return deferred.await()
    }

    fun deleteTask(taskModel: TaskModel) {
        _tasksLiveData.value = TasksRequestState.Loading
        viewModelScope.launch {
            val isDeleted = delete(taskModel)
            if (isDeleted) getTasksList()
        }
    }

    private suspend fun delete(taskModel: TaskModel): Boolean {
        val deferred: Deferred<Boolean> = viewModelScope.async{
            try {
                tasksRepository.deleteTask(taskModel)
                true
            } catch (exception: Exception) {
                val errorMessage = exception.message ?: ""
                _tasksLiveData.value = TasksRequestState.Error(errorMessage)
                false
            }
        }
        return deferred.await()
    }
}