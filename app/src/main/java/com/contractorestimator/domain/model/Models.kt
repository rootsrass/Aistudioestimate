package com.contractorestimator.domain.model

import java.util.Date
import java.util.UUID

/**
 * Represents a full job estimate saved in the system.
 */
data class Estimate(
    val id: String = UUID.randomUUID().toString(),
    val customerName: String,
    val serviceType: ServiceType,
    val date: Date = Date(),
    val input: EstimateInput,
    val result: EstimateResult
)

/**
 * The raw inputs provided by the contractor.
 */
data class EstimateInput(
    val squareFootage: Double,
    val sodType: String,
    val difficulty: Difficulty = Difficulty.MODERATE,
    val laborConfig: LaborConfig,
    val pricingConfig: PricingConfig
)

/**
 * The calculated result of an estimate.
 */
data class EstimateResult(
    val adjustedSqFt: Double,
    val materialCost: Double,
    val laborCost: Double,
    val subtotal: Double,
    val profitAmount: Double,
    val taxAmount: Double,
    val totalEstimate: Double
)

enum class Difficulty(val modifier: Double) {
    EASY(1.0),
    MODERATE(1.25),
    DIFFICULT(1.5)
}

enum class ServiceType(val displayName: String) {
    SOD_INSTALLATION("Sod Installation"),
    PAVERS("Pavers"),
    RETAINING_WALLS("Retaining Walls"),
    IRRIGATION("Irrigation"),
    LANDSCAPING("Landscaping")
}

data class LaborConfig(
    val numberOfWorkers: Int,
    val hourlyRate: Double,
    val estimatedHours: Double
)

data class PricingConfig(
    val costPerSqFt: Double,
    val wasteFactor: Double = 0.10, // Default 10%
    val profitMargin: Double = 0.20, // Default 20%
    val taxRate: Double = 0.07 // Default 7%
)
