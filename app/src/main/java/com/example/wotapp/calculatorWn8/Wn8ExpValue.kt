package com.example.wotapp.calculatorWn8

import java.io.Serializable

data class Wn8ExpValue(
    val IDNum: Int,
    val expDamage: Double,
    val expDef: Double,
    val expFrag: Double,
    val expSpot: Double,
    val expWinRate: Double
):Serializable