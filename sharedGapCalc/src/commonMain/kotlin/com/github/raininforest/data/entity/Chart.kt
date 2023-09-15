package com.github.raininforest.data.entity

data class Chart(
    val color: String,
    val thicknessPx: Int,
    val data: List<Point>
)
