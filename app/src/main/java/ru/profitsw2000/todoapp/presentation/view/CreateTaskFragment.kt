package ru.profitsw2000.todoapp.presentation.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.profitsw2000.todoapp.R
import ru.profitsw2000.todoapp.data.room.model.TaskModel
import ru.profitsw2000.todoapp.data.state.TaskCreateState
import ru.profitsw2000.todoapp.databinding.FragmentCreateTaskBinding
import ru.profitsw2000.todoapp.presentation.viewmodel.CreateTaskViewModel
import ru.profitsw2000.todoapp.utility.TAG

class CreateTaskFragment : Fragment() {

    private var _binding: FragmentCreateTaskBinding? = null
    private val binding get() = _binding!!
    private val createTaskViewModel: CreateTaskViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCreateTaskBinding.bind(inflater.inflate(R.layout.fragment_create_task, container, false))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        initViews()
        createTaskViewModel.getTasksList()
    }

    private fun initViews() = with(binding) {
        createTaskButton.setOnClickListener {
            if (!taskTextIsEmpty()) createTaskViewModel.createTask(getTaskModel())
            else toDoTextInputLayout.error = getString(R.string.enter_text_error_hint)
        }
        priorityTitleTextView.setOnClickListener {
            Log.d(TAG, "initViews: ${priorityNumberPicker.value}")
        }
    }

    private fun observeData() {
        val observer = Observer<TaskCreateState> { renderData(it) }
        createTaskViewModel.tasksLiveData.observe(viewLifecycleOwner, observer)
    }

    private fun renderData(taskCreateState: TaskCreateState) {
        when(taskCreateState) {
            TaskCreateState.CreateSuccess -> showMessage(getString(R.string.success_dialog_title_text), getString(R.string.success_dialog_message_text))
            is TaskCreateState.Error -> showMessage(getString(R.string.error_dialog_title_text),
                getString(
                    R.string.error_dialog_message_text
                ))
            is TaskCreateState.LoadSuccess -> showForm(taskCreateState.tasksListSize)
            TaskCreateState.Loading -> setProgressBarVisibility(true)
        }
    }

    private fun showMessage(title: String, message: String) {
        setProgressBarVisibility(false)
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok_button_text)) { dialog, _ ->
                requireActivity().onBackPressed()
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun setProgressBarVisibility(isVisible: Boolean) = with(binding) {
        if (isVisible) progressBar.visibility = View.VISIBLE
        else progressBar.visibility = View.GONE

        toDoTextInputLayout.isEnabled = !isVisible
        createTaskButton.isEnabled = !isVisible
        priorityNumberPicker.isEnabled = !isVisible
    }

    private fun showProgressBar() = with(binding) {
        Log.d(TAG, "showProgressBar: ")
        progressBar.visibility = View.VISIBLE
        toDoTextInputLayout.isEnabled = false
        createTaskButton.isEnabled = false
        priorityNumberPicker.isEnabled = false
    }

    private fun showForm(tasksListSize: Int) = with(binding) {
        Log.d(TAG, "showForm: ")
        setProgressBarVisibility(false)
        priorityNumberPicker.minValue = 1
        priorityNumberPicker.maxValue = tasksListSize + 1
    }

    private fun taskTextIsEmpty(): Boolean {
        return binding.toDoTextInputLayout.editText?.text.toString().isEmpty()
    }

    private fun getTaskModel(): TaskModel {
        return with(binding) {
            TaskModel(
                id = 0,
                priority = priorityNumberPicker.value,
                taskText = toDoTextInputLayout.editText?.text.toString()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = CreateTaskFragment()
    }
}