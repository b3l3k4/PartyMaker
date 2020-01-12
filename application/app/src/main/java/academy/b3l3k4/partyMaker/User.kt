package academy.b3l3k4.partyMaker

class User{
    var email: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var password: String? = null
    var description: String? = null
    var uid: String? = null

    constructor(){}

    constructor(email: String?, firstName: String?, lastName: String?, password: String?, description: String?, uid: String?){
        this.email = email
        this.firstName = firstName
        this.lastName = lastName
        this.password = password
        this.description = description
        this.uid = uid
    }

    override fun toString(): String {
        return "$email,$firstName,$lastName,$uid"
    }
}