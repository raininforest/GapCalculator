package com.github.raininforest.data

import com.github.raininforest.data.entity.GapListItemEntity

class GapListRepository {
    suspend fun gapList(): List<GapListItemEntity> = listOf(
        GapListItemEntity(
            id = "1",
            title = "Untitled gap 1",
            date = "12.02.2021 13:48:01"
        ),
        GapListItemEntity(
            id = "2",
            title = "Untitled gap 2",
            date = "12.02.2022 13:48:02"
        ),
        GapListItemEntity(
            id = "3",
            title = "Untitled gap 3",
            date = "12.02.2023 13:48:03"
        )
    )
}
