package com.fakhrinurrohman.myunjapp.di

import com.fakhrinurrohman.myunjapp.data.*
import com.fakhrinurrohman.myunjapp.viewmodels.*
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    // Provide the Database
    single { getDatabaseBuilder().build() }
    
    // Provide DAOs
    single { get<AppDatabase>().semesterDao() }
    single { get<AppDatabase>().courseDao() }
    single { get<AppDatabase>().userEventDao() }
    single { get<AppDatabase>().universityDao() }
    
    // Provide Services & Repositories
    singleOf(::SiakadApiService)
    singleOf(::AuthRepository)
    single { ScheduleRepository(get(), get(), get()) }
    singleOf(::UniversityRepository)
    
    // Provide ViewModels
    singleOf(::SessionViewModel)
    viewModelOf(::ScheduleViewModel)
    viewModelOf(::SemesterViewModel)
    viewModelOf(::CourseViewModel)
    viewModelOf(::UserEventViewModel)
    viewModelOf(::InformationViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::AuthViewModel)
}
