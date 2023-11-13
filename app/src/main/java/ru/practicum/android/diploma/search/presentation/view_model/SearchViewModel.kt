package ru.practicum.android.diploma.search.presentation.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.search.domain.SearchInteractor
import ru.practicum.android.diploma.search.domain.models.AnswerVacancyList
import ru.practicum.android.diploma.search.domain.models.QuerySearchMdl
import ru.practicum.android.diploma.search.presentation.states.StateFilters
import ru.practicum.android.diploma.search.presentation.states.ToastState
import ru.practicum.android.diploma.util.DataStatus

class SearchViewModel(private val searchInteractor: SearchInteractor) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_ML = 2000L
        private const val TOAST_DEBOUNCE_DELAY_ML = 10000L
    }


    private val _stateFilters = MutableStateFlow<StateFilters>(StateFilters.NoUseFilters)
    val stateFilters = _stateFilters as StateFlow<StateFilters>

    private val _stateSearch = MutableStateFlow<DataStatus<AnswerVacancyList>>(DataStatus.Default())
    val stateSearch = _stateSearch as StateFlow<DataStatus<AnswerVacancyList>>

    private val _stateToast = MutableStateFlow<ToastState>(ToastState.NoneMessage)
    val stateToast = _stateToast as StateFlow<ToastState>

    private var searchJob: Job? = null
    private var isShowToast: Boolean = true
    fun doRequestSearch(modelForQuery: QuerySearchMdl) {

        if (modelForQuery.text.length != 1) {
            if (modelForQuery.text != "") {
                viewModelScope.launch {
                    searchInteractor.doRequestSearch(modelForQuery).collect {
                        when (it) {
                            is DataStatus.Content -> {
                                _stateSearch.value = DataStatus.Content(it.data!!)
                            }

                            is DataStatus.Loading -> {
                                _stateSearch.value = DataStatus.Loading()
                            }

                            is DataStatus.Error -> {
                                _stateSearch.value = DataStatus.Error()
                            }

                            is DataStatus.EmptyContent -> {
                                _stateSearch.value = DataStatus.EmptyContent()
                            }

                            is DataStatus.NoConnecting -> {
                                _stateSearch.value = DataStatus.NoConnecting()
                            }

                            is DataStatus.Default -> {
                                _stateSearch.value = DataStatus.Default()
                            }

                            else -> {
                                _stateSearch.value = DataStatus.Default()
                            }

                        }
                    }
                }
            }
        }

    }

    fun getParamsFilters() {
        val params = searchInteractor.getParamsFilters()

        if (params == null) {
            _stateFilters.value = StateFilters.NoUseFilters
        } else {
            _stateFilters.value = StateFilters.UseFilters(params)
        }
    }

    fun searchDebounce(modelForQuery: QuerySearchMdl) {

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            if (modelForQuery.text.length != 1) {
                if (modelForQuery.text != "") {
                    delay(SEARCH_DEBOUNCE_DELAY_ML)
                    doRequestSearch(modelForQuery)
                }
            }
        }
    }

    fun setDefaultState() {
        _stateSearch.value = DataStatus.Default()
    }

    fun setToastNoMessage() {
        _stateToast.value = ToastState.NoneMessage
    }

    fun showToast(message: String) {
        _stateToast.value = ToastState.ShowMessage(message)
    }

    fun showToastDebounce(message: String) {
        if (isShowToast) {
            isShowToast = false
            showToast(message)
            viewModelScope.launch {
                delay(TOAST_DEBOUNCE_DELAY_ML)
                isShowToast = true
            }
        }
    }

}