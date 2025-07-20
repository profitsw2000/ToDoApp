package ru.profitsw2000.todoapp.presentation.view.adapter

interface OnTaskControlButtonClickListener {

    fun onIncreasePriorityClick(position: Int)

    fun onDecreasePriorityClick(position: Int)

    fun onDeleteTaskClick(position: Int)

    fun onEditTaskClick(position: Int)

}