package com.contractorestimator.domain.calculator

import com.contractorestimator.domain.model.*

/**
 * Strategy pattern for different service calculations.
 */
interface ServiceCalculator {
    fun calculate(input: EstimateInput): EstimateResult
}

/**
 * Specialized calculator for Sod Installation.
 */
class SodCalculator : ServiceCalculator {
    override fun calculate(input: EstimateInput): EstimateResult {
        val pricing = input.pricingConfig
        val labor = input.laborConfig

        // 1. Waste Factor
        val adjustedSqFt = input.squareFootage * (1 + pricing.wasteFactor)

        // 2. Material Cost
        val rawMaterialCost = adjustedSqFt * pricing.costPerSqFt

        // 3. Labor Cost
        val laborCost = labor.numberOfWorkers * labor.hourlyRate * labor.estimatedHours

        // 4. Difficulty Modifier
        val baseSubtotal = (rawMaterialCost + laborCost) * input.difficulty.modifier

        // 5. Margin & Tax
        val profitAmount = baseSubtotal * pricing.profitMargin
        val subtotalWithProfit = baseSubtotal + profitAmount
        val taxAmount = subtotalWithProfit * pricing.taxRate

        val total = subtotalWithProfit + taxAmount

        return EstimateResult(
            adjustedSqFt = adjustedSqFt,
            materialCost = rawMaterialCost,
            laborCost = laborCost,
            subtotal = baseSubtotal,
            profitAmount = profitAmount,
            taxAmount = taxAmount,
            totalEstimate = total
        )
    }
}
