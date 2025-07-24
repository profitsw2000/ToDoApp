package ru.profitsw2000.todoapp.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import ru.profitsw2000.todoapp.R
import ru.profitsw2000.todoapp.presentation.view.CreateTaskFragment
import ru.profitsw2000.todoapp.presentation.view.EditTaskFragment
import ru.profitsw2000.todoapp.presentation.view.MainFragment
import ru.profitsw2000.todoapp.utility.Controller

class MainActivity : AppCompatActivity(), Controller {

    private val fragmentManager by lazy { supportFragmentManager }
    private val actionBar: ActionBar? by lazy {
        supportActionBar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openMainFragment()
    }

    fun showUpButton() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    fun hideUpButton() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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

    override fun setAppBarText(appBarText: String) {
        actionBar?.let {
            title = appBarText
        }
    }
}