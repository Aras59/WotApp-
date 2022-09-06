package com.example.wotapp.calculatorWn8

import java.io.Serializable
import java.lang.Double.max
import java.lang.Double.min
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Collectors.toList


class Wn8Calculator(list: List<Wn8ExpValue>) : Serializable {
    private var listOfExpValue: List<Wn8ExpValue> = list


    fun calculateWN8byTank(tankID:Int, battles:Int, win:Double, def:Double, frags:Double, dmg:Double, spot:Double):Double {
        if(battles == 0) return 0.0
        val list = listOfExpValue.stream()
            .filter { i -> i.IDNum == tankID }
            .collect(Collectors.toList())
        for (v in list) {
            val rWin = (win / battles) * 100 / v.expWinRate
            val rDmg = (dmg / battles) / v.expDamage
            val rAssist = (spot / battles) / v.expSpot
            val rDef = (def / battles) / v.expDef
            val rFrags = (frags / battles) / v.expFrag
            val rWinC = max(0.0, (rWin - 0.71) / (1.0 - 0.71))
            val rDmgC = max(0.0, (rDmg - 0.22) / (1.0 - 0.22))
            val rFragsC = max(0.0, min(rDmgC + 0.2, (rFrags - 0.12) / (1.0 - 0.12)))
            val rAssistC = max(0.0, min(rDmgC + 0.1, (rAssist - 0.38) / (1.0 - 0.38)))
            val rDEFc = max(0.0, min(rDmgC + 0.1, (rDef - 0.10) / (1.0 - 0.10)))

            return 980 * rDmgC + 210 * rDmgC * rFragsC + 155 * rFragsC * rAssistC + 75 * rDEFc * rFragsC + 145 * min(
                1.8,
                rWinC
            )
        }
        return 0.0
    }
    
}