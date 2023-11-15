package ru.practicum.android.diploma.filter.presentation.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFiltersBinding
import ru.practicum.android.diploma.filter.domain.models.FilterData
import ru.practicum.android.diploma.filter.presentation.sharedviewmodel.FilterSharedVm
import ru.practicum.android.diploma.filter.presentation.util.KEY_FILTERS_RESULT
import ru.practicum.android.diploma.filter.presentation.view_model.FiltersVm
import ru.practicum.android.diploma.util.DefaultFragment

class Filters : DefaultFragment<FragmentFiltersBinding>() {
    private val vm: FiltersVm by viewModel()
    private val sharedViewModel: FilterSharedVm by activityViewModels()
    override fun bindingInflater(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFiltersBinding {
        return FragmentFiltersBinding.inflate(inflater, container, false)
    }


    override fun setUiListeners() {
        with(binding) {
            navigationBar.setNavigationOnClickListener {
                exitExtraWhenSystemBackPushed()
            }

            layoutWorkPlace.setOnClickListener {
                findNavController().navigate(R.id.action_to_workPlace)
            }

            layoutIndustry.setOnClickListener {
                findNavController().navigate(R.id.action_filters_to_industry,
                    Bundle().apply {

                    })
            }

            checkboxWithSalary.setOnCheckedChangeListener { buttonView, isChecked ->
                if (vm.userInput) vm.setWithSalaryParam(isChecked)
            }

            txtSalaryInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (vm.userInput) {
                        vm.userInput = false // Все изменения далее вызваны программно

                        // Передаем введенные данные на проверку viewModel
                        // Функция вернет значение предыдущего уровня зп, если был введен неверный символ
                        val pStr = vm.setNewSalaryToFilter(s)

                        // Ноль отображаем как пустое поле для ввода зп
                        if (pStr == null) binding.txtSalaryInput.setText("")
                        else {
                            binding.txtSalaryInput.setText(pStr)
                            binding.txtSalaryInput.setSelection(pStr.length)
                        }

                        vm.userInput = true
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            btnAcceptFilterSet.setOnClickListener {
                vm.saveNewFilterSet()
                sharedViewModel.deleteFilters()
                setFragmentResult(KEY_FILTERS_RESULT, bundleOf())
                exitExtraWhenSystemBackPushed()
            }

            btnDeclineFilterSet.setOnClickListener {
                vm.abortFilters()

                // Загружаем прежние данные в sharedViewModel
                sharedViewModel.setFilter(vm.getFilters())
            }

            btnClrWorkPlace.setOnClickListener {
                vm.clearWorkPlace()
            }

            btnClrIndustry.setOnClickListener {
                vm.clearIndustry()
            }
        }
    }

    override fun exitExtraWhenSystemBackPushed() {
        findNavController().popBackStack()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Загружаем набор настроек фильтрации в объединенную модель при первой загрузке фрагмента
        // Внутри объединенной модели так же есть проверка, что не было многократной перезаписи
        sharedViewModel.setFilter(vm.getFilters())
        Log.e("LOG", "On create")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.screenState.collect {
                    if (it is ScreenFiltersState.Content) {
                        renderFilterSettings(it.filterSet)
                        renderAcceptChangeBtn(it.btnAcceptVisibility)
                        renderDeclineChangeBtn(it.btnDeclineVisibility)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // При возвращении на фрагмент собираем потенциально полученную информацию
        // от фрагментов выбора страны, региона, профессии
        sharedViewModel.getFilters()?.let {
            vm.updateFiltersWithRemote(it)
            Log.e("LOG", "OnResume $it")
        }
    }

    private fun renderFilterSettings(filterSet: FilterData) {
        vm.userInput = false

        filterSet.nameCountry?.let {
            binding.lblChooseWorkPlace.isVisible = true
            binding.txtChooseWorkPlace.isVisible = true
            binding.txtChooseWorkPlace.text = it
        }

        filterSet.nameArea?.let {
            binding.txtChooseWorkPlace.text =
                with(StringBuilder()) {
                    append(filterSet.nameCountry.toString())
                    append(",")
                    append(it)
                }.toString()
        }

        filterSet.nameIndustry?.let {
            binding.lblChooseIndustry.isVisible = true
            binding.txtChooseIndustry.isVisible = true
            binding.txtChooseIndustry.text = it
        }

        filterSet.salary?.let {
            if (it > 0) {
                binding.txtSalaryInput.setText(it.toString())
            } else {
                binding.txtSalaryInput.setText("")
            }
        }

        filterSet.onlyWithSalary.let {
            binding.checkboxWithSalary.isChecked = filterSet.onlyWithSalary
        }


        if (filterSet.idCountry == null) {
            binding.lblChooseWorkPlace.isVisible = false
            binding.txtChooseWorkPlace.text = resources.getString(R.string.filters_work_place)
        }

        if (filterSet.idIndustry == null) {
            binding.lblChooseIndustry.isVisible = false
            binding.txtChooseIndustry.text = resources.getString(R.string.filters_industry)
        }

        if (filterSet.salary == null) {
            binding.txtSalaryInput.text = null
        }

        vm.userInput = true
    }

    private fun renderAcceptChangeBtn(visibility: Boolean) {
        binding.btnAcceptFilterSet.isVisible = visibility
    }

    private fun renderDeclineChangeBtn(visibility: Boolean) {
        binding.btnDeclineFilterSet.isVisible = visibility
    }
}