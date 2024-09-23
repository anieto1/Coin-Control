package database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface AccountDao {

    //Add an account to the Database using the Sign Up Page
    @Upsert
    suspend fun addAccount(account: Account)

    //Get the account being logged in with based on the login Email
    @Query("select * from accounts where email = :currentEmail")
    suspend fun getAccount(currentEmail: String): Account


    //Update an attributes of the current account
    @Update
    suspend fun updateAccount(account: Account)
}