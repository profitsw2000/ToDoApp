package ru.profitsw2000.todoapp.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.profitsw2000.todoapp.data.data.TasksRepositoryImpl
import ru.profitsw2000.todoapp.data.domain.TasksRepository
import ru.profitsw2000.todoapp.data.room.database.AppDatabase
import ru.profitsw2000.todoapp.presentation.viewmodel.CreateTaskViewModel

val mainModule = module {
    single {
        AppDatabase.create(androidContext())
        AppDatabase.getInstance()
    }
    single<TasksRepository> { TasksRepositoryImpl(get()) }
    single { CreateTaskViewModel(get()) }
}