package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.favorite.domain.FavoriteInteractor
import ru.practicum.android.diploma.favorite.domain.impl.FavoriteInteractorImpl
import ru.practicum.android.diploma.favorite.domain.models.VacancyConvertor

class InteractorModule {

    val interactorModule = module {

        single<FavoriteInteractor> {
            FavoriteInteractorImpl(get())
        }


    }

}