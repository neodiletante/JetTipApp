package com.example.jettipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jettipapp.components.InputField
import com.example.jettipapp.ui.theme.JetTipAppTheme
import com.example.jettipapp.util.calculateTotalPerPerson
import com.example.jettipapp.util.calculateTotalTip
import com.example.jettipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainContent()
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit){
    JetTipAppTheme {
        Surface(color = MaterialTheme.colorScheme.background){
            content()
        }
    }
}


@Composable
fun TopHeader(totalPerPerson: Double = 134.0){
    Surface(modifier = Modifier.fillMaxWidth()
        .height(150.dp)
        .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
        //.clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))))
    ){
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(text = "Total per person",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(text = "$$total",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContent(){
    val peopleNumber = remember {
        mutableIntStateOf(1)
    }
    val range = IntRange(start = 1, endInclusive = 100)
    val tipAmountState = remember {
        mutableDoubleStateOf(0.0)
    }
    val totalPerPersonState = remember {
        mutableDoubleStateOf(0.0)
    }
    BillForm(
        //range = range,
        splitByState = peopleNumber,
        tipAmountState = tipAmountState,
        totalPerPerson = totalPerPersonState
    ){

    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetTipAppTheme {
        MyApp {
            Text("Hello")
        }
    }
}

@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range : IntRange = 1..100,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPerson: MutableState<Double>,
    onValChange: (String) -> Unit = {}
){
    val totalBillState = remember {
        mutableStateOf("")
    }

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val sliderPositionState = remember {
        mutableFloatStateOf(0f)
    }

    val tipPercentage = (sliderPositionState.floatValue * 100).toInt()

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = modifier.padding(20.dp)) {


        TopHeader(totalPerPerson = totalPerPerson.value)

        Surface(
            modifier = modifier.padding(2.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(
                corner = CornerSize(8.dp)
            ),
            border = BorderStroke(width = 2.dp, color = Color.LightGray)
        ) {
            Column(
                modifier = Modifier.padding(6.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {

                InputField(
                    valueState = totalBillState,
                    enabled = true,
                    isSingleLine = true,
                    labelId = "Enter bill",
                    onAction = KeyboardActions {
                        if (!validState) return@KeyboardActions
                        onValChange(totalBillState.value.trim())
                        keyboardController?.hide()
                    }
                )
           if (validState){
                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Split",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                Log.d("FLUX", "btn Restar")
                                if (splitByState.value > 1) {
                                    splitByState.value--
                                    totalPerPerson.value = calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.value ,
                                        tipPercentage = tipPercentage)
                                }
                            }
                        )
                        Text(
                            text = splitByState.value .toString(), modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )
                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                Log.d("FLUX", "btn Sumar")
                                if (splitByState.value  < range.last){
                                    splitByState.value ++
                                    totalPerPerson.value = calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.value ,
                                        tipPercentage = tipPercentage)
                                }

                            }
                        )
                    }

                }
                // Tip Row
                Row(modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp)) {
                    Text(
                        text = "Tip",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically),
                    )
                    Spacer(modifier = Modifier.width(200.dp))
                    Text(
                        text = "$${tipAmountState.value}",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically),
                    )
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "$tipPercentage %")
                    Spacer(modifier = Modifier.height(14.dp))
                    Slider(value = sliderPositionState.floatValue,
                        onValueChange = { newVal ->
                            sliderPositionState.floatValue = newVal
                            tipAmountState.value =
                                calculateTotalTip(totalBill = totalBillState.value.toDouble(),
                                   tipPercentage =  tipPercentage)
                            totalPerPerson.value = calculateTotalPerPerson(
                                totalBill = totalBillState.value.toDouble(),
                                splitBy = splitByState.value ,
                                tipPercentage = tipPercentage)

                            Log.d("FLUX", "Bill Form: $newVal")

                        },
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                       // steps = 19,
                        onValueChangeFinished = {
                            Log.d("FLUX", "BiillForm: Finished...")
                            tipAmountState.value =
                                calculateTotalTip(totalBill = totalBillState.value.toDouble(),
                                    tipPercentage =  tipPercentage)
                            totalPerPerson.value = calculateTotalPerPerson(
                                totalBill = totalBillState.value.toDouble(),
                                splitBy = splitByState.value ,
                                tipPercentage = tipPercentage)
                        }
                    )
                }

            }else{
                Box(){}
           }
            }
        }
    }
}

