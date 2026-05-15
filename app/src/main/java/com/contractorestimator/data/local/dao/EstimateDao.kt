package com.contractorestimator.data.local.dao

import androidx.room.*
import com.contractorestimator.data.local.entity.EstimateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EstimateDao {
    @Query("SELECT * FROM estimates ORDER BY date DESC")
    fun getAllEstimates(): Flow<List<EstimateEntity>>

    @Query("SELECT * FROM estimates WHERE id = :id")
    suspend fun getEstimateById(id: String): EstimateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstimate(estimate: EstimateEntity)

    @Delete
    suspend fun deleteEstimate(estimate: EstimateEntity)
}
