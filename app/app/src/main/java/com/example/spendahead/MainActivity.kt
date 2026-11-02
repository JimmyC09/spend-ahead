package com.example.spendahead

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.spendahead.ui.theme.SpendAheadTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            SpendAheadTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    SpendAheadScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SpendAheadScreen(
    modifier: Modifier = Modifier
) {
    var checkingBalance by remember { mutableStateOf("") }
    var creditCardDebt by remember { mutableStateOf("") }
    var paycheckAmount by remember { mutableStateOf("") }
    var bills by remember { mutableStateOf("") }
    var necessities by remember { mutableStateOf("") }
    var purchaseAmount by remember { mutableStateOf("") }
    var plannedCreditPayment by remember { mutableStateOf("") }

    var payWithCredit by remember { mutableStateOf(false) }

    var technicallyAvailable by remember { mutableStateOf(0.0) }
    var safeToSpend by remember { mutableStateOf(0.0) }

    var checkingAfterPurchase by remember { mutableStateOf(0.0) }
    var safePositionAfterPurchase by remember { mutableStateOf(0.0) }
    var afterBillsAndNeeds by remember { mutableStateOf(0.0) }

    var projectedCreditDebt by remember { mutableStateOf(0.0) }
    var remainingCreditDebt by remember { mutableStateOf(0.0) }

    var afterPaycheckOne by remember { mutableStateOf(0.0) }
    var afterCreditPayment by remember { mutableStateOf(0.0) }
    var afterPaycheckTwo by remember { mutableStateOf(0.0) }

    var showResults by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "SpendAhead",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "See what happens before you make the purchase.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        MoneyInput(
            label = "Checking balance",
            value = checkingBalance,
            onValueChange = { checkingBalance = it }
        )

        MoneyInput(
            label = "Credit card debt",
            value = creditCardDebt,
            onValueChange = { creditCardDebt = it }
        )

        MoneyInput(
            label = "Next paycheck",
            value = paycheckAmount,
            onValueChange = { paycheckAmount = it }
        )

        MoneyInput(
            label = "Upcoming bills",
            value = bills,
            onValueChange = { bills = it }
        )

        MoneyInput(
            label = "Estimated necessities",
            value = necessities,
            onValueChange = { necessities = it }
        )

        MoneyInput(
            label = "Purchase you are considering",
            value = purchaseAmount,
            onValueChange = { purchaseAmount = it }
        )

        MoneyInput(
            label = "Planned credit card payment",
            value = plannedCreditPayment,
            onValueChange = { plannedCreditPayment = it }
        )

        Text(
            text = "Pay with",
            fontWeight = FontWeight.Medium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = !payWithCredit,
                onClick = {
                    payWithCredit = false
                },
                label = {
                    Text("Checking")
                }
            )

            FilterChip(
                selected = payWithCredit,
                onClick = {
                    payWithCredit = true
                },
                label = {
                    Text("Credit Card")
                }
            )
        }

        Button(
            onClick = {

                val checking = checkingBalance.toDoubleOrNull() ?: 0.0
                val debt = creditCardDebt.toDoubleOrNull() ?: 0.0
                val paycheck = paycheckAmount.toDoubleOrNull() ?: 0.0
                val upcomingBills = bills.toDoubleOrNull() ?: 0.0
                val weeklyNeeds = necessities.toDoubleOrNull() ?: 0.0
                val purchase = purchaseAmount.toDoubleOrNull() ?: 0.0
                val creditPayment = plannedCreditPayment.toDoubleOrNull() ?: 0.0

                technicallyAvailable = checking

                safeToSpend =
                    checking - upcomingBills - weeklyNeeds

                checkingAfterPurchase =
                    if (payWithCredit) {
                        checking
                    } else {
                        checking - purchase
                    }

                safePositionAfterPurchase =
                    if (payWithCredit) {
                        safeToSpend
                    } else {
                        safeToSpend - purchase
                    }

                projectedCreditDebt =
                    if (payWithCredit) {
                        debt + purchase
                    } else {
                        debt
                    }

                afterBillsAndNeeds =
                    checkingAfterPurchase - upcomingBills - weeklyNeeds

                afterPaycheckOne =
                    afterBillsAndNeeds + paycheck

                afterCreditPayment =
                    afterPaycheckOne - creditPayment

                afterPaycheckTwo =
                    afterCreditPayment + paycheck

                remainingCreditDebt =
                    (projectedCreditDebt - creditPayment).coerceAtLeast(0.0)

                showResults = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Simulate Purchase")
        }

        if (showResults) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Projection",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            ResultCard(
                title = "Technically Available",
                amount = technicallyAvailable
            )

            HighlightResultCard(
                title = "Safe to Spend",
                amount = safeToSpend
            )

            ResultCard(
                title = "Checking After Purchase",
                amount = checkingAfterPurchase
            )

            ResultCard(
                title = "Safe Spending Position",
                amount = safePositionAfterPurchase
            )

            ResultCard(
                title = "After Bills + Necessities",
                amount = afterBillsAndNeeds
            )

            if (checkingAfterPurchase < 0) {
                WarningCard(
                    message = "This purchase would put your checking account below $0."
                )
            } else if (safePositionAfterPurchase < 0) {
                WarningCard(
                    message = "You can technically make this purchase, but it uses money reserved for bills or necessities."
                )
            }

            ResultCard(
                title = "Projected Credit Card Debt",
                amount = projectedCreditDebt
            )

            ResultCard(
                title = "After Paycheck 1",
                amount = afterPaycheckOne
            )

            ResultCard(
                title = "After Credit Payment",
                amount = afterCreditPayment
            )

            ResultCard(
                title = "Remaining Credit Card Debt",
                amount = remainingCreditDebt
            )

            ResultCard(
                title = "After Paycheck 2",
                amount = afterPaycheckTwo
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Money Over Time",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            MoneyGraph(
                values = listOf(
                    technicallyAvailable,
                    checkingAfterPurchase,
                    afterBillsAndNeeds,
                    afterPaycheckOne,
                    afterCreditPayment,
                    afterPaycheckTwo
                )
            )

            GraphLabels()

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Timeline",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            TimelineItem(
                label = "Starting checking",
                amount = technicallyAvailable
            )

            TimelineItem(
                label = if (payWithCredit) {
                    "Checking after purchase on credit"
                } else {
                    "Checking after purchase"
                },
                amount = checkingAfterPurchase
            )

            TimelineItem(
                label = "After bills + necessities",
                amount = afterBillsAndNeeds
            )

            TimelineItem(
                label = "After paycheck 1",
                amount = afterPaycheckOne
            )

            TimelineItem(
                label = "After credit payment",
                amount = afterCreditPayment
            )

            TimelineItem(
                label = "After paycheck 2",
                amount = afterPaycheckTwo
            )
        }
    }
}

