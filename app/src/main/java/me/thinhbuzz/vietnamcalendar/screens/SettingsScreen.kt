package me.thinhbuzz.vietnamcalendar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import me.thinhbuzz.vietnamcalendar.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context) }
    val coroutineScope = rememberCoroutineScope()
    
    val firstDayOfWeek by settingsDataStore.firstDayOfWeek.collectAsStateWithLifecycle(FirstDayOfWeek.SUNDAY)
    val monthYearFormat by settingsDataStore.monthYearFormat.collectAsStateWithLifecycle(MonthYearFormat.MONTH_YEAR)
    val dayMonthYearFormat by settingsDataStore.dayMonthYearFormat.collectAsStateWithLifecycle(DayMonthYearFormat.DMY_SLASH)
    val dayMonthFormat by settingsDataStore.dayMonthFormat.collectAsStateWithLifecycle(DayMonthFormat.DM_SLASH)
    val addLeadingZero by settingsDataStore.addLeadingZero.collectAsStateWithLifecycle(true)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cài đặt") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Leading zero section
            SettingsSection(title = "Thêm số 0 đằng trước") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = addLeadingZero,
                        onCheckedChange = { checked ->
                            coroutineScope.launch {
                                settingsDataStore.setAddLeadingZero(checked)
                            }
                        }
                    )
                    Text(
                        text = if (addLeadingZero) "Bật (07/2025)" else "Tắt (7/2025)",
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            // First day of week section
            SettingsSection(title = "Ngày đầu tuần") {
                RadioButtonGroup(
                    options = FirstDayOfWeek.values().toList(),
                    selectedOption = firstDayOfWeek,
                    onOptionSelected = { option ->
                        coroutineScope.launch {
                            settingsDataStore.setFirstDayOfWeek(option)
                        }
                    },
                    optionLabel = { it.displayName }
                )
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Month-Year format section
            SettingsSection(title = "Định dạng Tháng-Năm") {
                RadioButtonGroup(
                    options = MonthYearFormat.values().toList(),
                    selectedOption = monthYearFormat,
                    onOptionSelected = { option ->
                        coroutineScope.launch {
                            settingsDataStore.setMonthYearFormat(option)
                        }
                    },
                    optionLabel = { it.getDisplayName(addLeadingZero) }
                )
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Day-Month-Year format section
            SettingsSection(title = "Định dạng Ngày-Tháng-Năm") {
                RadioButtonGroup(
                    options = DayMonthYearFormat.values().toList(),
                    selectedOption = dayMonthYearFormat,
                    onOptionSelected = { option ->
                        coroutineScope.launch {
                            settingsDataStore.setDayMonthYearFormat(option)
                        }
                    },
                    optionLabel = { it.getDisplayName(addLeadingZero) }
                )
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Day-Month format section
            SettingsSection(title = "Định dạng Ngày-Tháng") {
                RadioButtonGroup(
                    options = DayMonthFormat.values().toList(),
                    selectedOption = dayMonthFormat,
                    onOptionSelected = { option ->
                        coroutineScope.launch {
                            settingsDataStore.setDayMonthFormat(option)
                        }
                    },
                    optionLabel = { it.getDisplayName(addLeadingZero) }
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@Composable
private fun <T> RadioButtonGroup(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    optionLabel: (T) -> String
) {
    Column {
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = option == selectedOption,
                    onClick = { onOptionSelected(option) }
                )
                Text(
                    text = optionLabel(option),
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}