package com.contractorestimator.domain.registry

import com.contractorestimator.domain.calculator.ServiceCalculator
import com.contractorestimator.domain.calculator.SodCalculator
import com.contractorestimator.domain.model.ServiceType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Registry to manage different service calculators.
 * This architecture allows adding new services by simply registering them here.
 */
@Singleton
class ServiceRegistry @Inject constructor(
    private val sodCalculator: SodCalculator
    // Add future calculators here (e.g., paversCalculator)
) {
    fun getCalculator(serviceType: ServiceType): ServiceCalculator {
        return when (serviceType) {
            ServiceType.SOD_INSTALLATION -> sodCalculator
            else -> sodCalculator // Default fallback, should be handled gracefully
        }
    }
}
