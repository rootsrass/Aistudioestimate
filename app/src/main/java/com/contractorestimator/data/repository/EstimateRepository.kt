package com.contractorestimator.data.repository

import com.contractorestimator.data.local.dao.EstimateDao
import com.contractorestimator.data.local.entity.EstimateEntity
import com.contractorestimator.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EstimateRepository @Inject constructor(
    private val estimateDao: EstimateDao
) {
    fun getAllEstimates(): Flow<List<Estimate>> {
        return estimateDao.getAllEstimates().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getEstimateById(id: String): Estimate? {
        return estimateDao.getEstimateById(id)?.toDomain()
    }

    suspend fun saveEstimate(estimate: Estimate) {
        estimateDao.insertEstimate(estimate.toEntity())
    }

    suspend fun deleteEstimate(estimate: Estimate) {
        estimateDao.deleteEstimate(estimate.toEntity())
    }
}

// Mappers
fun EstimateEntity.toDomain(): Estimate {
    return Estimate(
        id = id,
        customerName = customerName,
        serviceType = ServiceType.valueOf(serviceType),
        date = Date(date),
        input = EstimateInput(
            squareFootage = squareFootage,
            sodType = sodType,
            difficulty = Difficulty.valueOf(difficulty),
            laborConfig = LaborConfig(workers, hourlyRate, estimatedHours),
            pricingConfig = PricingConfig(costPerSqFt = 0.0) // Prices are cached in Result
        ),
        result = EstimateResult(
            adjustedSqFt = adjustedSqFt,
            materialCost = materialCost,
            laborCost = laborCost,
            subtotal = subtotal,
            profitAmount = profitAmount,
            taxAmount = taxAmount,
            totalEstimate = totalEstimate
        )
    )
}

fun Estimate.toEntity(): EstimateEntity {
    return EstimateEntity(
        id = id,
        customerName = customerName,
        date = date.time,
        serviceType = serviceType.name,
        squareFootage = input.squareFootage,
        sodType = input.sodType,
        difficulty = input.difficulty.name,
        workers = input.laborConfig.numberOfWorkers,
        hourlyRate = input.laborConfig.hourlyRate,
        estimatedHours = input.laborConfig.estimatedHours,
        adjustedSqFt = result.adjustedSqFt,
        materialCost = result.materialCost,
        laborCost = result.laborCost,
        subtotal = result.subtotal,
        profitAmount = result.profitAmount,
        taxAmount = result.taxAmount,
        totalEstimate = result.totalEstimate
    )
}
