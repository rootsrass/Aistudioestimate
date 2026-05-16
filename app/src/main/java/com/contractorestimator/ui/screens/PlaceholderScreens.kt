package com.contractorestimator.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.contractorestimator.domain.model.Difficulty
import com.contractorestimator.ui.viewmodel.EstimateViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Calculate,
            contentDescription = null,
            modifier = Modifier.size(84.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Contractor Estimate Pro",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            "Quick Sod Installation Estimates",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = { navController.navigate("new_estimate") },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Create New Estimate", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = { navController.navigate("saved_estimates") },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Default.History, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("View History", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEstimateScreen(
    navController: NavController,
    viewModel: EstimateViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Project Details") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(scrollState)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Area Input
            OutlinedTextField(
                value = uiState.customerName,
                onValueChange = { viewModel.onCustomerNameChange(it) },
                label = { Text("Customer Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = if (uiState.squareFootage == 0.0) "" else uiState.squareFootage.toString(),
                onValueChange = { viewModel.onSqFtChange(it) },
                label = { Text("Total Square Footage") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefix = { Text("sq ft") }
            )

            // Sod Type (Simplified for now)
            val sodTypes = mapOf("Bahia" to 0.35, "St Augustine" to 0.45, "Zoysia" to 0.55)
            Text("Sod Selection", style = MaterialTheme.typography.labelLarge)
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                sodTypes.forEach { (name, price) ->
                    FilterChip(
                        selected = uiState.sodType == name,
                        onClick = { viewModel.onSodTypeChange(name, price) },
                        label = { Text(name) }
                    )
                }
            }

            // Difficulty
            Text("Installation Difficulty", style = MaterialTheme.typography.labelLarge)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Difficulty.values().forEach { diff ->
                    InputChip(
                        selected = uiState.difficulty == diff,
                        onClick = { viewModel.onDifficultyChange(diff) },
                        label = { Text(diff.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            // Labor
            HorizontalDivider()
            Text("Labor Configuration", style = MaterialTheme.typography.titleSmall)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = uiState.workers.toString(),
                    onValueChange = { viewModel.onLaborUpdate(it.toIntOrNull() ?: 0, uiState.hourlyRate, uiState.hours) },
                    label = { Text("Crew Size") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = uiState.hours.toString(),
                    onValueChange = { viewModel.onLaborUpdate(uiState.workers, uiState.hourlyRate, it.toDoubleOrNull() ?: 0.0) },
                    label = { Text("Est. Hours") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    viewModel.calculateEstimate()
                    navController.navigate("summary/new")
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Generate Estimate Breakdown")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    navController: NavController,
    estimateId: String?,
    viewModel: EstimateViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val result = uiState.lastResult ?: return
    
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Estimate Summary") })
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(24.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Project Cost", style = MaterialTheme.typography.labelLarge)
                    Text(
                        currencyFormat.format(result.totalEstimate),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            SummaryRow("Material Cost (${uiState.sodType})", currencyFormat.format(result.materialCost))
            SummaryRow("Adjusted Sq Ft (+10%)", "${result.adjustedSqFt.toInt()} sq ft")
            SummaryRow("Labor Cost", currencyFormat.format(result.laborCost))
            SummaryRow("Difficulty Multiplier", "x${uiState.difficulty.modifier}")
            HorizontalDivider()
            SummaryRow("Profit Margin (20%)", currencyFormat.format(result.profitAmount))
            SummaryRow("Estimated Tax", currencyFormat.format(result.taxAmount))

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { 
                    viewModel.saveCurrentEstimate()
                    navController.navigate("saved_estimates") 
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Estimate")
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.secondary)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedEstimatesScreen(
    navController: NavController,
    viewModel: EstimateViewModel = hiltViewModel()
) {
    val estimates by viewModel.savedEstimates.collectAsState()
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.US) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Estimation History") })
        }
    ) { padding ->
        if (estimates.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No saved estimates found", color = MaterialTheme.colorScheme.secondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(estimates) { estimate ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate("summary/${estimate.id}") },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(estimate.customerName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("${estimate.input.sodType} - ${dateFormat.format(estimate.date)}", style = MaterialTheme.typography.bodySmall)
                            }
                            Text(
                                currencyFormat.format(estimate.result.totalEstimate),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(24.dp).verticalScroll(rememberScrollState())) {
            Text("General Defaults", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            SettingsInput("Default Labor Rate", "25.00", "$ / hr")
            SettingsInput("Default Crew Size", "2", "workers")
            SettingsInput("Waste Factor", "10", "%")
            SettingsInput("Profit Margin", "20", "%")
            SettingsInput("Tax Rate", "7", "%")
            
            Spacer(modifier = Modifier.height(32.dp))
            Text("Sod Pricing ($ / sq ft)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            SettingsInput("Bahia", "0.35", "")
            SettingsInput("St Augustine", "0.45", "")
            SettingsInput("Zoysia", "0.55", "")
        }
    }
}

@Composable
fun SettingsInput(label: String, value: String, suffix: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, modifier = Modifier.weight(1f))
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.width(120.dp),
            suffix = { Text(suffix) },
            textStyle = MaterialTheme.typography.bodySmall,
            singleLine = true
        )
    }
}
