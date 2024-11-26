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
    var offset by remember { mutableStateOf(0f) }
    var selectedCandlestick by remember { mutableStateOf<Candlestick?>(null) }
    var touchPosition by remember { mutableStateOf<Offset?>(null) }
    
    val textMeasurer = rememberTextMeasurer()
    val dateFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val chartBackground = Color(0xFF1E1E1E)

    Box(
        modifier = modifier
            .background(chartBackground)
            .padding(16.dp),
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
                            -size.width.toFloat(),
                            size.width.toFloat()
                        )
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { position ->
                            touchPosition = position
                            val candleIndex = getCandleIndexAtPosition(position, scale, offset)
                            selectedCandlestick = candlesticks.getOrNull(candleIndex)
                        },
                        onTap = { position ->
                            val candleIndex = getCandleIndexAtPosition(position, scale, offset)
                            selectedCandlestick = candlesticks.getOrNull(candleIndex)
                        }
                    )
                }
        ) {
            if (candlesticks.isEmpty()) return@Canvas

            val visibleCount = 20
            val totalWidth = size.width
            val candleWidth = (totalWidth / visibleCount) * 0.8f
            val spacing = (totalWidth / visibleCount) * 0.2f

            val startIndex = ((-offset / (candleWidth + spacing)) * scale).toInt()
                .coerceIn(0, (candlesticks.size - visibleCount).coerceAtLeast(0))
            val endIndex = (startIndex + visibleCount).coerceAtMost(candlesticks.size)
            val visibleCandlesticks = candlesticks.subList(startIndex, endIndex)

            val prices = visibleCandlesticks.flatMap { listOf(it.high.toFloat(), it.low.toFloat()) }
            val maxPrice = prices.maxOrNull() ?: 0f
            val minPrice = prices.minOrNull() ?: 0f
            val priceRange = (maxPrice - minPrice).coerceAtLeast(0.0001f)
            val chartHeight = size.height

            // Mumları çiz
            visibleCandlesticks.forEachIndexed { index, candlestick ->
                val x = index * (candleWidth + spacing)
                if (x < -candleWidth || x > size.width + candleWidth) return@forEachIndexed

                val high = candlestick.high.toFloat()
                val low = candlestick.low.toFloat()
                val open = candlestick.open.toFloat()
                val close = candlestick.close.toFloat()

                val candleColor = if (close >= open) Color(0xFF00C853) else Color(0xFFD50000)

                // Fitil
                val topY = chartHeight - (((high - minPrice) / priceRange) * chartHeight)
                val bottomY = chartHeight - (((low - minPrice) / priceRange) * chartHeight)
                drawLine(
                    color = candleColor,
                    start = Offset(x + candleWidth/2, topY),
                    end = Offset(x + candleWidth/2, bottomY),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )

                // Gövde
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

            // Tooltip çizimi
            touchPosition?.let { position ->
                if (position.x >= 0 && position.x <= size.width && 
                    position.y >= 0 && position.y <= size.height) {
                    
                    val price = getPriceAtPosition(position.y, minPrice, maxPrice, chartHeight)
                    val tooltipWidth = 120f
                    val tooltipHeight = 40f
                    
                    val tooltipX = (position.x + tooltipWidth).coerceAtMost(size.width - tooltipWidth)
                    val tooltipY = position.y.coerceIn(tooltipHeight, size.height - tooltipHeight)
                    
                    // Tooltip background
                    drawRect(
                        color = Color(0xFF2D2D2D).copy(alpha = 0.95f),
                        topLeft = Offset(tooltipX - tooltipWidth, tooltipY - tooltipHeight/2),
                        size = Size(tooltipWidth, tooltipHeight)
                    )
                    
                    val priceText = String.format("%.2f", price)
                    val textWidth = textMeasurer.measure(priceText).size.width
                    
                    drawText(
                        textMeasurer = textMeasurer,
                        text = priceText,
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        topLeft = Offset(
                            tooltipX - tooltipWidth/2 - textWidth/2,
                            tooltipY - 12
                        )
                    )
                }
            }
        }

        // Seçili mum için tooltip
        selectedCandlestick?.let { candlestick ->
            Surface(
                modifier = Modifier.padding(8.dp),
                color = Color(0xFF2D2D2D).copy(alpha = 0.95f),
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = dateFormat.format(Date(candlestick.openTime)),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Fiyat: ${candlestick.close}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (candlestick.close.toFloat() >= candlestick.open.toFloat())
                                Color(0xFF00C853) else Color(0xFFD50000),
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = "Hacim: ${candlestick.volume}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    )
                }
            }
        }
    }
}

private fun getCandleIndexAtPosition(position: Offset, scale: Float, offset: Float): Int {
    return ((position.x + offset) / (scale * 10)).toInt()
}

private fun getPriceAtPosition(y: Float, minPrice: Float, maxPrice: Float, chartHeight: Float): Float {
    val priceRange = maxPrice - minPrice
    return maxPrice - (y / chartHeight) * priceRange
} 