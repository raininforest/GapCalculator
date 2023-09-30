package com.github.raininforest.android.ui.gap.details.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.raininforest.android.theme.darkestGray
import com.github.raininforest.android.theme.lightGray
import com.github.raininforest.android.ui.gap.details.PADDING
import com.github.raininforest.ui.details.data.GapDetailsState
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.core.axis.formatter.DecimalFormatAxisValueFormatter
import com.patrykandpatrick.vico.core.chart.scale.AutoScaleUp
import java.text.DecimalFormat

@Composable
fun ChartView(
    paddingValues: PaddingValues,
    data: GapDetailsState.GapDetailsData
) {
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        val chart = lineChart(
            lines = data.chartData.charts.map {
                lineSpec(
                    lineColor = Color(android.graphics.Color.parseColor(it.chartConfig.hexColor)),
                    lineBackgroundShader = null,
                    lineThickness = it.chartConfig.thicknessPx.dp
                )
            },
            axisValuesOverrider = remember { CustomAxisValuesOverrider() }
        )
        val chartModelProducer = remember { getChartEntryModelProducer(data.chartData) }
        chartModelProducer.setData(data.chartData)
        Chart(
            chart = chart,
            chartModelProducer = chartModelProducer,
            startAxis = rememberStartAxis(
                valueFormatter = DecimalFormatAxisValueFormatter(DecimalFormat("#.#")),
                label = axisLabelComponent(textSize = 6.sp, color = darkestGray),
                itemPlacer = CustomVerticalItemPlacer()
            ),
            bottomAxis = rememberBottomAxis(
                valueFormatter = DecimalFormatAxisValueFormatter(DecimalFormat("#.#")),
                label = axisLabelComponent(textSize = 6.sp, color = darkestGray),
            ),
            modifier = Modifier
                .width(350.dp)
                .height(350.dp)
                .background(lightGray)
                .align(Alignment.BottomCenter),
            autoScaleUp = AutoScaleUp.Full,
            isZoomEnabled = false,
            getXStep = { 1f },
            runInitialAnimation = false
        )
        Column(
            modifier = Modifier
                .padding(start = PADDING.dp, top = PADDING.dp)
                .align(Alignment.TopStart)
        ) {
            Text(
                text = data.textData.inputGapParameters.joinToString(separator = "\n"),
                style = MaterialTheme.typography.caption,
                textAlign = TextAlign.Start
            )
        }
        Column(
            modifier = Modifier
                .padding(end = PADDING.dp, top = PADDING.dp)
                .align(Alignment.TopEnd)
        ) {
            Text(
                text = data.textData.outputGapParameters.joinToString(separator = "\n"),
                style = MaterialTheme.typography.caption,
                textAlign = TextAlign.End
            )
        }
    }
}
