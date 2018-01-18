package fr.salleron.nicolas.findmycity.data

import android.util.Log

/**
* Created by nicolassalleron on 13/01/2018.
*/
class Difficulty(lvl: Int) {

    var mapData : ArrayList<City> = ArrayList()

    init {
        when (lvl) {
            0 -> {
                mapData.add(City(48.873927, 2.296787,"Paris","France","c'est une dame de fer"))
                mapData.add(City(41.900779, 12.483305, "Rome","Italie","je suis une capitale"))
                mapData.add(City(27.173953, 78.042113,"TajMahal","Inde", "ouahhh, une merveille du monde"))
                mapData.add(City(40.689586, -74.044493,"New York","États-Unis","big Apple"))
                mapData.add(City(51.501018, -0.125245,"London","Royaume-Uni","capitale au nord"))
            }
            1 -> {
                mapData.add(City(48.008752, 0.199925,"Le Mans","France","24h, et c'est pas la série"))
                mapData.add(City(55.947557, -3.200270,"Edimburgh","Royaume-Uni", "le siège d'Arthur"))
                mapData.add(City(45.504856, -73.556777,"Montreal","Canada","miam, la poutine"))
                mapData.add(City(53.342748, -6.267119,"Dublin","Irlande","de la bonne bière ! "))
                mapData.add(City(48.634982, -1.510264,"Mont-St-Michel","France","perdu sur un îlot rocheux"))
            }
            2 -> {
                mapData.add(City(47.996034, 0.222694,"Le Mans","France",":)"))
                mapData.add(City(64.002088, -22.554517,"Reykjanesbaer","Islande","bon courage !"))
                mapData.add(City(52.460247, -1.994817,"Quiton","Royaume-Uni","bon courage !"))
                mapData.add(City(48.634536, -2.053195,"Dinard","France","nan toujours pas d'indices"))
                mapData.add(City(37.817237, -119.512836,"Yosemite park","États-Unis","fond d'écran Apple, bon courage !"))

            }
        }

        Log.e("ClassDifficulty",mapData.toString())
    }


}