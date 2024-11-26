package com.example.cryptotradebot.presentation.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptotradebot.domain.model.Candlestick
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun CandlestickChart(
    candlesticks: List<Candlestick>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(
                start = 40.dp,
                end = 8.dp,
                top = 8.dp,
                bottom = 40.dp
            )
    ) {
        if (candlesticks.isEmpty()) return@Canvas

        val prices = candlesticks.flatMap { listOf(it.high.toFloat(), it.low.toFloat()) }
        val maxPrice = prices.maxOrNull() ?: 0f
        val minPrice = prices.minOrNull() ?: 0f
        val priceRange = maxPrice - minPrice
        val chartHeight = size.height - 16 // Kenar boşlukları için

        val candleWidth = (size.width - 48) / candlesticks.size
        val spacing = candleWidth * 0.2f
        val effectiveCandleWidth = candleWidth - spacing

        // Y ekseni çizgileri ve fiyat etiketleri
        val priceStep = (priceRange / 5f).roundToInt().toFloat()
        for (i in 0..5) {
            val price = minPrice + (priceStep * i)
            val y = chartHeight - (((price - minPrice) / priceRange) * chartHeight)
            
            // Yatay kılavuz çizgisi
            drawLine(
                color = Color.Gray.copy(alpha = 0.3f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
            
            // Fiyat etiketi
            val priceText = price.roundToInt().toString()
            drawText(
                textMeasurer = textMeasurer,
                text = priceText,
                style = TextStyle(fontSize = 10.sp, color = Color.Gray),
                topLeft = Offset(-35f, y - 8)
            )
        }

        // Mumları çiz
        candlesticks.forEachIndexed { index, candlestick ->
            val x = index * candleWidth + spacing/2

            val high = candlestick.high.toFloat()
            val low = candlestick.low.toFloat()
            val open = candlestick.open.toFloat()
            val close = candlestick.close.toFloat()

            val candleColor = if (close >= open) Color.Green else Color.Red

            // Fitil
            val topY = chartHeight - (((high - minPrice) / priceRange) * chartHeight)
            val bottomY = chartHeight - (((low - minPrice) / priceRange) * chartHeight)
            drawLine(
                color = candleColor,
                start = Offset(x + effectiveCandleWidth/2, topY),
                end = Offset(x + effectiveCandleWidth/2, bottomY),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )

            // Gövde
            val openY = chartHeight - (((open - minPrice) / priceRange) * chartHeight)
            val closeY = chartHeight - (((close - minPrice) / priceRange) * chartHeight)
            val top = minOf(openY, closeY)
            val bottom = maxOf(openY, closeY)
            val candleHeight = abs(bottom - top)

            drawRect(
                color = candleColor,
                topLeft = Offset(x, top),
                size = androidx.compose.ui.geometry.Size(
                    effectiveCandleWidth,
                    candleHeight
                )
            )

            // X ekseni zaman etiketleri (her 5 mumda bir)
            if (index % 5 == 0) {
                val time = dateFormat.format(Date(candlestick.openTime))
                drawText(
                    textMeasurer = textMeasurer,
                    text = time,
                    style = TextStyle(fontSize = 10.sp, color = Color.Gray),
                    topLeft = Offset(x, chartHeight + 8)
                )
            }
        }
    }
} 