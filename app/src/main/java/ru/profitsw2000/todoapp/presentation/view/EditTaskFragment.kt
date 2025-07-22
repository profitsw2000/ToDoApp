package ru.profitsw2000.todoapp.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.profitsw2000.todoapp.R
import ru.profitsw2000.todoapp.databinding.FragmentEditTaskBinding

private const val ARG_POSITION = "position"

class EditTaskFragment : Fragment() {

    private var taskPosition: Int? = null
    private var _binding: FragmentEditTaskBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            taskPosition = it.getInt(ARG_POSITION)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditTaskBinding.bind(inflater.inflate(R.layout.fragment_edit_task, container, false))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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