@Composable
fun MoneyInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label)
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun ResultCard(
    title: String,
    amount: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "$%.2f".format(amount),
                fontWeight = FontWeight.Bold,
                color = if (amount < 0) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Composable
fun HighlightResultCard(
    title: String,
    amount: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "$%.2f".format(amount),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = if (amount < 0) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                }
            )
        }
    }
}

@Composable
fun WarningCard(
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

@Composable
fun TimelineItem(
    label: String,
    amount: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "$%.2f".format(amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (amount < 0) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Composable
fun MoneyGraph(
    values: List<Double>
) {
    val lineColor = MaterialTheme.colorScheme.primary
    val pointColor = MaterialTheme.colorScheme.secondary

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(24.dp)
        ) {

            if (values.isEmpty()) return@Canvas

            val maximum = values.maxOrNull() ?: 1.0
            val minimum = values.minOrNull() ?: 0.0

            val range =
                if (maximum == minimum) {
                    1.0
                } else {
                    maximum - minimum
                }

            val horizontalSpacing =
                size.width / (values.size - 1)

            val points = values.mapIndexed { index, value ->

                val normalized =
                    ((value - minimum) / range).toFloat()

                val x =
                    index * horizontalSpacing

                val y =
                    size.height - (normalized * size.height)

                Offset(
                    x = x,
                    y = y
                )
            }

            val path = Path()

            points.forEachIndexed { index, point ->

                if (index == 0) {
                    path.moveTo(
                        point.x,
                        point.y
                    )
                } else {
                    path.lineTo(
                        point.x,
                        point.y
                    )
                }
            }

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(
                    width = 6f
                )
            )

            points.forEach { point ->
                drawCircle(
                    color = pointColor,
                    radius = 10f,
                    center = point
                )
            }
        }
    }
}

@Composable
fun GraphLabels() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Start",
            style = MaterialTheme.typography.labelSmall
        )

        Text(
            text = "Purchase",
            style = MaterialTheme.typography.labelSmall
        )

        Text(
            text = "Bills",
            style = MaterialTheme.typography.labelSmall
        )

        Text(
            text = "Pay #1",
            style = MaterialTheme.typography.labelSmall
        )

        Text(
            text = "Card Pay",
            style = MaterialTheme.typography.labelSmall
        )

        Text(
            text = "Pay #2",
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SpendAheadPreview() {
    SpendAheadTheme {
        SpendAheadScreen()
    }
}