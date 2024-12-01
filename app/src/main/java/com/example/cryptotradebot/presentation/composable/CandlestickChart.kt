package com.example.cryptotradebot.presentation.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptotradebot.domain.model.Candlestick
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@Composable
fun CandlestickChart(
    candlesticks: List<Candlestick>,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { 
        mutableStateOf(-(candlesticks.size * 20f))
    }
    
    val textMeasurer = rememberTextMeasurer()
    val dateFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val dayFormat = remember { SimpleDateFormat("dd MMM", Locale.getDefault()) }
    val chartBackground = Color(0xFF1E1E1E)

    Box(
        modifier = modifier
            .background(chartBackground)
            .padding(start = 8.dp, end = 0.dp, bottom = 10.dp, top=25.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, gestureZoom, _ ->
                        scale = (scale * gestureZoom).coerceIn(0.5f, 3f)
                        offset = (offset + pan.x).coerceIn(
                            -(candlesticks.size * 20f),
                            0f
                        )
                    }
                }
        ) {
            if (candlesticks.isEmpty()) return@Canvas

            val visibleCount = 30
            val totalWidth = size.width - 60.dp.toPx()
            val candleWidth = (totalWidth / visibleCount) * 0.6f
            val spacing = (totalWidth / visibleCount) * 0.4f

            val startIndex = ((-offset / (candleWidth + spacing)) * scale).toInt()
                .coerceIn(0, (candlesticks.size - visibleCount).coerceAtLeast(0))
            val endIndex = (startIndex + visibleCount).coerceAtMost(candlesticks.size)
            val visibleCandlesticks = candlesticks.subList(startIndex, endIndex)

            val prices = visibleCandlesticks.flatMap { listOf(it.high.toFloat(), it.low.toFloat()) }
            val maxPrice = prices.maxOrNull() ?: 0f
            val minPrice = prices.minOrNull() ?: 0f
            val priceRange = (maxPrice - minPrice).coerceAtLeast(0.0001f)
            val chartHeight = size.height - 25.dp.toPx()

            val priceSteps = 5
            for (i in 0..priceSteps) {
                val price = minPrice + (priceRange * i / priceSteps)
                val y = chartHeight - (((price - minPrice) / priceRange) * chartHeight)
                
                val priceText = String.format("%.2f", price)
                val textWidth = textMeasurer.measure(priceText).size.width
                
                drawText(
                    textMeasurer = textMeasurer,
                    text = priceText,
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    ),
                    topLeft = Offset(size.width - textWidth - 2.dp.toPx(), y - 6.dp.toPx())
                )
                
                drawLine(
                    color = Color.White.copy(alpha = 0.1f),
                    start = Offset(0f, y),
                    end = Offset(size.width - 70.dp.toPx(), y),
                    strokeWidth = 1f
                )
            }

            visibleCandlesticks.forEachIndexed { index, candlestick ->
                val x = index * (candleWidth + spacing)
                if (x < -candleWidth || x > size.width + candleWidth) return@forEachIndexed

                val high = candlestick.high.toFloat()
                val low = candlestick.low.toFloat()
                val open = candlestick.open.toFloat()
                val close = candlestick.close.toFloat()

                val candleColor = if (close >= open) Color(0xFF00C853) else Color(0xFFD50000)

                val topY = chartHeight - (((high - minPrice) / priceRange) * chartHeight)
                val bottomY = chartHeight - (((low - minPrice) / priceRange) * chartHeight)
                drawLine(
                    color = candleColor,
                    start = Offset(x + candleWidth/2, topY),
                    end = Offset(x + candleWidth/2, bottomY),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )

                val openY = chartHeight - (((open - minPrice) / priceRange) * chartHeight)
                val closeY = chartHeight - (((close - minPrice) / priceRange) * chartHeight)
                val top = minOf(openY, closeY)
                val bottom = maxOf(openY, closeY)
                val candleHeight = abs(bottom - top).coerceAtLeast(2f)

                drawRect(
                    color = candleColor,
                    topLeft = Offset(x, top),
                    size = Size(candleWidth, candleHeight)
                )
            }

            var lastDay = -1
            val timeInterval = 7
            val timeSteps = visibleCount / timeInterval
            
            for (i in 0..timeSteps) {
                val candleIndex = startIndex + (i * timeInterval)
                if (candleIndex < candlesticks.size) {
                    val x = (i * timeInterval) * (candleWidth + spacing)
                    val date = Date(candlesticks[candleIndex].openTime)
                    val calendar = Calendar.getInstance().apply { time = date }
                    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
                    
                    val timeText = if (currentDay != lastDay) {
                        lastDay = currentDay
                        SimpleDateFormat("dd/MM", Locale.getDefault()).format(date)
                    } else {
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
                    }
                    
                    val textWidth = textMeasurer.measure(timeText).size.width
                    
                    drawText(
                        textMeasurer = textMeasurer,
                        text = timeText,
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        ),
                        topLeft = Offset(x, size.height - 15.dp.toPx())
                    )
                    
                    drawLine(
                        color = Color.White.copy(alpha = 0.1f),
                        start = Offset(x, 0f),
                        end = Offset(x, chartHeight),
                        strokeWidth = 1f
                    )
                }
            }
        }
    }
} 