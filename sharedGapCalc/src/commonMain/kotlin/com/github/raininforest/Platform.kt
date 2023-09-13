package com.github.raininforest

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform