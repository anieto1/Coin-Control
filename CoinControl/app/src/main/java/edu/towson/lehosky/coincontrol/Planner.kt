package edu.towson.lehosky.coincontrol

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import network.UserFinancial

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Planner(
    userFinancials: List<UserFinancial>,
    currentDate: String
) {

    //Retrieve the UserFinancial object that has the current date clicked on the calender
    var financials = UserFinancial("", 0f, 0f, 0f, 0f, 0f)
    val idx = userFinancials.indexOfFirst { it.date == currentDate }
    if(idx >= 0) {
        financials = userFinancials[idx]
    }

    //Create a list of Bars (BarData) with the y-values being the corresponding float value based on the category
    val barChartData:List<BarData> = listOf(
        BarData(Point(1f, financials.bills), label = "Bills", color = MaterialTheme.colorScheme.primary),
        BarData(Point(2f, financials.rent), label = "Rent", color = MaterialTheme.colorScheme.primary),
        BarData(Point(3f, financials.groceries), label = "Groceries", color = MaterialTheme.colorScheme.primary),
        BarData(Point(4f, financials.personalSpending), label = "Spending", color = MaterialTheme.colorScheme.primary),
        BarData(Point(5f, financials.utilities), label = "Utilities", color = MaterialTheme.colorScheme.primary)
    )

    //Get all of the current y-values in the list of Bars and sort them so the y-axis labels have the correct increasing order
    val initialYValues = barChartData.map {data ->
        data.point.y
    }
    val sortedYValues = initialYValues.sorted()

    //Build the x-axis where the labels are the financial categories in the list (bills, utilities, ....)
    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .steps(barChartData.size)
        .bottomPadding(40.dp)
        .labelData { index -> barChartData[index].label }
        .axisLabelAngle(20f)
        .labelAndAxisLinePadding(20.dp)
        .startDrawPadding(20.dp)
        .startPadding(20.dp)
        .shouldDrawAxisLineTillEnd(true)
        .build()

    //Build the y-axis where the labels are the sorted float values from before in increasing order
    val yAxisData = AxisData.Builder()
        .steps(barChartData.size-1)
        .labelAndAxisLinePadding(20.dp)
        .axisOffset(20.dp)
        .labelData {index -> sortedYValues[index].toString()}
        .build()

    //Build the actual BarChart passing in the the list of BarData, the x-axis, and the y-axis
    val barChart = BarChartData(
        chartData = barChartData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        paddingEnd = 20.dp,
        showXAxis = true,
        showYAxis = true,
        horizontalExtraSpace = 30.dp,
    )


    Scaffold (
        topBar = {
            TopAppBar(title = {Text("Spending on $currentDate", textAlign = TextAlign.Center)})
        },
        content = {padding ->
            LazyColumn(
                modifier = Modifier.padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                item {
                    //Add the BarChart to the LazyColumn passing in the barChart variable built before
                    BarChart(
                        modifier = Modifier.padding(padding).height(750.dp).fillMaxWidth(),
                        barChartData = barChart
                    )
                }
            }
        }
    )

}