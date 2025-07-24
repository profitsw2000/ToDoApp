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
import ru.profitsw2000.todoapp.data.state.TaskCreateState
import ru.profitsw2000.todoapp.databinding.FragmentCreateTaskBinding
import ru.profitsw2000.todoapp.presentation.MainActivity
import ru.profitsw2000.todoapp.presentation.viewmodel.CreateTaskViewModel
import ru.profitsw2000.todoapp.utility.Controller
import ru.profitsw2000.todoapp.utility.TAG

class CreateTaskFragment : Fragment() {

    private var _binding: FragmentCreateTaskBinding? = null
    private val binding get() = _binding!!
    private val createTaskViewModel: CreateTaskViewModel by viewModel()
    private val controller by lazy { activity as Controller }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity !is Controller) {
            throw IllegalStateException(getString(R.string.not_controller_activity_exception_message))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleQuitButtonPress()
        (activity as MainActivity).showUpButton()
    }

    @SuppressLint("RestrictedApi")
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
        controller.setAppBarText(getString(R.string.create_task_fragment_app_bar_title))
        observeData()
        initViews()
        createTaskViewModel.getTasksList()
    }

    private fun initViews() = with(binding) {
        createTaskButton.setOnClickListener {
            if (!taskTextIsEmpty()) createTaskViewModel.createTask(getTaskModel())
            else toDoTextInputLayout.error = getString(R.string.enter_text_error_hint)
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
                    getString(R.string.confirm_edit_task_dialog_message))
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun navigateBack() {
        (activity as MainActivity).supportFragmentManager.popBackStack()
    }

    private fun setProgressBarVisibility(isVisible: Boolean) = with(binding) {
        if (isVisible) progressBar.visibility = View.VISIBLE
        else progressBar.visibility = View.GONE

        toDoTextInputLayout.isEnabled = !isVisible
        createTaskButton.isEnabled = !isVisible
        priorityNumberPicker.isEnabled = !isVisible
    }

    private fun showForm(tasksListSize: Int) = with(binding) {
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