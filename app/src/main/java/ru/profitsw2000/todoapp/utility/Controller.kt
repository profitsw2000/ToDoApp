package ru.profitsw2000.todoapp.utility

interface Controller {

    fun openMainFragment()

    fun openCreateTaskFragment()

    fun openEditTaskFragment(position: Int)

    fun setAppBarText(appBarText: String)

}