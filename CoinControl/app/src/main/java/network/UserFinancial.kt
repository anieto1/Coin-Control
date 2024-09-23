package network

//UserFinancial object used to hold the data for generating charts
//This object is what interacts with our RestAPI
data class UserFinancial(
    val date: String,
    val rent: Float,
    val bills: Float,
    val utilities: Float,
    val personalSpending: Float,
    val groceries: Float
)
