package ru.practicum.android.diploma.filter.data.dto.models

data class FilterDto(
    val idCountry: String?,
    val idArea: String?,
    val idIndustry: String?,
    val nameCountry: String?,
    val nameArea: String?,
    val nameIndustry: String?,
    val currency: String?,
    val salary: Int?,
    val onlyWithSalary: Boolean,
)
