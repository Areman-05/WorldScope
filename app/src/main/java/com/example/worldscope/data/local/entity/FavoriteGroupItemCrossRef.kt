package com.example.worldscope.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "favorite_group_items",
    primaryKeys = ["groupId", "alpha2Code"],
    foreignKeys = [
        ForeignKey(
            entity = FavoriteGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FavoriteCountryEntity::class,
            parentColumns = ["alpha2Code"],
            childColumns = ["alpha2Code"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["groupId"]),
        Index(value = ["alpha2Code"])
    ]
)
data class FavoriteGroupItemCrossRef(
    val groupId: Long,
    val alpha2Code: String,
    val addedAt: Long = System.currentTimeMillis()
)
