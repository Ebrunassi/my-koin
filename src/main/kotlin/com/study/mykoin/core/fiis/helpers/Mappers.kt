package com.study.mykoin.core.fiis.helpers

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.google.gson.Gson
import com.study.mykoin.core.common.errors.ServiceErrors
import com.study.mykoin.domain.fiis.Fii
import com.study.mykoin.domain.fiis.FiiEntry
import com.study.mykoin.domain.fiis.Profile

fun String.mapToFii(): Fii = Gson().fromJson(this, Fii::class.java)
fun String.mapToFiiEntry(): Either<ServiceErrors, FiiEntry> =
    try {
        Gson().fromJson(this, FiiEntry::class.java).right()
    } catch (e: Exception) {
        ServiceErrors.BadRequest(e.message ?: "An error happened when mapping the request body").left()
    }
fun String.mapToProfileEntity(): Profile = Gson().fromJson(this, Profile::class.java)