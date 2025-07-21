package ru.profitsw2000.todoapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.profitsw2000.todoapp.data.domain.TasksRepository
import ru.profitsw2000.todoapp.data.room.model.TaskModel
import ru.profitsw2000.todoapp.data.state.TaskCreateState
import ru.profitsw2000.todoapp.data.state.TasksRequestState
import ru.profitsw2000.todoapp.utility.TAG

class MainViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _tasksLiveData = MutableLiveData<TasksRequestState>()
    val tasksLiveData: LiveData<TasksRequestState> by this::_tasksLiveData
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val lifecycleScope = CoroutineScope(Dispatchers.Main)
    private var tasksList: List<TaskModel> = arrayListOf()

    fun getTasksList() {
        _tasksLiveData.value = TasksRequestState.Loading
        lifecycleScope.launch {
            try {
                tasksList = getToDoList()
                _tasksLiveData.value = TasksRequestState.Success(tasksList)
                Log.d(TAG, "getTasksList: $tasksList")
            } catch (exception: Exception) {
                _tasksLiveData.value = exception.message?.let { TasksRequestState.Error(it) }
            }
        }
    }

    private suspend fun getToDoList(): List<TaskModel>  {
        val deferred: Deferred<List<TaskModel>> = ioScope.async{
            try {
                tasksRepository.getAllTasks()
            } catch (exception: Exception) {
                throw exception
            }
        }
        return deferred.await()
    }

    fun changeTask(position: Int, isIncreasePriority: Boolean) {
        if ((isIncreasePriority && position != 0)
            || (!isIncreasePriority && position != (tasksList.size - 1))) changeTaskPriority(position, isIncreasePriority)
    }

    private fun changeTaskPriority(position: Int, isIncreasePriority: Boolean) {
        val argument = if (isIncreasePriority) 1
        else -1
        val mutableTasksList: MutableList<TaskModel> = tasksList.toMutableList()
        val taskToChange = TaskModel(
            id = tasksList[position].id,
            priority = tasksList[position].priority - argument,
            taskText = tasksList[position].taskText
        )
        val nextTaskToChange = TaskModel(
            id = tasksList[position - argument].id,
            priority = tasksList[position - argument].priority + argument,
            taskText = tasksList[position - argument].taskText
        )

        mutableTasksList[position] = taskToChange
        mutableTasksList[position - argument] = nextTaskToChange
        updateTasksList(mutableTasksList)
    }

    private fun updateTasksList(tasksList: List<TaskModel>) {
        _tasksLiveData.value = TasksRequestState.Loading
        lifecycleScope.launch {
            val isUpdated = update(tasksList)
            if (isUpdated) getTasksList()
        }
    }

    private suspend fun update(tasksList: List<TaskModel>): Boolean {
        val deferred: Deferred<Boolean> = ioScope.async{
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

    fun deleteTask(position: Int) {
        _tasksLiveData.value = TasksRequestState.Loading
        lifecycleScope.launch {
            val isDeleted = delete(tasksList[position])
            if (isDeleted) updateTasksList(getTasksListWithNewPriorities(position))
        }
    }

    private suspend fun delete(taskModel: TaskModel): Boolean {
        val deferred: Deferred<Boolean> = ioScope.async{
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

    private fun getTasksListWithNewPriorities(position: Int): List<TaskModel> {
        val newTasksList: MutableList<TaskModel> = mutableListOf()

        tasksList.forEachIndexed { index, taskModel ->
            val newPriority = if (index > position) taskModel.priority - 1
            else taskModel.priority

            newTasksList.add(index, TaskModel(
                id = taskModel.id,
                priority = newPriority,
                taskText = taskModel.taskText
            ))
        }

        newTasksList.removeAt(position)
        return newTasksList
    }
}