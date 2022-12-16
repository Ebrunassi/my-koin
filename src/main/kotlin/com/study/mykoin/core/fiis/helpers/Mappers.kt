package com.study.mykoin.core.fiis.helpers

import com.google.gson.Gson
import com.study.mykoin.domain.fiis.Fii
import com.study.mykoin.domain.fiis.FiiEntry


fun String.mapToFii(): Fii = Gson().fromJson(this, Fii::class.java)
fun String.mapToFiiEntity(): FiiEntry = Gson().fromJson(this, FiiEntry::class.java)