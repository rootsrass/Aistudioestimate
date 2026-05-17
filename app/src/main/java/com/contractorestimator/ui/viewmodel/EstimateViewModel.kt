package com.contractorestimator.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contractorestimator.data.repository.EstimateRepository
import com.contractorestimator.domain.registry.ServiceRegistry
import com.contractorestimator.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EstimateViewModel @Inject constructor(
    private val serviceRegistry: ServiceRegistry,
    private val repository: EstimateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EstimateUiState())
    val uiState: StateFlow<EstimateUiState> = _uiState.asStateFlow()

    private val calculator = serviceRegistry.getCalculator(ServiceType.SOD_INSTALLATION)

    val savedEstimates: StateFlow<List<Estimate>> = repository.getAllEstimates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSqFtChange(valStr: String) {
        val value = valStr.toDoubleOrNull() ?: 0.0
        _uiState.value = _uiState.value.copy(squareFootage = value)
    }

    fun onCustomerNameChange(name: String) {
        _uiState.value = _uiState.value.copy(customerName = name)
    }

    fun onSodTypeChange(type: String, price: Double) {
        _uiState.value = _uiState.value.copy(
            sodType = type,
            costPerSqFt = price
        )
    }

    fun onDifficultyChange(difficulty: Difficulty) {
        _uiState.value = _uiState.value.copy(difficulty = difficulty)
    }

    fun onLaborUpdate(workers: Int, rate: Double, hours: Double) {
        _uiState.value = _uiState.value.copy(
            workers = workers,
            hourlyRate = rate,
            hours = hours
        )
    }

    fun calculateEstimate() {
        val state = _uiState.value
        val input = EstimateInput(
            squareFootage = state.squareFootage,
            sodType = state.sodType,
            difficulty = state.difficulty,
            laborConfig = LaborConfig(state.workers, state.hourlyRate, state.hours),
            pricingConfig = PricingConfig(costPerSqFt = state.costPerSqFt)
        )
        
        val result = calculator.calculate(input)
        _uiState.value = _uiState.value.copy(lastResult = result)
    }

    fun saveCurrentEstimate() {
        viewModelScope.launch {
            val state = _uiState.value
            val result = state.lastResult ?: return@launch
            
            val estimate = Estimate(
                customerName = state.customerName.ifBlank { "Unsaved Customer" },
                serviceType = ServiceType.SOD_INSTALLATION,
                input = EstimateInput(
                    squareFootage = state.squareFootage,
                    sodType = state.sodType,
                    difficulty = state.difficulty,
                    laborConfig = LaborConfig(state.workers, state.hourlyRate, state.hours),
                    pricingConfig = PricingConfig(costPerSqFt = state.costPerSqFt)
                ),
                result = result
            )
            repository.saveEstimate(estimate)
        }
    }

    fun deleteEstimate(estimate: Estimate) {
        viewModelScope.launch {
            repository.deleteEstimate(estimate)
        }
    }
}

data class EstimateUiState(
    val customerName: String = "",
    val squareFootage: Double = 0.0,
    val sodType: String = "Bahia",
    val costPerSqFt: Double = 0.35,
    val difficulty: Difficulty = Difficulty.MODERATE,
    val workers: Int = 2,
    val hourlyRate: Double = 25.0,
    val hours: Double = 8.0,
    val lastResult: EstimateResult? = null
)
