package me.thinhbuzz.vietnamcalendar.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import me.thinhbuzz.vietnamcalendar.ui.theme.VietNamCalendarTheme
import androidx.datastore.preferences.core.stringPreferencesKey

class CalendarWidgetConfigActivity : ComponentActivity() {
    
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get the widget ID from the intent
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        
        // If we don't have a valid widget ID, exit
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
        
        setContent {
            VietNamCalendarTheme {
                WidgetConfigScreen(
                    onConfigComplete = { viewType ->
                        saveWidgetConfig(viewType)
                    }
                )
            }
        }
    }
    
    private fun saveWidgetConfig(viewType: String) {
        lifecycleScope.launch {
            val glanceId = GlanceAppWidgetManager(this@CalendarWidgetConfigActivity)
                .getGlanceIdBy(appWidgetId)
            
            updateAppWidgetState(
                context = this@CalendarWidgetConfigActivity,
                definition = PreferencesGlanceStateDefinition,
                glanceId = glanceId
            ) { prefs ->
                prefs.toMutablePreferences().apply {
                    this[stringPreferencesKey("view_type")] = viewType
                }
            }
            
            CalendarWidget().update(this@CalendarWidgetConfigActivity, glanceId)
            
            val resultValue = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            setResult(RESULT_OK, resultValue)
            finish()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetConfigScreen(
    onConfigComplete: (String) -> Unit
) {
    var selectedOption by remember { mutableStateOf("month") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cấu hình Widget") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Chọn kiểu hiển thị widget:",
                style = MaterialTheme.typography.titleMedium
            )
            
            Column(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WidgetTypeOption(
                    text = "Hiển thị theo tháng",
                    selected = selectedOption == "month",
                    onClick = { selectedOption = "month" }
                )
                WidgetTypeOption(
                    text = "Hiển thị theo tuần",
                    selected = selectedOption == "week",
                    onClick = { selectedOption = "week" }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { onConfigComplete(selectedOption) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Xác nhận")
            }
        }
    }
}

@Composable
fun WidgetTypeOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text)
    }
}