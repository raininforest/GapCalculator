package com.github.raininforest.ui.models.details

data class Chart(
    val color: String,
    val thicknessPx: Int,
    val data: List<Point>
)
