package org.sharesanitizer.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object ArtisticIcons {
    
    val Home: ImageVector
        get() = ImageVector.Builder(
            name = "HandDrawnHome",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3.5f, 10.5f)
                lineTo(12f, 3.5f)
                lineTo(20.5f, 10.5f)
                moveTo(5f, 9f)
                lineTo(5.5f, 20f)
                lineTo(18.5f, 20f)
                lineTo(19f, 9f)
                moveTo(10f, 20f)
                lineTo(10.5f, 13f)
                lineTo(13.5f, 13f)
                lineTo(14f, 20f)
            }
        }.build()

    val Settings: ImageVector
        get() = ImageVector.Builder(
            name = "HandDrawnSettings",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 8f)
                arcTo(4f, 4f, 0f, false, false, 12f, 16f)
                arcTo(4f, 4f, 0f, false, false, 12f, 8f)
                
                // Add outer gear bumps
                moveTo(12f, 3f)
                lineTo(12f, 5f)
                moveTo(12f, 19f)
                lineTo(12f, 21f)
                moveTo(3f, 12f)
                lineTo(5f, 12f)
                moveTo(19f, 12f)
                lineTo(21f, 12f)
                moveTo(5.5f, 5.5f)
                lineTo(7f, 7f)
                moveTo(17f, 17f)
                lineTo(18.5f, 18.5f)
                moveTo(5.5f, 18.5f)
                lineTo(7f, 17f)
                moveTo(17f, 7f)
                lineTo(18.5f, 5.5f)
            }
        }.build()

    val Info: ImageVector
        get() = ImageVector.Builder(
            name = "HandDrawnInfo",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 3f)
                arcTo(9f, 9f, 0f, false, false, 12f, 21f)
                arcTo(9f, 9f, 0f, false, false, 12f, 3f)
                
                moveTo(12f, 11f)
                lineTo(12f, 17f)
                moveTo(12f, 7f)
                lineTo(12.01f, 7f)
            }
        }.build()

    val Link: ImageVector
        get() = ImageVector.Builder(
            name = "HandDrawnLink",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(9f, 17f)
                arcTo(5f, 5f, 0f, false, true, 9f, 7f)
                lineTo(11f, 7f)
                moveTo(15f, 7f)
                arcTo(5f, 5f, 0f, false, true, 15f, 17f)
                lineTo(13f, 17f)
                moveTo(8f, 12f)
                lineTo(16f, 12f)
            }
        }.build()

    val Image: ImageVector
        get() = ImageVector.Builder(
            name = "HandDrawnImage",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(4f, 5f)
                lineTo(20f, 5f)
                lineTo(20f, 19f)
                lineTo(4f, 19f)
                lineTo(4f, 5f)
                
                moveTo(4f, 15f)
                lineTo(9f, 10f)
                lineTo(14f, 15f)
                lineTo(16f, 13f)
                lineTo(20f, 17f)
                
                moveTo(15f, 8f)
                arcTo(1.5f, 1.5f, 0f, false, false, 15f, 8.01f)
            }
        }.build()

    val Collections: ImageVector
        get() = ImageVector.Builder(
            name = "HandDrawnCollections",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(6f, 2f)
                lineTo(22f, 2f)
                lineTo(22f, 18f)
                lineTo(6f, 18f)
                lineTo(6f, 2f)
                
                moveTo(2f, 6f)
                lineTo(2f, 22f)
                lineTo(18f, 22f)
                
                moveTo(6f, 14f)
                lineTo(10f, 10f)
                lineTo(15f, 15f)
                lineTo(17f, 13f)
                lineTo(22f, 18f)
            }
        }.build()

    val Folder: ImageVector
        get() = ImageVector.Builder(
            name = "HandDrawnFolder",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3f, 4f)
                lineTo(10f, 4f)
                lineTo(12f, 7f)
                lineTo(21f, 7f)
                lineTo(21f, 20f)
                lineTo(3f, 20f)
                lineTo(3f, 4f)
            }
        }.build()

    val ArrowBack: ImageVector
        get() = ImageVector.Builder(
            name = "HandDrawnArrowBack",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(20f, 12f)
                lineTo(4f, 12f)
                moveTo(10f, 18f)
                lineTo(4f, 12f)
                lineTo(10f, 6f)
            }
        }.build()

    val Share: ImageVector
        get() = ImageVector.Builder(
            name = "HandDrawnShare",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(18f, 8f)
                arcTo(3f, 3f, 0f, false, false, 18f, 2f)
                arcTo(3f, 3f, 0f, false, false, 18f, 8f)
                
                moveTo(6f, 15f)
                arcTo(3f, 3f, 0f, false, false, 6f, 9f)
                arcTo(3f, 3f, 0f, false, false, 6f, 15f)
                
                moveTo(18f, 22f)
                arcTo(3f, 3f, 0f, false, false, 18f, 16f)
                arcTo(3f, 3f, 0f, false, false, 18f, 22f)
                
                moveTo(8.59f, 13.51f)
                lineTo(15.42f, 17.49f)
                moveTo(15.41f, 6.51f)
                lineTo(8.59f, 10.49f)
            }
        }.build()

    val Save: ImageVector
        get() = ImageVector.Builder(
            name = "HandDrawnSave",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(19f, 21f)
                lineTo(5f, 21f)
                lineTo(5f, 3f)
                lineTo(16f, 3f)
                lineTo(19f, 6f)
                lineTo(19f, 21f)
                
                moveTo(17f, 21f)
                lineTo(17f, 13f)
                lineTo(7f, 13f)
                lineTo(7f, 21f)
                
                moveTo(7f, 3f)
                lineTo(7f, 8f)
                lineTo(15f, 8f)
            }
        }.build()

    val ContentCopy: ImageVector
        get() = ImageVector.Builder(
            name = "HandDrawnContentCopy",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(16f, 1f)
                lineTo(4f, 1f)
                arcTo(2f, 2f, 0f, false, false, 2f, 3f)
                lineTo(2f, 17f)
                lineTo(4f, 17f)
                lineTo(4f, 3f)
                lineTo(16f, 3f)
                lineTo(16f, 1f)
                
                moveTo(20f, 7f)
                lineTo(9f, 7f)
                arcTo(2f, 2f, 0f, false, false, 7f, 9f)
                lineTo(7f, 21f)
                arcTo(2f, 2f, 0f, false, false, 9f, 23f)
                lineTo(20f, 23f)
                arcTo(2f, 2f, 0f, false, false, 22f, 21f)
                lineTo(22f, 9f)
                arcTo(2f, 2f, 0f, false, false, 20f, 7f)
            }
        }.build()

    val Check: ImageVector
        get() = ImageVector.Builder(
            name = "HandDrawnCheck",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(20f, 6f)
                lineTo(9f, 17f)
                lineTo(4f, 12f)
            }
        }.build()

    val Shield: ImageVector
        get() = ImageVector.Builder(
            name = "HandDrawnShield",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 22f)
                arcTo(14f, 14f, 0f, false, true, 4f, 10f)
                lineTo(4f, 5f)
                lineTo(12f, 2f)
                lineTo(20f, 5f)
                lineTo(20f, 10f)
                arcTo(14f, 14f, 0f, false, true, 12f, 22f)
            }
        }.build()

    val Delete: ImageVector
        get() = ImageVector.Builder(
            name = "HandDrawnDelete",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3f, 6f)
                lineTo(21f, 6f)
                moveTo(8f, 6f)
                lineTo(8f, 4f)
                arcTo(2f, 2f, 0f, false, true, 10f, 2f)
                lineTo(14f, 2f)
                arcTo(2f, 2f, 0f, false, true, 16f, 4f)
                lineTo(16f, 6f)
                moveTo(19f, 6f)
                lineTo(19f, 20f)
                arcTo(2f, 2f, 0f, false, true, 17f, 22f)
                lineTo(7f, 22f)
                arcTo(2f, 2f, 0f, false, true, 5f, 20f)
                lineTo(5f, 6f)
                moveTo(10f, 11f)
                lineTo(10f, 17f)
                moveTo(14f, 11f)
                lineTo(14f, 17f)
            }
        }.build()

    val Undo: ImageVector
        get() = ImageVector.Builder(
            name = "HandDrawnUndo",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3f, 10f)
                lineTo(9f, 4f)
                moveTo(3f, 10f)
                lineTo(9f, 16f)
                moveTo(3f, 10f)
                lineTo(14f, 10f)
                arcTo(6f, 6f, 0f, false, true, 20f, 16f)
                arcTo(6f, 6f, 0f, false, true, 14f, 22f)
                lineTo(9f, 22f)
            }
        }.build()
}

