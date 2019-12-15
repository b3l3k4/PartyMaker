package academy.b3l3k4.partyMaker

class Party{
    var title: String? = null
    var date: String? = null
    var description: String? = null
    var approximatePrice: String? = null
    var attendants: String? = null
    var price: String? = null
    var image_url: String? = null
    var name: String? = null
    var advert_url: String? = null
    var max_capacity: String? = null
    var id: String? = null
    var category: String? = null

    constructor(){}

    constructor(title: String?, date: String?, description: String?, approximatePrice: String?, attendants: String?, price: String?,
                name: String?, id: String?, image_url: String?,advert_url: String?,max_capacity: String?, category: String?){
        this.title = title
        this.date = date
        this.description = description
        this.approximatePrice = approximatePrice
        this.attendants = attendants
        this.price = price
        this.image_url = image_url
        this.name = name
        this.advert_url = advert_url
        this.max_capacity = max_capacity
        this.id = id
        this.category = category
    }

    override fun toString(): String {
        return "$name,$price,$max_capacity,$advert_url,$image_url,$id"

    }
}