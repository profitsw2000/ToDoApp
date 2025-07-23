package ru.profitsw2000.todoapp.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.replace
import ru.profitsw2000.todoapp.R
import ru.profitsw2000.todoapp.presentation.view.CreateTaskFragment
import ru.profitsw2000.todoapp.presentation.view.EditTaskFragment
import ru.profitsw2000.todoapp.presentation.view.MainFragment
import ru.profitsw2000.todoapp.utility.Controller
import ru.profitsw2000.todoapp.utility.TAG

class MainActivity : AppCompatActivity(), Controller {

    private val fragmentManager by lazy { supportFragmentManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        openMainFragment()
    }

    override fun openMainFragment() {
        fragmentManager.apply {
            beginTransaction()
                .replace(R.id.fragment_container, MainFragment.newInstance())
                .commit()
        }
    }

    override fun openCreateTaskFragment() {
        fragmentManager.apply {
            beginTransaction()
                .replace(R.id.fragment_container, CreateTaskFragment.newInstance())
                .addToBackStack("")
                .commit()
        }
    }

    override fun openEditTaskFragment(position: Int) {
        fragmentManager.apply {
            beginTransaction()
                .replace(R.id.fragment_container, EditTaskFragment.newInstance(position))
                .addToBackStack("")
                .commit()
        }
    }
}