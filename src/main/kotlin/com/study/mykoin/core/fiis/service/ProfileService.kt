package com.study.mykoin.core.fiis.service

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.study.mykoin.core.common.errors.ServiceErrors
import com.study.mykoin.core.fiis.helpers.mapToProfileEntity
import com.study.mykoin.core.fiis.storage.FiiWalletStorage
import com.study.mykoin.core.fiis.storage.ProfileStorage
import com.study.mykoin.domain.fiis.Fii
import com.study.mykoin.domain.fiis.MonthIncome
import com.study.mykoin.domain.fiis.Profile
import com.study.mykoin.domain.fiis.thisMonthIncome
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class ProfileService : ConsumerHandler {

    private val logger = LoggerFactory.getLogger(FiiWalletService::class.java)
    @Autowired
    private lateinit var profileStorage: ProfileStorage
    @Autowired
    private lateinit var fiiWalletStorage: FiiWalletStorage

    /**
     * key = userId
     * value = extracted information (ProfileDTO)
     * Updates 'monthlIncome' and 'totalInvested'
     */
    override fun handler(key: String, record: String) {

        try {
            logger.info("Received new message in profile service: $record")
            val profileEntity = record.mapToProfileEntity()
            profileStorage.save(profileEntity)
                .map { logger.info("[PROFILE-STORAGE] Updated database with '${it.username}' informations") } // The usage of Either here is only to learn and practice, it is not necessary
            // .fold(
            //    ifLeft = { logger.error("Some problem happened when saving in database: $it") },
            //    ifRight = { logger.info("[PROFILE-STORAGE] Updated database") }
            // )
        } catch (e: Exception) {
            ServiceErrors.HandlerError("${e.message} - ${e.localizedMessage}").left()
        }
    }

    /**
     * IMPORTANT: Review this entire flow
     * Once I got here, it's implicit that {fii} got modified with new values, so we need to update monthlyIncome from profile
     */
    fun updatedFiiInformationHandler(fii: Fii) {
        try {
            fii.thisMonthIncome()?.let {
                val profiles = profileStorage.findById(fii.userId)     // find profile who owns this fii to update it's information
                profiles
            }?.map {
                updateMonthIncome(it)
            }

        } catch (e : Exception){
            e.printStackTrace()
        }
    }

    /**
     * For now, this method is called sync. If there are other methods which triggers this function, lets make it
     * async. I've already created the domain event for that.
     */
    private fun updateMonthIncome(profile: Profile): Either<ServiceErrors, Long> {

        var count = 0
        var totalInvested = 0.0
        val monthIncomeValue = profile.fiiWallet
            .map {
                fiiWalletStorage.findById(it).also { totalInvested += it?.totalInvested ?: 0.0}       // Get the Fiis from database
            }.filter {
                it?.thisMonthIncome() != null       // Filter out those which doesn't have information for the current month
            }.also { count = it.size }
            .map {
                (it?.thisMonthIncome()?.times(it.quantity)) ?: 0.0
            }.reduce {
                sum, element -> sum + element
            }

        val actualDate = actualTimeWindow()

        val newMonthIncome = profile.monthIncome.filter { it.timeWindow != actualDate }.toMutableSet()
        newMonthIncome.add(
            MonthIncome(
                timeWindow = actualDate,
                value = monthIncomeValue,
                yield = (monthIncomeValue / totalInvested) * 100,
                completed = count == profile.fiiWallet.size
            )
        )
        return profileStorage.updateMonthIncome(profile.id!!, newMonthIncome)
    }
}

fun actualTimeWindow() = with(LocalDate.now()) { "${this.monthValue}/${this.year}" }
