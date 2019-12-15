package academy.b3l3k4.partyMaker

import android.os.Parcel
import android.os.Parcelable

class PartyInfo(var title: String, var date: String, var description: String, var price: String, var attendants: String): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()){}


    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(date)
        dest.writeString(description)
        dest.writeString(price)
        dest.writeString(attendants)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PartyInfo> {
        override fun createFromParcel(parcel: Parcel): PartyInfo {
            return PartyInfo(parcel)
        }

        override fun newArray(size: Int): Array<PartyInfo?> {
            return arrayOfNulls(size)
        }
    }
}