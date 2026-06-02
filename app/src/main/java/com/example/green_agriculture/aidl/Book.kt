package com.example.green_agriculture.aidl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(val bookName: String, val bookId: Int) : Parcelable