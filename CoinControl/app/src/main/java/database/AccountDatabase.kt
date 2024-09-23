package database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Account::class], version = 5, exportSchema = true)
abstract class AccountDatabase : RoomDatabase() {
    //Create the Database Access Object to our database and return it
    abstract fun accountDao() : AccountDao
}