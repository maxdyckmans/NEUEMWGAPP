package org.example.project

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxHeight


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.Zoom
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianValueFormatter

import com.patrykandpatrick.vico.multiplatform.cartesian.data.lineSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.LineCartesianLayer.LineFill
import com.patrykandpatrick.vico.multiplatform.common.Fill
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.LineCartesianLayer.LineStroke.Dashed
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.LineCartesianLayer.LineProvider
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.multiplatform.common.LegendItem
import com.patrykandpatrick.vico.multiplatform.common.component.ShapeComponent
import com.patrykandpatrick.vico.multiplatform.common.component.TextComponent
import com.patrykandpatrick.vico.multiplatform.common.component.rememberTextComponent
import com.patrykandpatrick.vico.multiplatform.common.rememberHorizontalLegend


import androidx.compose.runtime.saveable.listSaver

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round



import kotlin.math.log



var stepSize: Double? = 0.0001
val regularTextSize = 14.sp
val regularTextWeight = FontWeight.W700

const val BASE = 2.0


var colors: ColorPalette = AppColors.Light



@Composable
fun App() {

    val countSubstances = 4
    colors = if (isSystemInDarkTheme()) AppColors.Dark else AppColors.Light


//    var rInfinite by remember { mutableStateOf(false)}
//    var steps by remember { mutableStateOf(300) }
//    val K = remember {mutableFloatStateOf(1.0f)}
//
//    //Inital Concentrations for displaying in textbox
//    val initConcentrations = remember{mutableStateListOf<Float>(1f, 1f, 1f, 1f)}
//
//    //Concentrations to trigger calculation
//    val concentrationChange = remember{mutableStateListOf<Boolean>(false, false, false, false)}
//
//    //Concentrations for slider adjusting after Equilibrium
//    val sliderConcentrations =remember{mutableStateListOf<Float>(1f, 1f, 1f, 1f)}
//
//
//    val stoichiometricCoefficients = remember{mutableStateListOf<Int>(1,1,1,1)}
//    val dataList = remember {
//        mutableStateListOf(*List(countSubstances) { List(steps){1.0} }.toTypedArray())
//    }
//
//



    var rInfinite by rememberSaveable { mutableStateOf(false) }
    var steps by rememberSaveable { mutableStateOf(300) }
    val K = rememberSaveable { mutableStateOf(1.0f) }
    val modelProducer = remember{ CartesianChartModelProducer() }


    val initConcentrations = rememberSaveable(
        saver = listSaver(
            save = { list -> list.toList() }, // convert to normal list for saving
            restore = { restored -> restored.toMutableStateList() } // restore as SnapshotStateList
        )
    ) {
        mutableStateListOf(1f, 1f, 1f, 1f)
    }

    val concentrationChange = rememberSaveable(
        saver = listSaver(
            save = { list -> list.toList() },
            restore = { restored -> restored.toMutableStateList() }
        )
    ) {
        mutableStateListOf(false, false, false, false)
    }

    val sliderConcentrations = rememberSaveable(
        saver = listSaver(
            save = { list -> list.toList() },
            restore = { restored -> restored.toMutableStateList() }
        )
    ) {
        mutableStateListOf(1f, 1f, 1f, 1f)
    }

    val stoichiometricCoefficients = rememberSaveable(
        saver = listSaver(
            save = { list -> list.toList() },
            restore = { restored -> restored.toMutableStateList() }
        )
    ) {
        mutableStateListOf(1, 1, 1, 1)
    }

    val dataList = rememberSaveable(
        saver = listSaver(
            save = { list -> list.map { it.toList() } },  // convert inner lists to normal lists
            restore = { restored -> restored.map { it.toMutableStateList() }.toMutableStateList() } // restore as SnapshotStateList of lists
        )
    ) {
        mutableStateListOf(*List(countSubstances) { List(steps) { 1.0 } }.toTypedArray())
    }






    LaunchedEffect(K.value, concentrationChange.toList(), stoichiometricCoefficients.toList()) {

        //When Inputs change, recalculation
        for(i in 0 until sliderConcentrations.size){
            initConcentrations[i] = sliderConcentrations[i]
//

        }
        rInfinite = rInfinite(initConcentrations.map { it.toDouble()}.toTypedArray().toDoubleArray(), stoichiometricCoefficients.toIntArray(), K.value)

        calculateStepsize(0.01, initConcentrations.map { it.toDouble()}.toTypedArray().toDoubleArray(), stoichiometricCoefficients.toIntArray(), K.value)
        steps = calculateSuspended( K.value, initConcentrations, stoichiometricCoefficients, dataList)

        rInfinite(initConcentrations.map { it.toDouble()}.toTypedArray().toDoubleArray(), stoichiometricCoefficients.toIntArray(), K.value)

    }

    LaunchedEffect(dataList.toList()){
        //When Data changes, recompose

        modelProducer.runTransaction {
            lineSeries {
                for(i in 0 until dataList.size){
                    if(stoichiometricCoefficients[i] > 0){
                        series(
                            (0..steps).toList(), dataList[i].toList()
                        )
                    }
                }
            }
        }





    }


    Box(modifier = Modifier.background(color = colors.BackgroundPrimary).fillMaxSize()){
        Column(
            //Box Containing everything
            modifier = Modifier.wrapContentSize().padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {

            stoichioSliders(stoichiometricCoefficients)

            //Displaying the Formula
            displayFormula(stoichiometricCoefficients)

            concSliders(concentrationChange, initConcentrations, sliderConcentrations)

            kSlider(K)

            Button(
                colors = ButtonColors(
                    contentColor = colors.TextPrimary, containerColor = colors.BackgroundSecondary,
                    disabledContainerColor = colors.SliderInactiveTrack,
                    disabledContentColor = colors.TextSecondary
                ),
                onClick = {
                    //Update the Concentrations resulting from Equilibrium, to update Sliders
                    for (i in 0 until sliderConcentrations.size) {
                        sliderConcentrations[i] =
                            log(dataList[i][dataList[0].size - 1], BASE).toFloat()
                        initConcentrations[i] = sliderConcentrations[i]
                    }
                }

            ) {
                Text("Use Equilibrium Concentrations", color = colors.TextPrimary)
            }


            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight()) { // Adjust width based on data size) {
                if (rInfinite) {

                    Text(
                        "VALUES ARE TOO LARGE/SMALL", fontSize = 48.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    makeChart(modelProducer, stoichiometricCoefficients)
                }


            }


        }
    }

}


@Composable
fun displayFormula(stoichiometricCoefficients: SnapshotStateList<Int>){

    val textSize = 30.sp
    val textWeight = FontWeight.W700

    Text(
        text = buildAnnotatedString {
            val a = stoichiometricCoefficients[0] > 0
            val b = stoichiometricCoefficients[1] > 0
            val c = stoichiometricCoefficients[2] > 0
            val d = stoichiometricCoefficients[3] > 0

            if(a){
                withStyle(style = SpanStyle(color = colors.GraphColorSet1_Light, fontStyle = FontStyle.Italic, fontWeight = textWeight, fontSize = textSize)) {
                    append("${stoichiometricCoefficients[0]} A")
                }
            }

            if(a && b) {
                withStyle(style = SpanStyle(color = colors.TextPrimary,fontStyle = FontStyle.Italic, fontWeight = textWeight, fontSize = textSize)){
                    append(" + ")
                }
            }

            if(b) {
                withStyle(
                    style = SpanStyle(
                        color = colors.GraphColorSet1_Dark,
                        fontStyle = FontStyle.Italic,
                        fontWeight = textWeight,
                        fontSize = textSize
                    )
                ) {
                    append("${stoichiometricCoefficients[1]} B")
                }
            }


            withStyle(style = SpanStyle(color = colors.TextPrimary,fontStyle = FontStyle.Italic, fontWeight = textWeight, fontSize = textSize)){
                append(" ⇌ ")
            }



            if(c) {
                withStyle(
                    style = SpanStyle(
                        color = colors.GraphColorSet2_Light,
                        fontStyle = FontStyle.Italic,
                        fontWeight = textWeight,
                        fontSize = textSize
                    )
                ) {
                    append("${stoichiometricCoefficients[2]} C")
                }
            }

            if(c && d)
                withStyle(style = SpanStyle(color = colors.TextPrimary,fontStyle = FontStyle.Italic, fontWeight = textWeight, fontSize = textSize)){
                    append(" + ")
                }


            if(d) {
                withStyle(
                    style = SpanStyle(
                        color = colors.GraphColorSet2_Dark,
                        fontStyle = FontStyle.Italic,
                        fontWeight = textWeight,
                        fontSize = textSize
                    )
                ) {
                    append("${stoichiometricCoefficients[3]} D")
                }
            }

        }
    )

}


@Composable
fun makeChart(modelProducer: CartesianChartModelProducer, stoichiometricCoefficients: SnapshotStateList<Int>){

    val l = listOfNotNull(
        stoichiometricCoefficients[0].takeIf { it > 0 }?.let {
            LineCartesianLayer.rememberLine(fill = LineFill.single(Fill(colors.GraphColorSet1_Light)))
        },
        stoichiometricCoefficients[1].takeIf { it > 0 }?.let {
            LineCartesianLayer.rememberLine(fill = LineFill.single(Fill(colors.GraphColorSet1_Dark)),
                stroke = Dashed()
            )
        },
        stoichiometricCoefficients[2].takeIf { it > 0 }?.let {
            LineCartesianLayer.rememberLine(
                fill = LineFill.single(Fill(colors.GraphColorSet2_Light))
            )
        },
        stoichiometricCoefficients[3].takeIf { it > 0 }?.let {
            LineCartesianLayer.rememberLine(
                fill = LineFill.single(Fill(colors.GraphColorSet2_Dark)),
                stroke = Dashed()
            )
        }
    )

    val cartLayer = rememberLineCartesianLayer(LineProvider.series(l))

    val yFormatter = CartesianValueFormatter.decimal(
        decimalCount = 3
    )

    val chart = rememberCartesianChart(
        cartLayer,
        startAxis = VerticalAxis.rememberStart(
            guideline = null,
            valueFormatter = yFormatter,
            title = "c in mol/L",
            titleComponent = rememberTextComponent(
                style = TextStyle(color = colors.TextPrimary)
            ),
        ),

        bottomAxis = HorizontalAxis.rememberBottom(
            label = null,
            guideline = null,
            tick = null
            ),

        legend = rememberHorizontalLegend(
            items = {
                add(
                    LegendItem(
                        labelComponent = TextComponent(
                            textStyle = TextStyle(color = colors.GraphColorSet1_Light)
                        ),
                        label = "A      ",
                        icon = ShapeComponent(fill= Fill(colors.GraphColorSet1_Light))),

                )
                add(
                    LegendItem(
                        labelComponent = TextComponent(
                            textStyle = TextStyle(color =colors.GraphColorSet1_Dark)
                        ),
                        label = "B                     ",
                        icon = ShapeComponent(fill = Fill(colors.GraphColorSet1_Dark)))
                )
                add(
                    LegendItem(
                        labelComponent = TextComponent(
                            textStyle = TextStyle(color =colors.GraphColorSet2_Light)
                        ),
                        label = "C      ",
                        icon = ShapeComponent(fill= Fill(colors.GraphColorSet2_Light)))
                )
                add(
                    LegendItem(
                        labelComponent = TextComponent(
                            textStyle = TextStyle(color = colors.GraphColorSet2_Dark)
                        ),
                        label = "D",
                        icon = ShapeComponent(fill = Fill(colors.GraphColorSet2_Dark)))
                )

            }
        )
    )

    CartesianChartHost(
        chart = chart,
        modelProducer = modelProducer,
        modifier = Modifier.fillMaxSize(),
        scrollState = rememberVicoScrollState(
            scrollEnabled = false    // ❌ no scrolling allowed
        ),
        zoomState = rememberVicoZoomState(initialZoom = Zoom.Content),

        )


}

@Composable
fun kSlider(K: MutableState<Float>) {

    var v3 by remember { mutableFloatStateOf(K.value) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "K",
            color = colors.TextPrimary,
            fontSize = regularTextSize,
            fontWeight = regularTextWeight
        )

        Box(
            modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth()
        ) {

            Slider(
                value = v3,
                onValueChange = { v -> v3 = v },
                onValueChangeFinished = { K.value = BASE.pow(v3.round(2).toDouble()).toFloat() },
                colors = SliderDefaults.colors(
                    thumbColor = colors.SliderThumbColor,
                    activeTrackColor = colors.SliderActiveTrack,
                    inactiveTrackColor = colors.SliderInactiveTrack,
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent
                ),
                valueRange = -5f..5f,
                steps = 0,

                )
            Text(
                text = BASE.pow(v3.toDouble()).round(2).toFloat().toString(),
                color = colors.TextSecondary,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.background(Color.Transparent, RoundedCornerShape(5.dp))
                    .padding(3.dp).width(50.dp).height(15.dp).align(Alignment.Center)
            )
        }

    }

}










