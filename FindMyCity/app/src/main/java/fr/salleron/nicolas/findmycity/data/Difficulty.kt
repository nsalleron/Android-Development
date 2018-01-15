package fr.salleron.nicolas.findmycity.data

/**
* Created by nicolassalleron on 13/01/2018.
*/
class Difficulty(lvl: Int) {

    var mapData : ArrayList<City> = ArrayList()

    init {
        when (lvl) {
            0 -> {
                mapData.add(City(48.873927, 2.296787,"Paris","\"Dame de fer\""))
                mapData.add(City(48.008752, 0.199925,"Le Mans","\"24h\""))
                mapData.add(City(48.871027, 2.303739,"Paris","\"Dame de fer\""))
                mapData.add(City(48.634982, -1.510264,"Mont-St-Michel","\"îlot rocheux\""))
                mapData.add(City(55.947557, -3.200270,"Edimburgh","\"Arthur's Seat\""))
                mapData.add(City(51.501018, -0.125245,"London","\"Capitale\""))
                mapData.add(City(41.900779, 12.483305, "Rome","\"Capitale\""))
                mapData.add(City(27.173953, 78.042113,"TajMahal", "\"Merveille du monde\""))
                mapData.add(City(40.689586, -74.044493,"New York","\"Apple\""))
            }
            1 -> {
                mapData.add(City(53.342748, -6.267119,"Dublin","\"Guinness\""))
                mapData.add(City(45.504856, -73.556777,"Montreal","\"Notre-Dame\""))
                mapData.add(City(34.098001, -118.351504,"Hollywood","\"Movies\""))
            }
            2 -> {
                mapData.add(City(47.996034, 0.222694,"Le Mans","\"Pas d'astuce en difficile :)\""))
                mapData.add(City(48.634536, -2.053195,"Dinard","\"Nan toujours pas\""))
                mapData.add(City(52.460247, -1.994817,"Quiton","\"Bon courage !\""))
                mapData.add(City(64.002088, -22.554517,"Reykjanesbaer","\"Trouve mon nom\""))
                mapData.add(City(37.817237, -119.512836,"Yosemite park","\"Fond d'écran Apple\'"))
            }
        }
    }

}