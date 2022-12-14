package com.study.mykoin.core.fiis.helpers

import com.google.gson.Gson
import com.study.mykoin.domain.fiis.Fii
import com.study.mykoin.domain.fiis.FiiEntry
import com.study.mykoin.domain.fiis.Profile


fun String.mapToFii(): Fii = Gson().fromJson(this, Fii::class.java)
fun String.mapToFiiEntry(): FiiEntry = Gson().fromJson(this, FiiEntry::class.java)
fun String.mapToProfileEntity(): Profile = Gson().fromJson(this, Profile::class.java)