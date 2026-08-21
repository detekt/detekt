@file:Suppress("InvalidPackageDeclaration")

package com.example.myapplication

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Foo(val i: Int) : Parcelable
