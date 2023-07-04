package com.setruth.timemanager.ui.screen.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@OptIn(ExperimentalTextApi::class)
@Composable
fun Clock(timeState: TimeState, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val isDarkMode = isSystemInDarkTheme()
        val textMeasure = rememberTextMeasurer()
        Canvas(modifier = Modifier.fillMaxSize()) {
            // 时钟距离时钟View的边距
            val outMargin = 20.dp
            val radius = (size.width.coerceAtMost(size.height) - outMargin.toPx()) / 2
            // 时钟数字距离时钟刻度的边距
            val inMargin = 15.dp
            // 时针刻度的长度
            val hourScaleLength = 12.dp
            // 秒针刻度的长度
            val secondScaleLength = 6.dp
            // 内指针距离指针的边距
            val inHandMargin = 1.dp

            val centerX = size.center.x
            val centerY = size.center.y

            drawBackground(
                isDarkMode, radius, centerX, centerY,
                secondScaleLength, hourScaleLength, outMargin, inMargin, textMeasure
            )
            drawHourHand(radius, outMargin, isDarkMode, centerX, centerY, inHandMargin, timeState)
            drawMinuteHand(isDarkMode, radius, centerX, centerY, inHandMargin, timeState)
            drawSecondHand(isDarkMode, centerX, centerY, radius, timeState)
        }
    }
}

private fun DrawScope.drawSecondHand(
    isDarkMode: Boolean,
    centerX: Float,
    centerY: Float,
    radius: Float,
    timeState: TimeState
) {
    val seconds = timeState.second

    // 在不同的区间内秒针显示的阴影方向不同
    if (!isDarkMode) {
        if (seconds % 30 == 0) {
//                    secondHandPaint.setShadowLayer(8, 0, 0, android.graphics.Color.GRAY);
        } else if (seconds < 30) {
//                    secondHandPaint.setShadowLayer(8, 5, -5, Color.GRAY);
        } else {
//                    secondHandPaint.setShadowLayer(8, -5, -5, Color.GRAY);
        }
    }
    rotate(seconds * 6f) {
        drawLine(
            color = Color.Red,
            start = Offset(centerX, centerY + 10.dp.toPx()),
            end = Offset(centerX, centerY - radius),
            strokeWidth = 5f,
            cap = StrokeCap.Round,
        )
    }
    drawCircle(color = Color.Red, radius = 5.dp.toPx())
    drawCircle(color = Color.White, radius = 2.dp.toPx())
}

private fun DrawScope.drawMinuteHand(
    isDarkMode: Boolean,
    radius: Float,
    centerX: Float,
    centerY: Float,
    inHandMargin: Dp,
    timeState: TimeState
) {
    val minutes = timeState.minute
    val seconds = timeState.second

    // 这里计算分钟总和，在不同的区间内分针显示的阴影方向不同
    val totalMinutes = if (seconds == 0) minutes.toFloat() else minutes + seconds / 60f
    if (!isDarkMode) {
        if (totalMinutes % 30 == 0f) {
//                    minuteHandPaint.setShadowLayer(8, 0, 0, android.graphics.Color.GRAY);
        } else if (totalMinutes < 30) {
//                    minuteHandPaint.setShadowLayer(8, 5, -5, Color.GRAY);
        } else {
//                    minuteHandPaint.setShadowLayer(8, -5, -5, Color.GRAY);
        }
    }
    val clockRadius = radius / 2
    val minuteHandLength = clockRadius / 4
    rotate((minutes + seconds / 60f) * 6) {
        drawLine(
            color = if (isDarkMode) Color.White else Color.Black,
            start = Offset(centerX, centerY),
            end = Offset(centerX, centerY - radius + minuteHandLength),
            strokeWidth = 20f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = if (isDarkMode) Color.Black else Color.White,
            start = Offset(centerX, centerY - radius + minuteHandLength - inHandMargin.toPx()),
            end = Offset(centerX, centerY - radius + minuteHandLength + minuteHandLength + inHandMargin.toPx()),
            strokeWidth = 8f,
            cap = StrokeCap.Round,
        )
    }
}

