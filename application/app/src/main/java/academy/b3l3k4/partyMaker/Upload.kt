package academy.b3l3k4.partyMaker

class Upload {
    var name: String? = null
    var imageUrl: String? = null

    constructor() { //empty constructor needed
    }

    constructor(name: String?, imageUrl: String?) {
        this.name = name
        this.imageUrl = imageUrl
    }

}