package com.example.weatherapp.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable


@Entity
data class Location(

    @ColumnInfo(name = "city")
    var city: String,
    @ColumnInfo(name = "country")
    var country: String
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var Id: Int = 0

    constructor(source: Parcel) : this(
        city = source.readString()!!,
        country = source.readString()!!
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(city)
        dest.writeString(country)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Location> {
        override fun createFromParcel(parcel: Parcel): Location = Location(parcel)
        override fun newArray(size: Int): Array<Location?> = arrayOfNulls(size)
    }
}