private fun DrawScope.drawHourHand(
    radius: Float,
    outMargin: Dp,
    isDarkMode: Boolean,
    centerX: Float,
    centerY: Float,
    inHandMargin: Dp,
    timeState: TimeState
) {
    val hours = timeState.hour
    val minutes = timeState.minute
    val clockRadius = radius / 2 - outMargin.toPx()
    // 这里计算时钟总和，在不同的区间内时针显示的阴影方向不同
    val totalHours = if (minutes == 0) hours.toFloat() else hours + minutes / 60f
    if (!isDarkMode) {
        if (totalHours % 6 == 0f) {
//                    hourHandPaint.setShadowLayer(8, 0, 0, Color.GRAY);
        } else if (totalHours < 6) {
//                    hourHandPaint.setShadowLayer(8, 5, -5, Color.GRAY);
        } else {
//                    hourHandPaint.setShadowLayer(8, -5, -5, Color.GRAY);
        }
    }
    val hourHandLength = clockRadius * 2 / 3
    val hourHandLength2 = hourHandLength / 2
    rotate((hours + minutes / 60f) * 30) {
        drawLine(
            color = if (isDarkMode) Color.White else Color.Black,
            start = Offset(centerX, centerY),
            end = Offset(centerX, centerY - radius + hourHandLength),
            strokeWidth = 25f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = if (isDarkMode) Color.Black else Color.White,
            start = Offset(centerX, centerY - radius + hourHandLength - inHandMargin.toPx()),
            end = Offset(centerX, centerY - radius + hourHandLength + hourHandLength2 + inHandMargin.toPx()),
            strokeWidth = 10f,
            cap = StrokeCap.Round
        )
    }
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawBackground(
    isDarkMode: Boolean,
    radius: Float,
    centerX: Float,
    centerY: Float,
    secondScaleLength: Dp,
    hourScaleLength: Dp,
    outMargin: Dp,
    inMargin: Dp,
    textMeasure: TextMeasurer,
) {
    if (isDarkMode) {
        drawCircle(color = Color.Black, radius = radius)
    } else {
        drawCircle(color = Color.White, radius = radius)
    }
    drawClockScale(centerX, centerY, radius, secondScaleLength, isDarkMode, hourScaleLength)
    // drawTimeText todo 建议使用原生 api
//    drawTimeText(isDarkMode, radius, outMargin, inMargin, textMeasure)
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawTimeText(
    isDarkMode: Boolean,
    radius: Float,
    outMargin: Dp,
    inMargin: Dp,
    textMeasure: TextMeasurer
) {
    val textColor = if (isDarkMode) Color.White else Color.Black
    // todo 12 和 6 是对不齐的需要分两次绘制
    repeat(12) {
        val timeText = if (it == 0) "12" else it.toString()
        // 三角函数边长C
        val lengthC = radius.roundToInt() - (outMargin.roundToPx() + inMargin.roundToPx())
        val degrees = it * 30
        // x轴坐标
        val x = sin(Math.toRadians(degrees.toDouble())) * lengthC + radius + inMargin.roundToPx() / 5
        // y轴坐标
        val y = radius - cos(Math.toRadians(degrees.toDouble())) * lengthC +
                radius + outMargin.roundToPx() + inMargin.roundToPx()
        drawText(
            textMeasure,
            timeText,
            topLeft = Offset(x.toFloat(), y.toFloat()),
            style = TextStyle(
                textColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.W100,
                fontStyle = FontStyle.Italic
            )
        )
    }
}

private fun DrawScope.drawClockScale(
    centerX: Float,
    centerY: Float,
    radius: Float,
    secondScaleLength: Dp,
    isDarkMode: Boolean,
    hourScaleLength: Dp
) {
    // 小刻度
    repeat(60) {
        val angle = it * 6
        if (angle % 5 != 0) {
            rotate(angle.toFloat()) {
                drawLine(
                    color = Color.Gray,
                    start = Offset(centerX, centerY - radius),
                    end = Offset(centerX, centerY - radius + secondScaleLength.toPx()),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
    // 大刻度
    repeat(12) {
        rotate(30f * it) {
            drawLine(
                color = if (isDarkMode) Color.White else Color.Black,
                start = Offset(centerX, centerY - radius),
                end = Offset(centerX, centerY - radius + hourScaleLength.toPx()),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )
        }
    }
}
