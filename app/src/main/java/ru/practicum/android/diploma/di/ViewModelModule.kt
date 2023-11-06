package ru.practicum.android.diploma.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.favorite.presentation.view_model.FavoriteViewModel
import ru.practicum.android.diploma.filter.presentation.view_model.DistrictVm
import ru.practicum.android.diploma.filter.presentation.view_model.IndustryVm

class ViewModelModule {

    val viewModelModule = module {
        viewModel { FavoriteViewModel(favoriteInteractor = get()) }

        viewModel {DistrictVm(useCaseAreaController = get())}

        viewModel{IndustryVm(industriesController = get())}
    }

}