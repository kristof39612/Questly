package hu.bme.aut.szoftverarch.questly.data.auth

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val id: String,
    val email: String,
    val token: String,
    val username: String,
    val points: Int,
)