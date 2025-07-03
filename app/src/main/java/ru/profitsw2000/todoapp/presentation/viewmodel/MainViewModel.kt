package ru.profitsw2000.todoapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.profitsw2000.todoapp.data.domain.TasksRepository
import ru.profitsw2000.todoapp.data.state.TasksRequestState

class MainViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _tasksLiveData = MutableLiveData<TasksRequestState>()
    val tasksLiveData: LiveData<TasksRequestState> by this::_tasksLiveData

    fun getTasksList() {

    }
}