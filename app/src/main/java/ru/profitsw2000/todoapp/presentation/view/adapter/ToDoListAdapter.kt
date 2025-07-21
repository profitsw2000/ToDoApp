package ru.profitsw2000.todoapp.presentation.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.profitsw2000.todoapp.data.room.model.TaskModel
import ru.profitsw2000.todoapp.databinding.ToDoListItemViewBinding

class ToDoListAdapter(
    val onTaskControlButtonClickListener: OnTaskControlButtonClickListener
) : RecyclerView.Adapter<ToDoListAdapter.ViewHolder>() {

    private var data: List<TaskModel> = arrayListOf()

    fun setData(data: List<TaskModel>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ToDoListItemViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        val taskViewHolder = ViewHolder(binding)

        setClickListeners(taskViewHolder)

        return taskViewHolder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.taskText.text = data[position].taskText
    }

    private fun setClickListeners(taskViewHolder: ViewHolder) {
        with(taskViewHolder) {
            increasePriorityButton.setOnClickListener {
                onTaskControlButtonClickListener.onIncreasePriorityClick(position = taskViewHolder.adapterPosition)
            }

            decreasePriorityButton.setOnClickListener {
                onTaskControlButtonClickListener.onDecreasePriorityClick(position = taskViewHolder.adapterPosition)
            }

            deleteTaskButton.setOnClickListener {
                onTaskControlButtonClickListener.onDeleteTaskClick(position = taskViewHolder.adapterPosition)
            }

            editTaskButton.setOnClickListener {
                onTaskControlButtonClickListener.onEditTaskClick(position = taskViewHolder.adapterPosition)
            }
        }
    }

    inner class ViewHolder(binding: ToDoListItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val taskText = binding.taskContentTextView
        val increasePriorityButton = binding.arrowUpImageView
        val decreasePriorityButton = binding.arrowDownImageView
        val deleteTaskButton = binding.deleteTaskImageView
        val editTaskButton = binding.editTaskImageView
    }
}