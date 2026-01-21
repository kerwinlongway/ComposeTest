package maps.voice.gps.navigation.offline.driving.directions.route.composetest

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import maps.voice.gps.navigation.offline.driving.directions.route.composetest.ui.theme.ComposeTestTheme

@Composable
fun TwoTexts(modifier: Modifier = Modifier, text1: String, text2: String) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .background(Color.Green)
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(end = 1.dp)
                .background(Color.Blue)
                .padding(start = 4.dp)
                .wrapContentWidth(Alignment.Start),
            text = text1
        )
        VerticalDivider(
            color = Color.Black,
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 1.dp)
                .background(Color.Red)
                .firstBaselineToTop(35.dp)
                .wrapContentWidth(Alignment.End),

            text = text2
        )
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    /*TwoTexts(
                        modifier = Modifier.padding(innerPadding),
                        text1 = "xxx",
                        text2 = "sdhgsjakghaskg\ngsdagdas\nadshgaskhg"
                    )*/
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        // 场景 1: 左短右长 -> 左边只占一点，右边占据绝大部分
                        SmartTwoTexts(
                            "Short",
                            "This is a very very long text that will definitely wrap"
                        )

                        // 场景 2: 左长右短 -> 右边只占一点，左边占据绝大部分
//        SmartTwoTexts("This is a very very long text that will definitely wrap", "Short")

                        // 场景 3: 都长 -> 平分 (50/50)
//        SmartTwoTexts(
//            "Long text 1 that is huge enough",
//            "Long text 2 that is also huge enough"
//        )
                    }
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeTestTheme {

    }
}

fun Modifier.firstBaselineToTop(
    firstBaselineToTop: Dp
) = layout { measurable, constraints ->
    // Measure the composable
    val placeable = measurable.measure(constraints)

    if (placeable[FirstBaseline] == AlignmentLine.Unspecified) {
        return@layout with(placeable) {
            layout(width, height) {
                // Where the composable gets placed
                placeable.placeRelative(0, height)
            }
        }
    }

    // Check the composable has a first baseline
//    check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
    val firstBaseline = placeable[FirstBaseline]

    // Height of the composable with padding - first baseline
    val placeableY = firstBaselineToTop.roundToPx() - firstBaseline
    val height = placeable.height + placeableY
    layout(placeable.width, height) {
        // Where the composable gets placed
        placeable.placeRelative(0, placeableY)
    }
}

