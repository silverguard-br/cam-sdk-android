package com.silverguard.cam.di

import com.silverguard.cam.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    viewModel { HomeViewModel() }
}

val modules = listOf(
    homeModule
)