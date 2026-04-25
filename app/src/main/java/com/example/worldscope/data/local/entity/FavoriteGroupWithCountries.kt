package com.example.worldscope.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class FavoriteGroupWithCountries(
    @Embedded val group: FavoriteGroupEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "alpha2Code",
        associateBy = Junction(
            value = FavoriteGroupItemCrossRef::class,
            parentColumn = "groupId",
            entityColumn = "alpha2Code"
        )
    )
    val countries: List<FavoriteCountryEntity>
)