@Composable
fun stoichioSliders(stoichiometricCoefficients:SnapshotStateList<Int>){


    Text(
        text = "Stoichiometry",
        color = colors.TextPrimary,
        fontSize = regularTextSize,
        fontWeight = regularTextWeight)
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        content = {
            items(4) { i ->

                var v2 by remember { mutableFloatStateOf(stoichiometricCoefficients[i].toFloat()) }


                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    //Text for Letter (Substance A/B/C/D)
                    Text(
                        text = "${(65 + i).toChar()}",
                        color = if (65 + i == 65) colors.GraphColorSet1_Light
                        else if (65 + i == 66) colors.GraphColorSet1_Dark
                        else if (65 + i == 67) colors.GraphColorSet2_Light
                        else colors.GraphColorSet2_Dark,

                        fontWeight = FontWeight.W700,
                        modifier = Modifier.background(
                            Color.Transparent,
                            RoundedCornerShape(5.dp)
                        )
                    )


                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {

                        //Stoicho Slider
                        Slider(
                            value = v2,
                            onValueChange = { value -> v2 = value },
                            onValueChangeFinished = {
                                stoichiometricCoefficients[i] = v2.toInt()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = SliderDefaults.colors(
                                thumbColor = colors.SliderThumbColor,
                                activeTrackColor = colors.SliderActiveTrack,
                                inactiveTrackColor = colors.SliderInactiveTrack
                            ),
                            valueRange = 0f..5f
                        )


                        //Stoicho Text
                        Text(
                            text = v2.toInt().toString(),
                            color = colors.TextSecondary,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.W700,
                            modifier = Modifier.background(
                                Color.Transparent,
                                RoundedCornerShape(5.dp)
                            )
                                .padding(3.dp).width(50.dp).height(15.dp)
                        )

                    }
                }
            }
        }
    )
}


