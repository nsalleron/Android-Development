package fr.salleron.nicolas.findmycity.data

/**
 * Created by nicolassalleron on 14/01/2018.
 * Juste une classe pour ranger des choses
 */
class City(lat: Double, lng: Double, name: String, var country: String, help : String, var snippet: String) {
    var lat : Double? = lat
    var lng : Double? = lng
    var name : String? = name
    var help : String? = help

}