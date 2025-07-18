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

class CreateTaskViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _tasksLiveData = MutableLiveData<TaskCreateState>()
    val tasksLiveData: LiveData<TaskCreateState> by this::_tasksLiveData

    private lateinit var data: List<TaskModel>

    fun getTasksList() {
        _tasksLiveData.value = TaskCreateState.Loading
        viewModelScope.launch {
            try {
                data = tasksRepository.getAllTasks()
                _tasksLiveData.value = TaskCreateState.LoadSuccess(data.size)
            } catch (exception: Exception) {
                val message = exception.message ?: ""
                TaskCreateState.Error(message)
            }
        }
    }

    fun createTask(taskModel: TaskModel) {
        _tasksLiveData.value = TaskCreateState.Loading
        viewModelScope.launch {
            _tasksLiveData.value = insertTask(taskModel)
        }
    }

    private fun getTasksListWithNewPriorities(newTaskPriority: Int): List<TaskModel> {
        val tasksList: MutableList<TaskModel> = mutableListOf()
        data.forEach {
            val newPriority = if (it.priority >= newTaskPriority) it.priority + 1
            else it.priority

            tasksList.add(
                TaskModel(
                    id = it.id,
                    priority = newPriority,
                    taskText = it.taskText
                )
            )
        }

        return tasksList
    }

    private suspend fun insertTask(taskModel: TaskModel): TaskCreateState {
        val deferred: Deferred<TaskCreateState> = viewModelScope.async{
            try {
                tasksRepository.updateTasksList(getTasksListWithNewPriorities(taskModel.priority))
                tasksRepository.insertTask(taskModel)
                TaskCreateState.CreateSuccess
            } catch (exception: Exception) {
                val message = exception.message ?: ""
                TaskCreateState.Error(message)
            }
        }
        return deferred.await()
    }
}