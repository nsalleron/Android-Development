package fr.salleron.nicolas.findmycity.data

/**
 * Created by nicolassalleron on 13/01/2018.
 */
class Difficulty {
    val DEFAULT_DIFFICULTY = "lvl0"
    val LVL0 = 0
    val LVL1 = 1
    val LVL2 = 2

    public lateinit var mapData : ArrayList<City>

    constructor(lvl: Int){
        mapData = ArrayList<City>()
        when (lvl) {
            0 -> {
                mapData.add(City(48.873927, 2.296787,"Paris"))
                mapData.add(City(48.008752, 0.199925,"Le Mans"))
                mapData.add(City(48.871027, 2.303739,"Paris"))
                mapData.add(City(48.634982, -1.510264,"Mont-St-Michel"))
                mapData.add(City(55.947557, -3.200270,"Edimburgh"))
                mapData.add(City(51.501018, -0.125245,"London"))
                mapData.add(City(41.900779, 12.483305, "Rome"))
                mapData.add(City(27.173953, 78.042113,"TajMahal"))
                mapData.add(City(40.689586, -74.044493,"New York"))
            }
            1 -> {
                mapData.add(City(53.342748, -6.267119,"Dublin"))
                mapData.add(City(45.504856, -73.556777,"Montreal"))
                mapData.add(City(34.098001, -118.351504,"Hollywood"))
            }
            2 -> {
                mapData.add(City(47.996034, 0.222694,"Le Mans"))
                mapData.add(City(48.634536, -2.053195,"Dinard"))
                mapData.add(City(52.460247, -1.994817,"Quiton"))
                mapData.add(City(64.002088, -22.554517,"Reykjanesbaer"))
                mapData.add(City(37.817237, -119.512836,"Yosemite park"))
            }
        }
    }

}