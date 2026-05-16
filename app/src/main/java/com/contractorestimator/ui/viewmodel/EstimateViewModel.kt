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
        _uiState.value = _uiState.value.copy(squareFootage = valStr)
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

    fun onLaborUpdate(workers: String, rate: String, hours: String) {
        _uiState.value = _uiState.value.copy(
            workers = workers,
            hourlyRate = rate,
            hours = hours
        )
    }

    fun loadEstimate(id: String) {
        viewModelScope.launch {
            val estimate = repository.getEstimateById(id)
            if (estimate != null) {
                _uiState.value = _uiState.value.copy(
                    customerName = estimate.customerName,
                    squareFootage = estimate.input.squareFootage.toString(),
                    sodType = estimate.input.sodType,
                    difficulty = estimate.input.difficulty,
                    workers = estimate.input.laborConfig.numberOfWorkers.toString(),
                    hourlyRate = estimate.input.laborConfig.hourlyRate.toString(),
                    hours = estimate.input.laborConfig.estimatedHours.toString(),
                    lastResult = estimate.result
                )
            }
        }
    }

    fun clearEstimate() {
        _uiState.value = EstimateUiState()
    }

    fun calculateEstimate() {
        val state = _uiState.value
        val input = EstimateInput(
            squareFootage = state.squareFootage.toDoubleOrNull() ?: 0.0,
            sodType = state.sodType,
            difficulty = state.difficulty,
            laborConfig = LaborConfig(state.workers.toIntOrNull() ?: 0, state.hourlyRate.toDoubleOrNull() ?: 0.0, state.hours.toDoubleOrNull() ?: 0.0),
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
                    squareFootage = state.squareFootage.toDoubleOrNull() ?: 0.0,
                    sodType = state.sodType,
                    difficulty = state.difficulty,
                    laborConfig = LaborConfig(state.workers.toIntOrNull() ?: 0, state.hourlyRate.toDoubleOrNull() ?: 0.0, state.hours.toDoubleOrNull() ?: 0.0),
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
    val squareFootage: String = "",
    val sodType: String = "Bahia",
    val costPerSqFt: Double = 0.35,
    val difficulty: Difficulty = Difficulty.MODERATE,
    val workers: String = "2",
    val hourlyRate: String = "25.0",
    val hours: String = "8.0",
    val lastResult: EstimateResult? = null
)
