package com.example.football

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF101827)
private val Panel = Color(0xFF1B2638)
private val Green = Color(0xFF19C37D)
private val Muted = Color(0xFF9AA8BA)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { YallaMatchApp() }
    }
}

data class Match(
    val competition: String,
    val home: String,
    val away: String,
    val score: String,
    val time: String,
    val live: Boolean
)

private val demoMatches = listOf(
    Match("الدوري الإسباني", "ريال مدريد", "برشلونة", "1 - 1", "مباشر 67′", true),
    Match("الدوري الإنجليزي", "ليفربول", "مانشستر سيتي", "— : —", "20:00", false),
    Match("دوري أبطال أوروبا", "بايرن ميونخ", "باريس سان جيرمان", "— : —", "22:00", false),
    Match("الدوري الإيطالي", "ميلان", "إنتر", "2 - 0", "انتهت", false)
)

@Composable
fun YallaMatchApp() {
    var selectedTab by remember { mutableStateOf("المباريات") }
    var selectedDay by remember { mutableStateOf("اليوم") }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Green,
            background = Navy,
            surface = Panel,
            onBackground = Color.White,
            onSurface = Color.White
        )
    ) {
        Scaffold(
            containerColor = Navy,
            topBar = {
                Column(
                    Modifier.fillMaxWidth().background(Navy).padding(horizontal = 18.dp, vertical = 16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(42.dp).background(Green, RoundedCornerShape(13.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SportsSoccer, contentDescription = null, tint = Navy)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("FOOTBALL", fontSize = 21.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Text("كل المباريات في مكان واحد", color = Muted, fontSize = 12.sp)
                        }
                        Text("● LIVE", color = Green, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(18.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("أمس", "اليوم", "غدًا").forEach { day ->
                            FilterChip(
                                selected = selectedDay == day,
                                onClick = { selectedDay = day },
                                label = { Text(day) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Green,
                                    selectedLabelColor = Navy,
                                    labelColor = Color.White
                                )
                            )
                        }
                    }
                }
            },
            bottomBar = {
                NavigationBar(containerColor = Panel, contentColor = Color.White) {
                    val tabs = listOf(
                        Triple("المباريات", Icons.Default.SportsSoccer, "المباريات"),
                        Triple("الدوريات", Icons.Default.EmojiEvents, "الدوريات"),
                        Triple("المواعيد", Icons.Default.CalendarMonth, "المواعيد"),
                        Triple("ملخصات", Icons.Default.Tv, "ملخصات"),
                        Triple("المفضلة", Icons.Default.Favorite, "المفضلة")
                    )
                    tabs.forEach { (label, icon, key) ->
                        NavigationBarItem(
                            selected = selectedTab == key,
                            onClick = { selectedTab = key },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Green,
                                selectedTextColor = Green,
                                indicatorColor = Green.copy(alpha = 0.15f),
                                unselectedIconColor = Muted,
                                unselectedTextColor = Muted
                            )
                        )
                    }
                }
            }
        ) { padding ->
            when (selectedTab) {
                "الدوريات" -> LeaguesScreen(Modifier.padding(padding))
                "المواعيد" -> FixturesScreen(Modifier.padding(padding), selectedDay)
                "ملخصات" -> HighlightsScreen(Modifier.padding(padding))
                "المفضلة" -> FavoritesScreen(Modifier.padding(padding))
                else -> MatchesScreen(Modifier.padding(padding), selectedDay)
            }
        }
    }
}

@Composable
fun MatchesScreen(modifier: Modifier = Modifier, day: String) {
    Column(modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        SectionHeading(if (day == "اليوم") "مباريات $day" else "مباريات $day", "نتائج تجريبية")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 18.dp)) {
            items(demoMatches) { match -> MatchCard(match) }
            item { ApiNotice() }
        }
    }
}

@Composable
fun MatchCard(match: Match) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Panel)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(match.competition, color = Muted, fontSize = 12.sp, modifier = Modifier.weight(1f))
                if (match.live) Text("● مباشر", color = Green, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(match.home, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                Text(match.score, fontSize = 20.sp, fontWeight = FontWeight.Black)
                Text(match.away, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold, textAlign = androidx.compose.ui.text.style.TextAlign.End)
            }
            Spacer(Modifier.height(10.dp))
            Text(match.time, color = if (match.live) Green else Muted, fontSize = 12.sp)
        }
    }
}

@Composable
fun LeaguesScreen(modifier: Modifier = Modifier) {
    val leagues = listOf("🏆 الدوري الإسباني", "🏆 الدوري الإنجليزي", "🏆 دوري أبطال أوروبا", "🏆 الدوري الإيطالي", "🏆 الدوري المغربي")
    Column(modifier.fillMaxSize().padding(16.dp)) {
        SectionHeading("الدوريات", "اختر بطولة لمتابعة الترتيب")
        leagues.forEachIndexed { index, league ->
            Card(
                Modifier.fillMaxWidth().padding(bottom = 10.dp).clickable { },
                colors = CardDefaults.cardColors(containerColor = Panel),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(league, Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                    Text("عرض الترتيب  ›", color = Green, fontSize = 12.sp)
                }
            }
        }
        ApiNotice()
    }
}

@Composable
fun FixturesScreen(modifier: Modifier = Modifier, day: String) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        SectionHeading("جدول المباريات", "المواعيد حسب توقيت هاتفك")
        demoMatches.filter { it.time.contains(":") || it.live }.forEach { MatchCard(it) }
        Spacer(Modifier.height(10.dp))
        ApiNotice()
    }
}

@Composable
fun HighlightsScreen(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        SectionHeading("ملخصات المباريات", "فيديوهات من مصادر رسمية")
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.padding(18.dp)) {
                Icon(Icons.Default.Tv, contentDescription = null, tint = Green, modifier = Modifier.size(36.dp))
                Spacer(Modifier.height(10.dp))
                Text("ستظهر الملخصات هنا", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(6.dp))
                Text("سنربط هذه الصفحة بمصادر فيديو رسمية أو مرخّصة عند إعداد النسخة المتصلة بالإنترنت.", color = Muted)
            }
        }
    }
}

@Composable
fun FavoritesScreen(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        SectionHeading("المفضلة", "فرقك وبطولاتك المفضلة")
        Text("اضغط على الفرق أو البطولات لإضافتها إلى المفضلة في النسخة القادمة.", color = Muted)
    }
}

@Composable
fun SectionHeading(title: String, subtitle: String) {
    Column(Modifier.padding(top = 6.dp, bottom = 14.dp)) {
        Text(title, fontSize = 21.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = Muted, fontSize = 12.sp)
    }
}

@Composable
fun ApiNotice() {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF263244)), shape = RoundedCornerShape(12.dp)) {
        Text(
            "تنبيه: هذه نسخة أولية ببيانات تجريبية. النتائج المباشرة والترتيب والإشعارات تحتاج إلى API رياضي موثوق ومفتاح API.",
            Modifier.padding(14.dp),
            color = Color(0xFFDCE6F2),
            fontSize = 12.sp
        )
    }
}
