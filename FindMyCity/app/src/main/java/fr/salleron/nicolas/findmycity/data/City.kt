package fr.salleron.nicolas.findmycity.data

/**
 * Created by nicolassalleron on 14/01/2018.
 */
class City {
    public var lat : Double? = null
    public var lng : Double? = null
    public var name : String? = null

    constructor(lat:Double,lng:Double,name:String){
        this.lat = lat
        this.lng = lng
        this.name = name
    }

}