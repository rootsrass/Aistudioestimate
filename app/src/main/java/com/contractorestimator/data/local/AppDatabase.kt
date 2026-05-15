package com.contractorestimator.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.contractorestimator.data.local.dao.EstimateDao
import com.contractorestimator.data.local.entity.EstimateEntity

@Database(entities = [EstimateEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun estimateDao(): EstimateDao
}