@Composable
fun concSliders(concentrationChange: SnapshotStateList<Boolean>, initConcentrations: SnapshotStateList<Float>, sliderConcentrations: SnapshotStateList<Float>){
    Text(
        text = "Concentration",
        color = colors.TextPrimary,
        fontSize = regularTextSize,
        fontWeight = regularTextWeight
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),   // spacing between rows
        content = {
            items(4) { i ->
                val v by animateFloatAsState(targetValue = sliderConcentrations[i])

                Column {
                    //Row to hold Slider and Text
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {


                        //Text for Letter (Substance A/B/C/D)
                        Text(
                            text = "${(65 + i).toChar()}",
                            color = if (65 + i == 65) colors.GraphColorSet1_Light
                            else if (65 + i == 66) colors.GraphColorSet1_Dark
                            else if (65 + i == 67) colors.GraphColorSet2_Light
                            else colors.GraphColorSet2_Dark,
                            fontWeight = FontWeight.W700,
                            modifier = Modifier.background(
                                Color.Transparent,
                                RoundedCornerShape(5.dp)
                            )
                        )


                        //Box for Slider and displaying Number
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            //Slider for the Concentration
                            Slider(
                                value = v,
                                onValueChange = { value ->
                                    sliderConcentrations[i] = value
                                },
                                onValueChangeFinished = {

                                    //Inverting
                                    concentrationChange[i] =
                                        !concentrationChange[i]

                                },

                                modifier = Modifier.fillMaxWidth(),
                                valueRange = -9.966f..10f,
                                colors = SliderDefaults.colors(
                                    thumbColor = colors.SliderThumbColor,
                                    activeTrackColor = colors.SliderActiveTrack,
                                    inactiveTrackColor = colors.SliderInactiveTrack
                                )
                            )



                            Text(

                                text = if (sliderConcentrations[i] < -2) BASE.pow(
                                    sliderConcentrations[i].toDouble()
                                ).round(3).toString()
                                else BASE.pow(sliderConcentrations[i].toDouble()).round(2)
                                    .toString(),
                                color = colors.TextSecondary,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.W700,
                                modifier = Modifier.background(
                                    Color.Transparent,
                                    RoundedCornerShape(5.dp)
                                )
                                    .padding(3.dp)
                            )
                        }

                    }
                }
            }
        }
    )


}

