package com.study.mykoin.core.fiis.schedule

import com.study.mykoin.core.fiis.service.actualTimeWindow
import com.study.mykoin.core.fiis.storage.FiiWalletStorage
import com.study.mykoin.core.fiis.storage.ProfileStorage
import com.study.mykoin.domain.fiis.MonthIncome
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableScheduling
class Schedule {

    private val logger = LoggerFactory.getLogger(Scheduled::class.java)
    @Autowired
    private lateinit var profileStorage: ProfileStorage
    @Autowired
    private lateinit var fiiWalletStorage: FiiWalletStorage

    /**
     * Responsible to update the list 'monthIncome' from Profile.
     * Every new month it will create a new object containing the fii's income information of the month
     */
    @Scheduled(cron = "0 * * * * *") // every minute
    fun updateMonthIncomeFromProfile() {
        profileStorage.listAll().map {
            it.forEach { profile ->
                profile.monthIncome.filter { it.timeWindow == actualTimeWindow() }
                    .let {
                        if (it.isEmpty()) {
                            profileStorage.createMonthIncome(profile.id!!, MonthIncome(timeWindow = actualTimeWindow()))
                                .also { logger.info("[SCHEDULE] Added information regarding ${actualTimeWindow()} to profile '${profile.username}'") }
                        }
                    }
            }
        }
    }
}
