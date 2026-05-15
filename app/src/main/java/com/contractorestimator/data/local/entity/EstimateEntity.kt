package com.contractorestimator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.contractorestimator.domain.model.Difficulty
import com.contractorestimator.domain.model.ServiceType

@Entity(tableName = "estimates")
data class EstimateEntity(
    @PrimaryKey val id: String,
    val customerName: String,
    val date: Long,
    val serviceType: String,
    
    // Input fields (persistent individual fields for simplicity)
    val squareFootage: Double,
    val sodType: String,
    val difficulty: String,
    val workers: Int,
    val hourlyRate: Double,
    val estimatedHours: Double,
    
    // Result fields
    val adjustedSqFt: Double,
    val materialCost: Double,
    val laborCost: Double,
    val subtotal: Double,
    val profitAmount: Double,
    val taxAmount: Double,
    val totalEstimate: Double
)
