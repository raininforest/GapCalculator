package com.github.raininforest.navigation

enum class NavDestinations(val link: String, val argument: String = "") {
    GAP_LIST("gap_list"),
    GAP_DETAILS("gap_details", "gap_id"),
    GAP_EDIT("gap_edit", "gap_id")
}
