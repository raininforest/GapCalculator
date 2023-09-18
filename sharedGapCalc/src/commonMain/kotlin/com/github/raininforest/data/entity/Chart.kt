package com.github.raininforest.data.entity

data class Chart(
    val hexColor: String,
    val thicknessPx: Int,
    val function: List<Point>
)
