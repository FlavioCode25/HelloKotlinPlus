package com.example.hellokotlinplus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hellokotlinplus.ui.theme.HelloKotlinPlusTheme
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HelloKotlinPlusTheme {
                HelloWorldApp()
            }
        }
    }
}

data class LanguageGreeting(
    val languageName: String,
    val flagEmoji: String,
    val template: String, // e.g. "Hello, %s!"
    val sampleDefault: String = "World"
)

val SupportedLanguages = listOf(
    LanguageGreeting("English", "🇺🇸", "Hello, %s!"),
    LanguageGreeting("Spanish", "🇪🇸", "Hola, %s!"),
    LanguageGreeting("French", "🇫🇷", "Bonjour, %s!"),
    LanguageGreeting("German", "🇩🇪", "Hallo, %s!"),
    LanguageGreeting("Italian", "🇮🇹", "Ciao, %s!"),
    LanguageGreeting("Japanese", "🇯🇵", "こんにちは、%s"),
    LanguageGreeting("Korean", "🇰🇷", "안녕하세요, %s"),
    LanguageGreeting("Portuguese", "🇵🇹", "Olá, %s!"),
    LanguageGreeting("Russian", "🇷🇺", "Привет, %s!"),
    LanguageGreeting("Turkish", "🇹🇷", "Merhaba, %s!"),
    LanguageGreeting("Arabic", "🇸🇦", "مرحباً يا %s!")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelloWorldApp() {
    var userName by rememberSaveable { mutableStateOf("") }
    var selectedLanguageIndex by rememberSaveable { mutableStateOf(0) }
    var waveCount by rememberSaveable { mutableStateOf(0) }
    var isWavingAnimationTriggered by remember { mutableStateOf(false) }

    val selectedLanguage = SupportedLanguages.getOrElse(selectedLanguageIndex) { SupportedLanguages[0] }
    val displayName = userName.trim().ifEmpty { "World" }
    val fullGreeting = selectedLanguage.template.format(displayName)

    val timeOfDayGreeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 6..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            in 18..22 -> "Good evening"
            else -> "Good night"
        }
    }

    val focusManager = LocalFocusManager.current

    // Continuous subtle waving animation
    val infiniteTransition = rememberInfiniteTransition(label = "waveTransition")
    val idleRotation by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idleWaveRotation"
    )

    // Extra button press wave scale animation
    val buttonScale by animateFloatAsState(
        targetValue = if (isWavingAnimationTriggered) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 400f),
        finishedListener = { isWavingAnimationTriggered = false },
        label = "buttonScale"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            userName = ""
                            selectedLanguageIndex = 0
                            waveCount = 0
                        },
                        modifier = Modifier.testTag("reset_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.reset_button)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.navigationBars),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 640.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hero Greeting Card
                GreetingHeroCard(
                    fullGreeting = fullGreeting,
                    timeOfDay = timeOfDayGreeting,
                    language = selectedLanguage,
                    waveRotation = idleRotation,
                )

                // Wave Action & Milestone Section
                WaveActionCard(
                    waveCount = waveCount,
                    buttonScale = buttonScale,
                    onWaveClick = {
                        waveCount++
                        isWavingAnimationTriggered = true
                    }
                )

                // Name Personalization Card
                NameInputCard(
                    userName = userName,
                    onNameChange = { userName = it },
                    onDone = { focusManager.clearFocus() }
                )

                // Multi-Language Selector
                LanguageSelectorCard(
                    languages = SupportedLanguages,
                    selectedIndex = selectedLanguageIndex,
                    onSelectLanguage = { selectedLanguageIndex = it }
                )

                // Compose Tech Specs Badge
                TechSpecsCard()

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun GreetingHeroCard(
    fullGreeting: String,
    timeOfDay: String,
    language: LanguageGreeting,
    waveRotation: Float
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("greeting_hero_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Animated waving emoji & badge
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                ) {
                    Text(
                        text = "👋",
                        fontSize = 38.sp,
                        modifier = Modifier.rotate(waveRotation)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Time of day pill
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "☀️ $timeOfDay",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                // Main Animated Greeting
                AnimatedContent(
                    targetState = fullGreeting,
                    transitionSpec = {
                        (slideInVertically { height -> height / 2 } + fadeIn())
                            .togetherWith(fadeOut())
                    },
                    label = "greetingTextAnimation"
                ) { greeting ->
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .testTag("main_greeting_text")
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Subtitle with Language Info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${language.flagEmoji} ${language.languageName}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun WaveActionCard(
    waveCount: Int,
    buttonScale: Float,
    onWaveClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.waves_count_label),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = "$waveCount",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("wave_count_text")
                    )
                }

                // Friendly Milestone Badge
                val milestone = when {
                    waveCount >= 50 -> "🌟 Master Greeter!"
                    waveCount >= 20 -> "🎉 Super Friendly!"
                    waveCount >= 10 -> "✨ Hello Enthusiast!"
                    waveCount >= 5 -> "👋 Great Connection!"
                    waveCount >= 1 -> "🌱 First Wave!"
                    else -> "Ready to say hi?"
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Text(
                        text = milestone,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Button(
                onClick = onWaveClick,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .scale(buttonScale)
                    .testTag("wave_button")
            ) {
                Icon(
                    imageVector = Icons.Default.WavingHand,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.say_hello_button),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun NameInputCard(
    userName: String,
    onNameChange: (String) -> Unit,
    onDone: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.name_input_label),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            OutlinedTextField(
                value = userName,
                onValueChange = onNameChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.name_input_placeholder),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                trailingIcon = {
                    if (userName.isNotEmpty()) {
                        IconButton(
                            onClick = { onNameChange("") },
                            modifier = Modifier.testTag("clear_name_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear name input"
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onDone() }),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("name_input")
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LanguageSelectorCard(
    languages: List<LanguageGreeting>,
    selectedIndex: Int,
    onSelectLanguage: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.languages_title),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                languages.forEachIndexed { index, lang ->
                    val isSelected = index == selectedIndex
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectLanguage(index) },
                        label = {
                            Text(
                                text = "${lang.flagEmoji} ${lang.languageName}",
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        modifier = Modifier.testTag("lang_chip_${lang.languageName.lowercase()}")
                    )
                }
            }
        }
    }
}

@Composable
fun TechSpecsCard() {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = stringResource(R.string.quick_facts_title),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TechFeatureRow("UI Framework", stringResource(R.string.fact_ui_framework))
                TechFeatureRow("Language", stringResource(R.string.fact_language))
                TechFeatureRow("Design System", stringResource(R.string.fact_architecture))
            }
        }
    }
}

@Composable
fun TechFeatureRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HelloWorldPreview() {
    HelloKotlinPlusTheme {
        HelloWorldApp()
    }
}