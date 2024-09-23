package database

import androidx.room.Entity
import androidx.room.PrimaryKey

//Account object used for the database
@Entity(tableName = "accounts")
data class Account(
    var firstName: String,
    var lastName: String,
    @PrimaryKey
    var email: String,
    var password: String,
    var income: String,
    var expenses: String,
    var bills: String,
    var savings: String
)
