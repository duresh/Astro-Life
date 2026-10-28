package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.ZodiacSign

@Composable
fun ZodiacSignEmblem(
    sign: ZodiacSign,
    color: Color = Color(0xFF1E88E5),
    size: Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = w * 0.065f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val thinStroke = Stroke(width = w * 0.045f, cap = StrokeCap.Round, join = StrokeJoin.Round)

        when (sign) {
            ZodiacSign.ARIES -> {
                // 1. මේෂ - එළුවා (Ram with curled horns)
                // Left Horn
                val leftHorn = Path().apply {
                    moveTo(w * 0.5f, h * 0.55f)
                    cubicTo(w * 0.35f, h * 0.2f, w * 0.12f, h * 0.25f, w * 0.12f, h * 0.48f)
                    cubicTo(w * 0.12f, h * 0.65f, w * 0.3f, h * 0.65f, w * 0.35f, h * 0.5f)
                }
                // Right Horn
                val rightHorn = Path().apply {
                    moveTo(w * 0.5f, h * 0.55f)
                    cubicTo(w * 0.65f, h * 0.2f, w * 0.88f, h * 0.25f, w * 0.88f, h * 0.48f)
                    cubicTo(w * 0.88f, h * 0.65f, w * 0.7f, h * 0.65f, w * 0.65f, h * 0.5f)
                }
                drawPath(leftHorn, color, style = stroke)
                drawPath(rightHorn, color, style = stroke)

                // Face / Snout
                val face = Path().apply {
                    moveTo(w * 0.42f, h * 0.48f)
                    lineTo(w * 0.42f, h * 0.75f)
                    cubicTo(w * 0.42f, h * 0.88f, w * 0.58f, h * 0.88f, w * 0.58f, h * 0.75f)
                    lineTo(w * 0.58f, h * 0.48f)
                }
                drawPath(face, color, style = stroke)
                drawCircle(color, radius = w * 0.035f, center = Offset(w * 0.5f, h * 0.78f), style = Fill)
            }

            ZodiacSign.TAURUS -> {
                // 2. වෘෂභ - ගවයා (Bull head with crescent horns)
                // Majestic sweeping horns
                val horns = Path().apply {
                    moveTo(w * 0.15f, h * 0.22f)
                    cubicTo(w * 0.25f, h * 0.42f, w * 0.75f, h * 0.42f, w * 0.85f, h * 0.22f)
                }
                drawPath(horns, color, style = stroke)

                // Head Circle
                drawCircle(color, radius = w * 0.25f, center = Offset(w * 0.5f, h * 0.62f), style = stroke)

                // Snout / Muzzle ring
                val muzzle = Path().apply {
                    moveTo(w * 0.4f, h * 0.68f)
                    cubicTo(w * 0.4f, h * 0.78f, w * 0.6f, h * 0.78f, w * 0.6f, h * 0.68f)
                }
                drawPath(muzzle, color, style = thinStroke)
            }

            ZodiacSign.GEMINI -> {
                // 3. මිථුන - යුගලය / වීණාව සහ මුගුර (Twins / Coupled figures)
                // Arch top and bottom
                val topArch = Path().apply {
                    moveTo(w * 0.2f, h * 0.2f)
                    cubicTo(w * 0.35f, h * 0.14f, w * 0.65f, h * 0.14f, w * 0.8f, h * 0.2f)
                }
                val botArch = Path().apply {
                    moveTo(w * 0.2f, h * 0.8f)
                    cubicTo(w * 0.35f, h * 0.86f, w * 0.65f, h * 0.86f, w * 0.8f, h * 0.8f)
                }
                drawPath(topArch, color, style = stroke)
                drawPath(botArch, color, style = stroke)

                // Twin vertical pillars (Male & Female figures)
                drawLine(color, Offset(w * 0.36f, h * 0.18f), Offset(w * 0.36f, h * 0.82f), strokeWidth = w * 0.07f)
                drawLine(color, Offset(w * 0.64f, h * 0.18f), Offset(w * 0.64f, h * 0.82f), strokeWidth = w * 0.07f)

                // Celestial Lyre (වීණාව) cross ties
                drawLine(color, Offset(w * 0.36f, h * 0.42f), Offset(w * 0.64f, h * 0.42f), strokeWidth = w * 0.05f)
                drawLine(color, Offset(w * 0.36f, h * 0.58f), Offset(w * 0.64f, h * 0.58f), strokeWidth = w * 0.05f)
            }

            ZodiacSign.CANCER -> {
                // 4. කටක - කකුළුවා (Crab with large claws)
                // Carapace body
                drawCircle(color, radius = w * 0.18f, center = Offset(w * 0.5f, h * 0.5f), style = stroke)

                // Left pincer (claw)
                val leftClaw = Path().apply {
                    moveTo(w * 0.4f, h * 0.38f)
                    cubicTo(w * 0.25f, h * 0.2f, w * 0.12f, h * 0.35f, w * 0.24f, h * 0.48f)
                    cubicTo(w * 0.15f, h * 0.4f, w * 0.25f, h * 0.22f, w * 0.4f, h * 0.38f)
                }
                drawPath(leftClaw, color, style = stroke)

                // Right pincer (claw)
                val rightClaw = Path().apply {
                    moveTo(w * 0.6f, h * 0.62f)
                    cubicTo(w * 0.75f, h * 0.8f, w * 0.88f, h * 0.65f, w * 0.76f, h * 0.52f)
                    cubicTo(w * 0.85f, h * 0.6f, w * 0.75f, h * 0.78f, w * 0.6f, h * 0.62f)
                }
                drawPath(rightClaw, color, style = stroke)

                // Eye points
                drawCircle(color, radius = w * 0.03f, center = Offset(w * 0.44f, h * 0.44f), style = Fill)
                drawCircle(color, radius = w * 0.03f, center = Offset(w * 0.56f, h * 0.56f), style = Fill)
            }

            ZodiacSign.LEO -> {
                // 5. සිංහ - සිංහයා (Lion with magnificent mane & tail)
                // Lion muzzle / heart node
                drawCircle(color, radius = w * 0.12f, center = Offset(w * 0.3f, h * 0.62f), style = stroke)

                // Mane arch curving up and long royal tail
                val maneTail = Path().apply {
                    moveTo(w * 0.38f, h * 0.54f)
                    cubicTo(w * 0.42f, h * 0.2f, w * 0.75f, h * 0.18f, w * 0.72f, h * 0.45f)
                    cubicTo(w * 0.68f, h * 0.7f, w * 0.82f, h * 0.82f, w * 0.88f, h * 0.68f)
                }
                drawPath(maneTail, color, style = stroke)

                // Tuft of tail
                drawCircle(color, radius = w * 0.04f, center = Offset(w * 0.88f, h * 0.68f), style = Fill)
            }

            ZodiacSign.VIRGO -> {
                // 6. කන්‍යා - කන්‍යාව / වී කරල හා පහන (Maiden holding wheat ear & lamp)
                // The classical M symbol with the ribbon/sheaf of wheat
                val mPath = Path().apply {
                    moveTo(w * 0.15f, h * 0.72f)
                    lineTo(w * 0.15f, h * 0.32f)
                    cubicTo(w * 0.22f, h * 0.22f, w * 0.32f, h * 0.22f, w * 0.36f, h * 0.32f)
                    lineTo(w * 0.36f, h * 0.72f)
                    moveTo(w * 0.36f, h * 0.32f)
                    cubicTo(w * 0.42f, h * 0.22f, w * 0.52f, h * 0.22f, w * 0.56f, h * 0.32f)
                    lineTo(w * 0.56f, h * 0.75f)
                    // The loop of wheat ear / lamp ribbon
                    cubicTo(w * 0.58f, h * 0.95f, w * 0.82f, h * 0.75f, w * 0.75f, h * 0.5f)
                    lineTo(w * 0.62f, h * 0.88f)
                }
                drawPath(mPath, color, style = stroke)

                // Wheat Grain / Flame at peak
                drawCircle(color, radius = w * 0.04f, center = Offset(w * 0.75f, h * 0.45f), style = Fill)
            }

            ZodiacSign.LIBRA -> {
                // 7. තුලා - තරාදිය (Balance Scales - beam, fulcrum, and two balance pans)
                // Center Fulcrum Stand
                drawLine(color, Offset(w * 0.5f, h * 0.22f), Offset(w * 0.5f, h * 0.82f), strokeWidth = w * 0.065f)
                drawCircle(color, radius = w * 0.05f, center = Offset(w * 0.5f, h * 0.22f), style = Fill)

                // Top Balance Beam
                drawLine(color, Offset(w * 0.15f, h * 0.32f), Offset(w * 0.85f, h * 0.32f), strokeWidth = w * 0.07f)

                // Left Pan Cords & Dish
                val leftPan = Path().apply {
                    moveTo(w * 0.15f, h * 0.32f)
                    lineTo(w * 0.22f, h * 0.58f)
                    moveTo(w * 0.35f, h * 0.32f)
                    lineTo(w * 0.28f, h * 0.58f)
                    // Pan Dish
                    moveTo(w * 0.16f, h * 0.58f)
                    cubicTo(w * 0.18f, h * 0.68f, w * 0.32f, h * 0.68f, w * 0.34f, h * 0.58f)
                    close()
                }
                drawPath(leftPan, color, style = thinStroke)

                // Right Pan Cords & Dish
                val rightPan = Path().apply {
                    moveTo(w * 0.65f, h * 0.32f)
                    lineTo(w * 0.72f, h * 0.58f)
                    moveTo(w * 0.85f, h * 0.32f)
                    lineTo(w * 0.78f, h * 0.58f)
                    // Pan Dish
                    moveTo(w * 0.66f, h * 0.58f)
                    cubicTo(w * 0.68f, h * 0.68f, w * 0.82f, h * 0.68f, w * 0.84f, h * 0.58f)
                    close()
                }
                drawPath(rightPan, color, style = thinStroke)

                // Base stand
                drawLine(color, Offset(w * 0.35f, h * 0.82f), Offset(w * 0.65f, h * 0.82f), strokeWidth = w * 0.07f)
            }

            ZodiacSign.SCORPIO -> {
                // 8. වෘශ්චික - ගෝනුස්සා (Scorpion with arched stinger & claws)
                val mBody = Path().apply {
                    moveTo(w * 0.15f, h * 0.72f)
                    lineTo(w * 0.15f, h * 0.35f)
                    cubicTo(w * 0.22f, h * 0.25f, w * 0.32f, h * 0.25f, w * 0.36f, h * 0.35f)
                    lineTo(w * 0.36f, h * 0.72f)
                    moveTo(w * 0.36f, h * 0.35f)
                    cubicTo(w * 0.42f, h * 0.25f, w * 0.52f, h * 0.25f, w * 0.56f, h * 0.35f)
                    lineTo(w * 0.56f, h * 0.72f)
                    // Arched stinger tail
                    lineTo(w * 0.72f, h * 0.72f)
                    cubicTo(w * 0.85f, h * 0.72f, w * 0.88f, h * 0.5f, w * 0.78f, h * 0.45f)
                }
                drawPath(mBody, color, style = stroke)

                // Stinger Arrow barb
                val barb = Path().apply {
                    moveTo(w * 0.78f, h * 0.35f)
                    lineTo(w * 0.88f, h * 0.42f)
                    lineTo(w * 0.78f, h * 0.5f)
                }
                drawPath(barb, color, style = stroke)
            }

            ZodiacSign.SAGITTARIUS -> {
                // 9. ධනු - දුන්න සහ ඊතලය (Bow and Arrow / Archer)
                // Curved Bow
                val bow = Path().apply {
                    moveTo(w * 0.2f, h * 0.8f)
                    cubicTo(w * 0.15f, h * 0.5f, w * 0.5f, h * 0.15f, w * 0.8f, h * 0.2f)
                }
                drawPath(bow, color, style = stroke)

                // Bowstring
                drawLine(color, Offset(w * 0.2f, h * 0.8f), Offset(w * 0.8f, h * 0.2f), strokeWidth = w * 0.035f)

                // Flying Arrow shaft
                drawLine(color, Offset(w * 0.25f, h * 0.75f), Offset(w * 0.82f, h * 0.18f), strokeWidth = w * 0.07f)

                // Arrowhead
                val arrowhead = Path().apply {
                    moveTo(w * 0.62f, h * 0.18f)
                    lineTo(w * 0.82f, h * 0.18f)
                    lineTo(w * 0.82f, h * 0.38f)
                }
                drawPath(arrowhead, color, style = stroke)

                // Crossbar
                drawLine(color, Offset(w * 0.42f, h * 0.5f), Offset(w * 0.5f, h * 0.58f), strokeWidth = w * 0.065f)
            }

            ZodiacSign.CAPRICORN -> {
                // 10. මකර - මකරා / මත්ස්‍ය මුවා (Makara / Sea-Goat)
                val makara = Path().apply {
                    // Horn of goat
                    moveTo(w * 0.22f, h * 0.25f)
                    lineTo(w * 0.36f, h * 0.65f)
                    lineTo(w * 0.5f, h * 0.25f)
                    // Curving sea dragon tail with loop
                    cubicTo(w * 0.68f, h * 0.25f, w * 0.75f, h * 0.55f, w * 0.58f, h * 0.75f)
                    cubicTo(w * 0.42f, h * 0.9f, w * 0.65f, h * 0.98f, w * 0.75f, h * 0.82f)
                    cubicTo(w * 0.82f, h * 0.68f, w * 0.62f, h * 0.68f, w * 0.58f, h * 0.75f)
                }
                drawPath(makara, color, style = stroke)
            }

            ZodiacSign.AQUARIUS -> {
                // 11. කුම්භ - කළය / ජල ධාරාව (Water Pot / Kalasa with waves)
                // Water pot rim
                val potRim = Path().apply {
                    moveTo(w * 0.35f, h * 0.22f)
                    lineTo(w * 0.65f, h * 0.22f)
                }
                drawPath(potRim, color, style = stroke)

                // Pot body
                val potBody = Path().apply {
                    moveTo(w * 0.4f, h * 0.22f)
                    cubicTo(w * 0.25f, h * 0.35f, w * 0.28f, h * 0.52f, w * 0.5f, h * 0.55f)
                    cubicTo(w * 0.72f, h * 0.52f, w * 0.75f, h * 0.35f, w * 0.6f, h * 0.22f)
                }
                drawPath(potBody, color, style = stroke)

                // Flowing celestial water waves (ජල ධාරා)
                val wave1 = Path().apply {
                    moveTo(w * 0.15f, h * 0.68f)
                    cubicTo(w * 0.32f, h * 0.62f, w * 0.42f, h * 0.74f, w * 0.6f, h * 0.68f)
                    cubicTo(w * 0.72f, h * 0.62f, w * 0.82f, h * 0.74f, w * 0.88f, h * 0.68f)
                }
                val wave2 = Path().apply {
                    moveTo(w * 0.15f, h * 0.82f)
                    cubicTo(w * 0.32f, h * 0.76f, w * 0.42f, h * 0.88f, w * 0.6f, h * 0.82f)
                    cubicTo(w * 0.72f, h * 0.76f, w * 0.82f, h * 0.88f, w * 0.88f, h * 0.82f)
                }
                drawPath(wave1, color, style = stroke)
                drawPath(wave2, color, style = stroke)
            }

            ZodiacSign.PISCES -> {
                // 12. මීන - මසුන් දෙදෙනා (Two Fishes in sacred circle)
                // Left Swimming Fish
                val fish1 = Path().apply {
                    moveTo(w * 0.35f, h * 0.18f)
                    cubicTo(w * 0.15f, h * 0.4f, w * 0.15f, h * 0.65f, w * 0.35f, h * 0.82f)
                }
                // Right Swimming Fish
                val fish2 = Path().apply {
                    moveTo(w * 0.65f, h * 0.18f)
                    cubicTo(w * 0.85f, h * 0.4f, w * 0.85f, h * 0.65f, w * 0.65f, h * 0.82f)
                }
                drawPath(fish1, color, style = stroke)
                drawPath(fish2, color, style = stroke)

                // Connecting Celestial Band
                drawLine(color, Offset(w * 0.18f, h * 0.5f), Offset(w * 0.82f, h * 0.5f), strokeWidth = w * 0.065f)

                // Fish Tails / Fins
                drawLine(color, Offset(w * 0.28f, h * 0.18f), Offset(w * 0.42f, h * 0.22f), strokeWidth = w * 0.045f)
                drawLine(color, Offset(w * 0.28f, h * 0.82f), Offset(w * 0.42f, h * 0.78f), strokeWidth = w * 0.045f)
                drawLine(color, Offset(w * 0.72f, h * 0.18f), Offset(w * 0.58f, h * 0.22f), strokeWidth = w * 0.045f)
                drawLine(color, Offset(w * 0.72f, h * 0.82f), Offset(w * 0.58f, h * 0.78f), strokeWidth = w * 0.045f)
            }
        }
    }
}
