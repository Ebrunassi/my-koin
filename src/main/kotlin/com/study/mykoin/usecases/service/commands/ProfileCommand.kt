package com.study.mykoin.usecases.service.commands

import com.study.mykoin.domain.fiis.MonthIncome
import com.study.mykoin.domain.fiis.profile.Password
import com.study.mykoin.domain.fiis.profile.Username

class ProfileCommand (
    val username: Username,
    val password: Password,
){
}