@Composable
fun SmartTwoTexts(
    text1: String,
    text2: String,
    modifier: Modifier = Modifier
) {
    // 使用自定义 Layout
    Layout(
        content = {
            // 我们约定：组件顺序固定为 Text1, Divider, Text2
            Text(
                text = text1,
                modifier = Modifier
                    .wrapContentWidth(Alignment.Start)
//                    .background(color = Color.Green) // 内容若不足填满分配空间，靠左对齐
            )
            VerticalDivider(
                color = Color.Black,
                thickness = 1.dp,
                modifier = Modifier
                    .padding(horizontal = 4.dp)

            )
            Text(
                text = text2,
                modifier = Modifier
                    .wrapContentWidth(Alignment.End)
//                    .background(color = Color.Red) // 内容若不足填满分配空间，靠右对齐
            )
        },
        modifier = modifier
    ) { measurables, constraints ->
        // 1. 拆解子组件 (假设顺序固定)
        val text1Measurable = measurables[0]
        val dividerMeasurable = measurables[1]
        val text2Measurable = measurables[2]


        val dividerWidth = dividerMeasurable.minIntrinsicWidth(constraints.maxHeight)
//        val dividerWidth = 100

        // 3. 计算文本可用的总宽度和半宽阈值
        val totalTextWidth = (constraints.maxWidth - dividerWidth).coerceAtLeast(0)
        val halfWidth = totalTextWidth / 2

        // 4. 【核心逻辑】询问两个文本的“理想单行宽度” (Intrinsic Query)
        // maxIntrinsicWidth: 问它“给你无限宽，你实际单行有多长？”
        val t1Desired = text1Measurable.maxIntrinsicWidth(constraints.maxHeight)
//        val t1Desired = 100
        val t2Desired = text2Measurable.maxIntrinsicWidth(constraints.maxHeight)
//        val t2Desired = 100

        // 5. 决策分配宽度的逻辑
        val t1Width: Int
        val t2Width: Int

        if (t1Desired < halfWidth) {
            // 场景A: 左边文本很短，左边拿它想要的，剩下的给右边
            t1Width = t1Desired
            t2Width = t2Desired.coerceAtMost(totalTextWidth - t1Width)
        } else if (t2Desired < halfWidth) {
            // 场景B: 右边文本很短，右边拿它想要的，剩下的给左边
            t2Width = t2Desired
            t1Width = t1Desired.coerceAtMost(totalTextWidth - t2Width)
        } else {
            // 场景C: 两个都很长，平分
            t1Width = halfWidth
            t2Width = totalTextWidth - t1Width // 剩下的给右边 (避免除法精度丢失像素)
        }

        val dividerMiddleGap = ((totalTextWidth - t1Width - t2Width) / 2).coerceAtLeast(0)

        // 6. 带着算好的宽度，正式测量两个文本
        val text1Placeable = text1Measurable.measure(
            Constraints.fixedWidth(t1Width) // 强行锁定宽度
        )
        val text2Placeable = text2Measurable.measure(
            Constraints.fixedWidth(t2Width) // 强行锁定宽度
        )

        // 7. 处理高度：找出最高的文本，以此作为整个 Layout 的高度
        val maxHeight = maxOf(text1Placeable.height, text2Placeable.height)

        // 8. 重新测量 Divider (为了让它 fillMaxHeight)
        // 注意：Divider 之前只是测量了宽度，现在我们需要给它具体的高度约束
        val finalDividerPlaceable = dividerMeasurable.measure(
            Constraints.fixedHeight(maxHeight)
        )

        Log.d(
            "tt_test",
            "SmartTwoTexts: dividerWidth=$dividerWidth finalDividerPlaceable=${finalDividerPlaceable.width} measuredWidth=${finalDividerPlaceable.measuredWidth}"
        )

        // 9. 布局摆放 (Layout Phase)
        layout(width = constraints.maxWidth, height = maxHeight) {
            var xPosition = 0

            // 放左边文本
            text1Placeable.placeRelative(x = xPosition, y = 0)
            xPosition += t1Width + dividerMiddleGap

            // 放分割线
            finalDividerPlaceable.placeRelative(x = xPosition, y = 0)
            xPosition += dividerWidth + dividerMiddleGap

            // 放右边文本
            text2Placeable.placeRelative(x = xPosition, y = 0)
        }
    }
}

@Preview(showBackground = true, widthDp = 300)
@Composable
fun SmartSplitPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        // 场景 1: 左短右长 -> 左边只占一点，右边占据绝大部分
        SmartTwoTexts("Short", "This is a very very long text that will definitely wrap")

        // 场景 2: 左长右短 -> 右边只占一点，左边占据绝大部分
        SmartTwoTexts("This is a very very long text that will definitely wrap", "Short")

        // 场景 3: 都长 -> 平分 (50/50)
        SmartTwoTexts(
            "Long text 1 that is huge enough",
            "Long text 2 that is also huge enough"
        )
    }
}

@Preview(showBackground = true, widthDp = 300)
@Composable
fun SmartSplitPreview2() {
    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()

        ) {
            var text1 by remember { mutableStateOf("Long text 1 that is huge enough") }
            var text2 by remember { mutableStateOf("Long text 2 that is also huge enough") }
            Text(
                text = text1,
                modifier = Modifier.clickable {
                    text1 += "111"
                }
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(
                modifier = Modifier
                    .background(color = Color.Red)
                    .height(20.dp)
                    .width(1.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = text2,
                modifier = Modifier.clickable {
                    text2 += "222"
                }
            )
        }
    }
}

@Composable
fun MyBasicColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        // Don't constrain child views further, measure them with given constraints
        // List of measured children
        val placeables = measurables.map { measurable ->
            // Measure each children
            measurable.measure(constraints)
        }

        // Set the size of the layout as big as it can
        layout(constraints.maxWidth, constraints.maxHeight) {
            // Track the y co-ord we have placed children up to
            var yPosition = 0

            // Place children in the parent layout
            placeables.forEach { placeable ->
                // Position item on the screen
                placeable.placeRelative(x = 0, y = yPosition)

                // Record the y co-ord placed up to
                yPosition += placeable.height
            }
        }
    }
}

@Composable
fun CallingComposable(modifier: Modifier = Modifier) {
    MyBasicColumn(modifier.padding(8.dp)) {
        Text("MyBasicColumn")
        Text("places items")
        Text("vertically.")
        Text("We've done it by hand!")
    }
}

