package ru.profitsw2000.todoapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.profitsw2000.todoapp.data.domain.TasksRepository
import ru.profitsw2000.todoapp.data.room.model.TaskModel
import ru.profitsw2000.todoapp.data.state.TaskCreateState
import ru.profitsw2000.todoapp.data.state.TaskEditState
import ru.profitsw2000.todoapp.data.state.TasksRequestState
import ru.profitsw2000.todoapp.utility.TAG

class EditTaskViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _tasksLiveData = MutableLiveData<TaskEditState>()
    val tasksLiveData: LiveData<TaskEditState> by this::_tasksLiveData
    private lateinit var taskModelList: List<TaskModel>
    private lateinit var taskModel: TaskModel
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val lifecycleScope = CoroutineScope(Dispatchers.Main)

    fun getTask(position: Int) {
        _tasksLiveData.value = TaskEditState.Loading
        lifecycleScope.launch {
            try {
                taskModelList = getToDoList()
                if (taskModelList.size > position) {
                    taskModel = taskModelList[position]
                    _tasksLiveData.value = TaskEditState.LoadSuccess(taskModel, taskModelList.size)
                }
                else TaskCreateState.Error("")
            } catch (exception: Exception) {
                val message = exception.message ?: ""
                TaskCreateState.Error(message)
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

    fun editTask(taskModel: TaskModel) {
        if (taskModel.priority == this.taskModel.priority) TaskEditState.EditSuccess
        else updateTasksList(getTasksListWithNewPriorities(taskModel))
    }

    private fun getTasksListWithNewPriorities(newTaskModel: TaskModel): List<TaskModel> {
        val newPriority = newTaskModel.priority
        val oldPriority = taskModel.priority
        val newTasksList: MutableList<TaskModel> = mutableListOf()

        taskModelList.forEach {
            when {
                it.priority < oldPriority && it.priority < newPriority -> newTasksList.add(it)
                it.priority > oldPriority && it.priority > newPriority -> newTasksList.add(it)
                it.priority in (oldPriority + 1)..newPriority -> {
                    newTasksList.add(
                        TaskModel(
                        id = it.id,
                        priority = it.priority - 1,
                        taskText = it.taskText
                        )
                    )
                }
                it.priority in newPriority..<oldPriority -> {
                    newTasksList.add(TaskModel(
                        id = it.id,
                        priority = it.priority + 1,
                        taskText = it.taskText
                        )
                    )
                }
                it.priority == oldPriority -> {
                    newTasksList.add(newTaskModel)
                }
            }
        }

        return newTasksList
    }

    private fun updateTasksList(tasksList: List<TaskModel>) {
        _tasksLiveData.value = TaskEditState.Loading
        lifecycleScope.launch {
            val isUpdated = update(tasksList)
            if (isUpdated) TaskEditState.EditSuccess
        }
    }

    private suspend fun update(tasksList: List<TaskModel>): Boolean {
        val deferred: Deferred<Boolean> = ioScope.async{
            try {
                tasksRepository.updateTasksList(tasksList)
                true
            } catch (exception: Exception) {
                val errorMessage = exception.message ?: ""
                _tasksLiveData.value = TaskEditState.Error(errorMessage)
                false
            }
        }
        return deferred.await()
    }
/*    fun editTask(taskModel: TaskModel) {
        _tasksLiveData.value = TaskEditState.Loading
        viewModelScope.launch {
            _tasksLiveData.value = updateTask(taskModel)
        }
    }

    private suspend fun updateTask(taskModel: TaskModel): TaskEditState {
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
    }*/
}