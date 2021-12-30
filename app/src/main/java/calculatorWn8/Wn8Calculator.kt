package calculatorWn8

import java.io.Serializable
import java.lang.Double.max
import java.lang.Double.min


class Wn8Calculator: Serializable {
    private lateinit var listOfExpValue: List<Wn8ExpValue>

    constructor(list:List<Wn8ExpValue>){
        listOfExpValue = list
    }


    fun calulateWN8byTank(tankid:Int,battles:Int,win:Double,def:Double,frags:Double,dmg:Double,spot:Double):Double {
        if(battles == 0) return 0.0
        for (v in listOfExpValue) {
            if (v.IDNum.equals(tankid)) {
                var rWin = (win/battles)*100 / v.expWinRate
                var rDmg = (dmg/battles) / v.expDamage
                var rAssist = (spot/battles) / v.expSpot
                var rDef = (def/battles) / v.expDef
                var rFrags = (frags/battles) / v.expFrag
                var rWinc = max(0.0, (rWin - 0.71) / (1.0 - 0.71))
                var rDmgc = max(0.0, (rDmg - 0.22) / (1.0 - 0.22))
                var rFragsc = max(0.0, min(rDmgc + 0.2, (rFrags - 0.12) / (1.0 - 0.12)))
                var rAssistc = max(0.0, min(rDmgc + 0.1, (rAssist - 0.38) / (1.0 - 0.38)))
                var rDEFc = max(0.0, min(rDmgc + 0.1, (rDef - 0.10) / (1.0 - 0.10)))
                var WN8 =
                    980 * rDmgc + 210 * rDmgc * rFragsc + 155 * rFragsc* rAssistc  + 75 * rDEFc * rFragsc + 145 * min(
                        1.8,
                        rWinc
                    )
                return WN8
            }
        }
        return 0.0
    }


}