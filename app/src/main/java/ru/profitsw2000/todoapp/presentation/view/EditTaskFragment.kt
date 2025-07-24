package ru.profitsw2000.todoapp.presentation.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.profitsw2000.todoapp.R
import ru.profitsw2000.todoapp.data.room.model.TaskModel
import ru.profitsw2000.todoapp.data.state.TaskEditState
import ru.profitsw2000.todoapp.databinding.FragmentEditTaskBinding
import ru.profitsw2000.todoapp.presentation.MainActivity
import ru.profitsw2000.todoapp.presentation.viewmodel.EditTaskViewModel
import ru.profitsw2000.todoapp.utility.Controller
import ru.profitsw2000.todoapp.utility.TAG

private const val ARG_POSITION = "position"

class EditTaskFragment : Fragment() {

    private var taskPosition: Int? = null
    private var _binding: FragmentEditTaskBinding? = null
    private val binding get() = _binding!!
    private val editTaskViewModel: EditTaskViewModel by viewModel()
    private val controller by lazy { activity as Controller }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity !is Controller) {
            throw IllegalStateException(getString(R.string.not_controller_activity_exception_message))
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleQuitButtonPress()
        arguments?.let {
            taskPosition = it.getInt(ARG_POSITION)
        }
        (activity as MainActivity).showUpButton()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditTaskBinding.bind(inflater.inflate(R.layout.fragment_edit_task, container, false))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller.setAppBarText(getString(R.string.edit_task_fragment_app_bar_title))
        observeData()
        initViews()
        getTaskFromDB(taskPosition)
    }

    private fun getTaskFromDB(taskPosition: Int?) {
        if (taskPosition != null) editTaskViewModel.getTask(taskPosition)
        else showMessage(getString(R.string.error_dialog_title_text),
            getString(
                R.string.unknown_error_dialog_message_text
            ))
    }

    private fun observeData() {
        val observer = Observer<TaskEditState> { renderData(it) }
        editTaskViewModel.tasksLiveData.observe(viewLifecycleOwner, observer)
    }

    private fun initViews() = with(binding) {
        createTaskButton.setOnClickListener {
            editTaskViewModel.editTask(getTaskModel())
        }
    }

    private fun renderData(taskEditState: TaskEditState) {
        when(taskEditState) {
            TaskEditState.EditSuccess -> showMessage(getString(R.string.success_dialog_title_text), getString(R.string.success_edit_dialog_message_text))
            is TaskEditState.Error -> showMessage(getString(R.string.error_dialog_title_text),
                getString(
                    R.string.error_dialog_message_text
                ))
            is TaskEditState.LoadSuccess -> showForms(taskEditState.taskModel, taskEditState.tasksListSize)
            TaskEditState.Loading -> setProgressBarVisible(true)
        }
    }

    private fun showForms(taskModel: TaskModel, taskListSize: Int) = with(binding) {
        setProgressBarVisible(false)
        toDoTextInputLayout.editText?.setText(taskModel.taskText)
        priorityNumberPicker.minValue = 1
        priorityNumberPicker.maxValue = taskListSize
        priorityNumberPicker.value = taskModel.priority
    }

    private fun setProgressBarVisible(isVisible: Boolean) = with(binding) {
        if (isVisible) progressBar.visibility = View.VISIBLE
        else progressBar.visibility = View.GONE

        toDoTextInputLayout.isEnabled = !isVisible
        priorityNumberPicker.isEnabled = !isVisible
        createTaskButton.isEnabled = !isVisible
    }

    private fun showMessage(title: String, message: String) {
        setProgressBarVisible(false)
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok_button_text)) { dialog, _ ->
                dialog.dismiss()
                navigateBack()
            }
            .create()
            .show()
    }

    private fun showWarningMessage(title: String, message: String) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.yes_button_text)) { dialog, _ ->
                dialog.dismiss()
                navigateBack()
            }
            .setNegativeButton(getString(R.string.no_button_text)) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun handleQuitButtonPress() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showWarningMessage(getString(R.string.confirm_exit_dialog_title),
                    getString(R.string.confirm_create_task_dialog_message))
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
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

    private fun navigateBack() {
        (activity as MainActivity).supportFragmentManager.popBackStack()
    }

    companion object {
        @JvmStatic
        fun newInstance(position: Int) =
                EditTaskFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_POSITION, position)
                    }
                }
    }
}