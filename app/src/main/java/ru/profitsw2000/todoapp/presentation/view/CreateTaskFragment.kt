package ru.profitsw2000.todoapp.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.profitsw2000.todoapp.R
import ru.profitsw2000.todoapp.data.state.TaskCreateState
import ru.profitsw2000.todoapp.databinding.FragmentCreateTaskBinding

class CreateTaskFragment : Fragment() {

    private var _binding: FragmentCreateTaskBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCreateTaskBinding.bind(inflater.inflate(R.layout.fragment_create_task, container, false))
        return binding.root
    }

    private fun observeData(taskCreateState: TaskCreateState) {

    }

    companion object {
        @JvmStatic
        fun newInstance() = CreateTaskFragment()
    }
}