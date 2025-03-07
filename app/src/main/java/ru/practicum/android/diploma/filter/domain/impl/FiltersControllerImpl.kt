package ru.practicum.android.diploma.filter.domain.impl

import ru.practicum.android.diploma.filter.domain.interfaces.FiltersController
import ru.practicum.android.diploma.filter.domain.interfaces.FiltersRepository
import ru.practicum.android.diploma.filter.domain.models.FilterData

class FiltersControllerImpl(private val filtersRepository: FiltersRepository) : FiltersController {
    override fun getFilterSettings(): FilterData = filtersRepository.getFiltersSet()
    override fun getDefaultSettings(): FilterData = filtersRepository.getDefaultSettings()
    override fun saveFilterSettings(filterToSave: FilterData) {
        filtersRepository.saveFiltersSet(settingsToSave = filterToSave)
    }
}