package network

import android.util.Log
import com.google.gson.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class GetFinancials {
    //Query the RestAPI based on the current email
    //This returns a list of UserFinancial objects held in the RestAPI based on the current date
    suspend fun getFinancials(email: String): List<UserFinancial> {
        var apiResult = listOf(UserFinancial("", 0f, 0f, 0f, 0f, 0f))
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .get()
                    .url("https://my-json-server.typicode.com/Webby234/coincontrol/$email")
                    .build()
                val response = client.newCall(request).execute()
                val body = response.body?.string()
                val gson = GsonBuilder().create()
                apiResult = gson.fromJson(body, Array<UserFinancial>::class.java).toList()
            }catch(e: Exception) {
                Log.e("API Get", "Unsuccessful Get Request $e")
            }
            apiResult
        }
    }

    //Add a UserFinancial object to the list corresponding to the current email on the RestAPI
    //This breaks down the UserFinancial object into a JSON format and adds (put) that object to the JSON list on the API
    suspend fun addFinancials(email: String, userFinancials: UserFinancial) {
        return withContext(Dispatchers.IO) {
            try {
                val jsonString = Gson().toJson(userFinancials)
                val body: RequestBody = jsonString.toRequestBody(JSON)
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://my-json-server.typicode.com/Webby234/coincontrol/$email")
                    .put(body)
                    .build()
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    Log.e("API Put", response.message)
                }
            }catch(e: Exception) {
                Log.e("API Put", "Could Not Make Put Request")
            }
        }
    }

    companion object {
        val JSON = "application/json".toMediaTypeOrNull()
    }
}