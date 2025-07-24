package ru.profitsw2000.todoapp.presentation.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.profitsw2000.todoapp.R
import ru.profitsw2000.todoapp.data.room.model.TaskModel
import ru.profitsw2000.todoapp.data.state.TasksRequestState
import ru.profitsw2000.todoapp.databinding.FragmentMainBinding
import ru.profitsw2000.todoapp.presentation.MainActivity
import ru.profitsw2000.todoapp.presentation.view.adapter.OnTaskControlButtonClickListener
import ru.profitsw2000.todoapp.presentation.view.adapter.ToDoListAdapter
import ru.profitsw2000.todoapp.presentation.viewmodel.MainViewModel
import ru.profitsw2000.todoapp.utility.Controller


class MainFragment : Fragment(), FragmentManager.OnBackStackChangedListener {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val controller by lazy { activity as Controller }
    private val mainViewModel: MainViewModel by viewModel()
    private val adapter = ToDoListAdapter(object : OnTaskControlButtonClickListener{
        override fun onIncreasePriorityClick(position: Int) {
            mainViewModel.changeTask(position, isIncreasePriority = true)
        }

        override fun onDecreasePriorityClick(position: Int) {
            mainViewModel.changeTask(position, isIncreasePriority = false)
        }

        override fun onDeleteTaskClick(position: Int) {
            showDeleteTaskWarningMessage(position)
        }

        override fun onEditTaskClick(position: Int) {
            controller.openEditTaskFragment(position)
        }

    })

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity !is Controller) {
            throw IllegalStateException(getString(R.string.not_controller_activity_exception_message))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).supportFragmentManager.addOnBackStackChangedListener(this)
    }

    override fun onBackStackChanged() {
        if (activity != null) {
            if (requireActivity().supportFragmentManager.backStackEntryCount < 1) {
                (activity as MainActivity?)?.hideUpButton()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.bind(inflater.inflate(R.layout.fragment_main, container, false))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller.setAppBarText(getString(R.string.main_fragment_app_bar_title))
        initViews()
        observeData()
        mainViewModel.getTasksList()
    }

    private fun initViews() = with(binding) {
        tasksListRecyclerView.adapter = adapter
        addTaskFab.setOnClickListener {
            controller.openCreateTaskFragment()
        }
    }

    private fun observeData() {
        val observer = Observer<TasksRequestState> { renderData(it) }
        mainViewModel.tasksLiveData.observe(viewLifecycleOwner, observer)
    }

    private fun renderData(tasksRequestState: TasksRequestState) {
        when(tasksRequestState) {
            is TasksRequestState.Error -> showMessage(getString(R.string.error_dialog_title_text), tasksRequestState.errorMessage)
            TasksRequestState.Loading -> setProgressBarVisible(true)
            is TasksRequestState.Success -> populateRecyclerView(tasksRequestState.taskModelList)
        }
    }

    private fun showMessage(title: String, message: String) {
        setProgressBarVisible(false)
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok_button_text)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showDeleteTaskWarningMessage(position: Int) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.confirm_delete_task_dialog_title))
            .setMessage(getString(R.string.confirm_delete_task_dialog_message))
            .setPositiveButton(getString(R.string.yes_button_text)) { dialog, _ ->
                mainViewModel.deleteTask(position)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.no_button_text)) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun setProgressBarVisible(visible: Boolean) = with(binding) {
        if (visible) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    private fun populateRecyclerView(taskModelList: List<TaskModel>) {
        setProgressBarVisible(false)
        adapter.setData(taskModelList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}