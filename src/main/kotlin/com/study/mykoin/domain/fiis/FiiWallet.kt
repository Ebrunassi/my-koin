package com.study.mykoin.domain.fiis

import com.study.mykoin.core.crawler.model.LastIncome
import com.study.mykoin.core.crawler.model.NextIncome
import com.study.mykoin.core.fiis.model.enums.FiiTypeEnum
import com.study.mykoin.core.fiis.model.enums.ResourceTypeEnum
import com.study.mykoin.core.fiis.model.enums.WhenPaymentEnum
import kotlinx.serialization.Serializable
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * This domain is used in FiiWallet collection.
 * It holds all the information regarding the Fii that I have in my wallet
 */
@Document(collection = "fiis-wallet")
@Serializable
class Fii(
    @Id
    var id: Long?,
    val name: String,
    val userId: Long,
    val type: FiiTypeEnum,
    var quantity: Int,
    var totalInvested: Double,
    var averagePrice: Double,
    var actualMonth: MonthInformation = MonthInformation(),
    var nextMonth: MonthInformation = MonthInformation(),
    val resourceType: ResourceTypeEnum?,
    // Regarding only to this fii
    var lastIncome: LastIncome = LastIncome(),
    var nextIncome: NextIncome = NextIncome(),
    var porcent: Double
) {
    companion object {
        @Transient
        const val SEQUENCE_NAME = "fiis-wallet-sequence"
    }
}

fun Fii.updateFii(updatedFii: FiiEntry) {
    when (whenPayment(updatedFii.transactionDate)) {
        WhenPaymentEnum.THIS_MONTH -> {
            actualMonth.quantity += updatedFii.quantity
            actualMonth.totalInvested += updatedFii.totalInvested
            actualMonth.monthlyIncome = actualMonth.quantity * (this.thisMonthIncome() ?: 0.0)

            // Also updates the next month values
            nextMonth.quantity += updatedFii.quantity
            nextMonth.totalInvested += updatedFii.totalInvested
            nextMonth.monthlyIncome = nextMonth.quantity * (this.nextMonthIncome() ?: 0.0)

            quantity += updatedFii.quantity
            totalInvested += updatedFii.totalInvested
            averagePrice = totalInvested / quantity
        }
        WhenPaymentEnum.NEXT_MONTH -> {
            nextMonth.quantity += updatedFii.quantity
            nextMonth.totalInvested += updatedFii.totalInvested
            nextMonth.monthlyIncome = nextMonth.quantity * (this.nextMonthIncome() ?: 0.0)

            quantity += updatedFii.quantity
            totalInvested += updatedFii.totalInvested
            averagePrice = totalInvested / quantity
        }
        WhenPaymentEnum.UNDEFINED -> {
            nextMonth.quantity += updatedFii.quantity
            nextMonth.totalInvested += updatedFii.totalInvested
            nextMonth.monthlyIncome += nextMonth.quantity * (this.nextMonthIncome() ?: 0.0)

            quantity = nextMonth.quantity
            totalInvested = nextMonth.totalInvested
            averagePrice = totalInvested / quantity
        }
    }

}

fun Fii.thisMonthIncome(): Double? {
    val pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val lastIncome = with(this.lastIncome.payDay) { if (this?.contains("-") == false) this else return null }
    val nextIncome = with(this.nextIncome.payDay) { if (this?.contains("-") == false) this else return null }

    val lastIncomeDate = lastIncome.let { LocalDate.parse(it, pattern) }
    val nextIncomeDate = nextIncome.let { LocalDate.parse(it, pattern) }
    val actualDate = LocalDate.now()

    return if (lastIncomeDate?.monthValue == actualDate.monthValue) this.lastIncome.value
    else if (nextIncomeDate?.monthValue == actualDate.monthValue) this.nextIncome.value
    else null
}

fun Fii.nextMonthIncome(): Double? {
    val pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val lastIncome = with(this.lastIncome.payDay) { if (this?.contains("-") == false) this else return null }
    val nextIncome = with(this.nextIncome.payDay) { if (this?.contains("-") == false) this else return null }

    val lastIncomeDate = lastIncome.let { LocalDate.parse(it, pattern) }
    val nextIncomeDate = nextIncome.let { LocalDate.parse(it, pattern) }
    val actualDate = LocalDate.now()

    return if (lastIncomeDate?.monthValue!! > actualDate.monthValue) this.lastIncome.value
    else if (nextIncomeDate?.monthValue!! > actualDate.monthValue) this.nextIncome.value
    else null
}

fun Fii.whenPayment(transactionDate: String? = null): WhenPaymentEnum {
    return try {
        val pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val lastIncomePayDay = with(this.lastIncome.payDay) { if (this?.contains("-") == false) this else null }
        val lastIncomeBaseDay = with(this.lastIncome.baseDay) { if (this?.contains("-") == false) this else null }
        val nextIncomePayDay = with(this.nextIncome.payDay) { if (this?.contains("-") == false) this else null }
        val nextIncomeBaseDay = with(this.nextIncome.baseDay) { if (this?.contains("-") == false) this else null }

        val lastIncomePayDate = lastIncomePayDay?.let { LocalDate.parse(it, pattern) }
        val lastIncomeBaseDate = lastIncomeBaseDay?.let { LocalDate.parse(it, pattern) }
        val nextIncomePayDate = nextIncomePayDay?.let { LocalDate.parse(it, pattern) }
        val nextIncomeBaseDate = nextIncomeBaseDay?.let { LocalDate.parse(it, pattern) }

        val now = LocalDate.now()
        val referenceDate = transactionDate?.let { LocalDate.parse(it, pattern) } ?: LocalDate.now()


        if (lastIncomeBaseDay == null && nextIncomeBaseDay == null) return WhenPaymentEnum.UNDEFINED        // Don't have this information yet

        return if (referenceDate.isBefore(lastIncomeBaseDate)) {
            WhenPaymentEnum.THIS_MONTH
        } else if (nextIncomeBaseDate != null && referenceDate.isBefore(nextIncomeBaseDate)) {
            if (nextIncomePayDate?.monthValue == now.monthValue) WhenPaymentEnum.THIS_MONTH
            else WhenPaymentEnum.NEXT_MONTH
        } else WhenPaymentEnum.NEXT_MONTH
    } catch (e: Exception) {
        WhenPaymentEnum.UNDEFINED
    }
}
