package ru.profitsw2000.todoapp.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.profitsw2000.todoapp.R
import ru.profitsw2000.todoapp.data.room.model.TaskModel
import ru.profitsw2000.todoapp.data.state.TasksRequestState
import ru.profitsw2000.todoapp.databinding.FragmentMainBinding
import ru.profitsw2000.todoapp.presentation.viewmodel.MainViewModel

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by viewModel()

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
        initViews()
        observeData()
        mainViewModel.getTasksList()
    }

    private fun initViews() = with(binding) {

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
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok_button_text)) { dialog, _ ->
                dialog.dismiss()
            }
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

    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}