fun rInfinite(concs: DoubleArray, stoichCo: IntArray, K:Float):Boolean{
    val concs = concs.map{BASE.pow(it)}
    val forward = (if(stoichCo[0] > 0) concs[0].pow(stoichCo[0].toDouble()) else 1.0)*(if(stoichCo[1] > 0)concs[1].pow(stoichCo[1].toDouble()) else 1.0)
    val backward = (if(stoichCo[2] > 0) concs[2].pow(stoichCo[2].toDouble()) else 1.0 )*(if(stoichCo[3] > 0) concs[3].pow(stoichCo[3].toDouble()) else 1.0)

    val r = K.toDouble() * forward - backward
    println("R in infinityFunction: $r")

    return r.isInfinite()
}



fun calculateStepsize(desiredError: Double, concs: DoubleArray, stoichCo: IntArray, K:Float){
    var countStoichos = 0
    for (coefficient in stoichCo){
        if(coefficient > 0) countStoichos++
    }

    val concs =
        concs.map { BASE.pow(it) }.toDoubleArray()
    val forward = (if(stoichCo[0] > 0) concs[0].pow(stoichCo[0].toDouble()) else 1.0)*(if(stoichCo[1] > 0)concs[1].pow(stoichCo[1].toDouble()) else 1.0)
    val backward = (if(stoichCo[2] > 0) concs[2].pow(stoichCo[2].toDouble()) else 1.0 )*(if(stoichCo[3] > 0) concs[3].pow(stoichCo[3].toDouble()) else 1.0)

    val initialR = K.toDouble() * forward - backward


    val slopes = DoubleArray(4) { i ->
        val factor = stoichCo[i].toDouble()
        if (i < 2) -factor * initialR else factor * initialR
    }

    val maxSlope = slopes.maxOf { abs(it) }
    stepSize = (desiredError / maxSlope).coerceIn(1e-12, 1e-2)




}



