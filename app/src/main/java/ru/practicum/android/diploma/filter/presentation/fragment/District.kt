package ru.practicum.android.diploma.filter.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentDistrictBinding
import ru.practicum.android.diploma.filter.presentation.view_model.DistrictVm
import ru.practicum.android.diploma.filter.recycler.AreaAdapter
import ru.practicum.android.diploma.util.DefaultFragment

const val ARG_COUNTRY_ID = "country_id_pram"
const val ARG_FRAGMENT_TYPE = "fragment_to_open"


const val KEY_DISTRICT_RESULT = "district_result"
const val KEY_COUNTRY_RESULT = "area_result"

const val AREA_ID = "area_id_param"
const val AREA_NAME = "area_name_param"

/**
 * A simple [Fragment] subclass.
 * Use the [District.newInstance] factory method to
 * create an instance of this fragment and set required param
 * get fragmentType to access right child Fragment
 * fragmentType==0 -> District Fragment (need to get countryId param)
 * fragmentType==1 -> Country Fragment
 * fragmentType==2 -> Industry Fragment
 */
open class District : DefaultFragment<FragmentDistrictBinding>() {
    private var countryId: Int? = null // Считывается из аргументов в onCreate
    private var fragmentType: Int? = null // Считывается из аргументов в onCreate

    val vm: DistrictVm by viewModel()

    private val adapter = AreaAdapter(mutableListOf()) {
        vm.areaToSendBack = it
        exitExtraWhenSystemBackPushed() // Exit after choosing required area
    }

    override fun bindingInflater(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDistrictBinding {
        return FragmentDistrictBinding.inflate(inflater, container, false)
    }

    override fun setUiListeners() {
        with(binding) {
            navigationBar.setNavigationOnClickListener {
                exitExtraWhenSystemBackPushed()
            }
        }
    }

    override fun exitExtraWhenSystemBackPushed() {
        // Для поиска вакансии по региону необходимо передать в поисковый запрос id региона
        vm.getAreaBundle()?.let { setFragmentResult(KEY_DISTRICT_RESULT, it) }
        findNavController().popBackStack()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            countryId = it.getInt(ARG_COUNTRY_ID)
            fragmentType = it.getInt(ARG_FRAGMENT_TYPE)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers() // Обработчики lifeData
        setUpAdapter() // Настройка адаптера для RecyclerView

        // При загрузке фрагмента анализируем его тип
        fragmentType?.let {
            when (it) {
                FragmentType.DISTRICT.id -> {
                    // Загрузка списка регионов производится только при наличии ненулевого id страны
                    countryId?.let { id -> vm.loadDistrictList(id) }
                }
                FragmentType.COUNTRY.id->{
                    binding.navigationBar.title = resources.getString(R.string.country_fragment_title)
                    vm.loadCountryList()
                }
                FragmentType.INDUSTRY.id->{
                    binding.navigationBar.title = "Industry"
                }
                else -> {}
            }
        }
    }

    private fun setUpAdapter() {
        binding.areaRecycler.adapter = adapter
        binding.areaRecycler.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setObservers() {
        vm.errorMsg.observe(viewLifecycleOwner) { showMsgDialog(it) }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.screenState.collect { setFragmentScreenState(it) }
            }
        }
    }

    private fun setFragmentScreenState(newScreenState: DistrictScreenState) {
        when (newScreenState) {
            is DistrictScreenState.Loading -> binding.txtContent.text = "Initial "
            is DistrictScreenState.Content -> adapter.changeData(newScreenState.data)
            else -> {}
        }
    }

    companion object {
        fun newInstance(fragmentType: Int, countryId: Int): District {
            return District().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COUNTRY_ID, countryId)
                    putInt(ARG_FRAGMENT_TYPE, fragmentType)
                }
            }
        }
    }
}