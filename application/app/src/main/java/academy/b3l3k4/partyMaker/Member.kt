package academy.b3l3k4.partyMaker

class Member {
    var permission: String? = null
    var uid: String? = null
    var userFullName: String? = null
    var adminUID: String? = null

    constructor(){}

    constructor(permission: String?, uid: String?, userFullName: String?, adminUID: String?){
        this.permission = permission
        this.uid = uid
        this.userFullName = userFullName
        this.adminUID = adminUID
    }

    override fun toString(): String {
        return "$permission,$uid,$userFullName,$adminUID"
    }
}