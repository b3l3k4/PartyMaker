package academy.b3l3k4.partyMaker

class Notifications {
    var owner: String? = null
    var permission: String? = null
    var status: String? = null
    var eventId: String? = null
    var eventTitle: String? = null

    constructor(){}

    constructor(owner: String?, permission: String?, status: String?, eventId: String?, eventTitle: String?){
        this.owner = owner
        this.permission = permission
        this.status = status
        this.eventId = eventId
        this.eventTitle = eventTitle
    }


}