suspend fun calculateSuspended(
    K: Float,
    startConc: SnapshotStateList<Float>,
    stoichCo: SnapshotStateList<Int>,
    dataList: SnapshotStateList<List<Double>>
) : Int {
    var r = 0.0
    var done = 0
    var steps = 0
    val result = mutableListOf<List<Double>>()


        withContext(Dispatchers.Default) {

            val currentConcentrations =
                startConc.toFloatArray().map { BASE.pow(it.toDouble()) }.toDoubleArray()



            while (done < 4) {
                ensureActive()
                done = 0



                steps += 1



                result.add(currentConcentrations.toList())

                val forward = (if(stoichCo[0] > 0) currentConcentrations[0].pow(stoichCo[0].toDouble()) else 1.0)*(if(stoichCo[1] > 0)currentConcentrations[1].pow(stoichCo[1].toDouble()) else 1.0)
                val backward = (if(stoichCo[2] > 0) currentConcentrations[2].pow(stoichCo[2].toDouble()) else 1.0 )*(if(stoichCo[3] > 0) currentConcentrations[3].pow(stoichCo[3].toDouble()) else 1.0)

                r = K.toDouble() * forward - backward

                println("Step: $steps R: $r")









                for (i in currentConcentrations.indices) {
                    //Für Stoffe A/B und C/D ist die Formel mit vorzeichen Gewechselt
                        if(stoichCo[i] > 0){
                            val temp = stoichCo[i].toDouble() * r * stepSize!!
                            val newValue =
                                if (i < 2) currentConcentrations[i] - temp
                                else currentConcentrations[i] + temp

                            //Clip value above 0
                            val clippedValue = newValue.coerceAtLeast(0.0)

                            if ((stoichCo[i] > 0 && (abs((currentConcentrations[i] - clippedValue) /clippedValue  ) < 0.0001)) || steps >= 50000) {
                                done ++

                            }

                            currentConcentrations[i] = clippedValue

                        }
                        else{
                            done++
                        }




                }


//            val edukte = currentConcentrations[0].pow(stoichCo[0]) * currentConcentrations[1].pow(stoichCo[1])
//            val produkte = currentConcentrations[2].pow(stoichCo[2]) * currentConcentrations[3].pow(stoichCo[3])
//            val Q: Double = produkte/edukte
//
//            if(abs(Q - K) < 0.1){
//                done = true
//            }


            }

            //Fill the Datalist with dummy data, so graph is readable, if only few steps were made
            while(steps < 100) {
                result.add(currentConcentrations.toList())
                steps++


            }
        }

        // Update dataList on main thread
        withContext(Dispatchers.Main) {

            if(r.isFinite()){

                dataList.clear()
                dataList.addAll(transpose(result))

            }
            else{
                println("R is infinite")
            }

        }



    return steps
}

fun <T> transpose(list: List<List<T>>): List<List<T>> {
    if (list.isEmpty()) return emptyList()
    val cols = list[0].size
    return List(cols) { colIndex -> list.map { it[colIndex] } }
}

fun Float.round(decimals:Int):Float{
    return (round(this.toDouble()* 10.0.pow(decimals.toDouble()) )
            /(10.0.pow(decimals.toDouble()))).toFloat()
}


fun Double.round(decimals:Int):Double{
    return (round(this* 10.0.pow(decimals.toDouble()))
            /(10.0.pow(decimals.toDouble())))
}


