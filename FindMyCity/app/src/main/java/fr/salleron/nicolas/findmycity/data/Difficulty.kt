package fr.salleron.nicolas.findmycity.data

/**
* Created by nicolassalleron on 13/01/2018.
*/
class Difficulty(lvl: Int) {

    var mapData : ArrayList<City> = ArrayList()

    init {
        when (lvl) {
            0 -> {
                mapData.add(City(48.873927, 2.296787,"Paris",
                        "France","c'est là ou se trouve une dame de fer",
                        "Population de 2,244 millions d'habitants"))
                mapData.add(City(41.900779, 12.483305, "Rome","Italie",
                        "je suis une capitale","La fontaine de Trévi"))
                mapData.add(City(27.173953, 78.042113,"Agra","Inde",
                        "ouahhh, une merveille du monde","C'était le Taj Mahal"))
                mapData.add(City(40.689586, -74.044493,"New York",
                        "États-Unis", "Big Apple","La statue de la liberté"))
                mapData.add(City(51.501018, -0.125245,"London",
                        "Royaume-Uni", "capitale au nord",
                        "La grande tour, c'était Big Ben"))
            }
            1 -> {
                mapData.add(City(48.008752, 0.199925,"Le Mans",
                        "France", "24h, et c'est pas la série",
                        "C'était la Cathédrale Saint Julien-Le Mans"))
                mapData.add(City(55.947557, -3.200270,"Edimburgh",
                        "Royaume-Uni", "le siège d'Arthur",
                        "La batisse était le Château d'Édimbourg"))
                mapData.add(City(45.504856, -73.556777,"Montreal",
                        "Canada", "miam, la poutine",
                        "Basilique Notre-Dame de Montréal"))
                mapData.add(City(53.342748, -6.267119,"Dublin",
                        "Irlande", "de la bonne bière ! ",
                        "Château de Dublin"))
                mapData.add(City(48.634982, -1.510264,"Mont-St-Michel",
                        "France", "perdu sur un îlot rocheux",
                        "44 habitants seulement !"))
            }
            2 -> {
                mapData.add(City(47.996034, 0.222694,"Le Mans","France",
                        ":)","On était à coté de l'intermarché !"))
                mapData.add(City(64.002088, -22.554517,"Reykjanesbaer",
                        "Islande","bon courage !",
                        "Prochaine visite!"))
                mapData.add(City(52.460247, -1.994817,"Quiton",
                        "Royaume-Uni","bon courage !",
                        "Il fallait quelque chose de difficile"))
                mapData.add(City(48.634536, -2.053195,"Dinard","France",
                        "nan toujours pas d'indices","La plage est sympa"))
                mapData.add(City(37.817237, -119.512836,"Yosemite park",
                        "États-Unis","Système Apple, bon courage !","Yosemite -> Sierra"))

            }
        }
    }


}