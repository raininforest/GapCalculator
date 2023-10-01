package com.github.raininforest.data.repository

import com.github.raininforest.usecases.calculator.Calculator
import com.github.raininforest.usecases.parametercomposer.ParameterComposer
import com.github.raininforest.data.entity.ChartData
import com.github.raininforest.data.entity.GapDetailsEntity
import com.github.raininforest.data.entity.TextData
import com.github.raininforest.db.DBSource

class GapDetailsRepository(
    private val dbSource: DBSource,
    private val calculator: Calculator,
    private val parameterComposer: ParameterComposer
) {
    suspend fun getGapDetails(gapId: Long): GapDetailsEntity {
        val gapTitle = dbSource.getGap(gapId = gapId)?.title.orEmpty()
        val gapInputParameters = dbSource.getGapParameters(gapId) ?: return dummyGapDetails
        val calculationResult = calculator.calculate(gapInputParameters)
        val gapOutputParameters = calculationResult.outputParameters

        return GapDetailsEntity(
            gapTitle = gapTitle,
            chartData = calculationResult.chartData,
            textData = parameterComposer.composeParameters(gapInputParameters, gapOutputParameters),
            warnings = calculationResult.warnings
        )
    }

    private val dummyGapDetails: GapDetailsEntity
        get() = GapDetailsEntity(
            gapTitle = "",
            chartData = ChartData(emptyList()),
            textData = TextData(emptyList(), emptyList()),
            warnings = emptyList()
        )
}