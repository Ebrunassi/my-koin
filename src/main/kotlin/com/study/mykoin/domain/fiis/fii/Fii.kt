package com.study.mykoin.domain.fiis.fii

import com.study.mykoin.domain.fiis.FiiEntry
import com.study.mykoin.domain.fiis.MonthInformation
import com.study.mykoin.domain.fiis.enums.ResourceTypeEnum
import com.study.mykoin.domain.fiis.enums.WhenPaymentEnum
import com.study.mykoin.usecases.service.commands.FiiCommand
import kotlinx.serialization.Serializable
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * It holds all the information regarding to a FII
 */
@Document(collection = "fii")
@Serializable
class Fii(
    @Id
    val id: String,
    val name: String,
    val type: FiiTypeEnum,
    var actualMonth: MonthInformation = MonthInformation(),
    var nextMonth: MonthInformation = MonthInformation(),
    val resourceType: ResourceTypeEnum? = null,
    var lastIncome: LastIncome = LastIncome(),
    var nextIncome: NextIncome = NextIncome(),

    ) {
    companion object {
        @Transient
        const val SEQUENCE_NAME = "fiis-wallet-sequence"

        fun create(fiiCommand: FiiCommand) =
            Fii(
                id = FiiId.new().stringValue(),
                name = fiiCommand.name.stringValue(),
                type = fiiCommand.type,
            )
        }
    }

/*
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
        WhenPaymentEnum.NEXT_MONTH, WhenPaymentEnum.UNDEFINED -> {
            nextMonth.quantity = updatedFii.quantity + quantity       // We sum the ammount of FII that was bought with the ammount we already have
            nextMonth.totalInvested = updatedFii.totalInvested + totalInvested
            nextMonth.monthlyIncome = nextMonth.quantity * (this.nextMonthIncome() ?: 0.0)

            quantity += updatedFii.quantity
            totalInvested += updatedFii.totalInvested
            averagePrice = totalInvested / quantity
        }
        /*WhenPaymentEnum.UNDEFINED -> {
            nextMonth.quantity = updatedFii.quantity + quantity
            nextMonth.totalInvested = updatedFii.totalInvested + totalInvested
            nextMonth.monthlyIncome = nextMonth.quantity * (this.nextMonthIncome() ?: 0.0)

            quantity = nextMonth.quantity
            totalInvested = nextMonth.totalInvested
            averagePrice = totalInvested / quantity
        }*/
    }

}

fun Fii.createFii(updatedFii: FiiEntry) {
    when (whenPayment(updatedFii.transactionDate)) {
        WhenPaymentEnum.THIS_MONTH -> {
            actualMonth.quantity = updatedFii.quantity
            actualMonth.totalInvested = updatedFii.totalInvested
            actualMonth.monthlyIncome = actualMonth.quantity * (this.thisMonthIncome() ?: 0.0)

            // Also updates the next month values
            nextMonth.quantity = updatedFii.quantity
            nextMonth.totalInvested += updatedFii.totalInvested
            nextMonth.monthlyIncome = nextMonth.quantity * (this.nextMonthIncome() ?: 0.0)

            quantity = updatedFii.quantity
            totalInvested = updatedFii.totalInvested
            averagePrice = totalInvested / quantity
        }
        WhenPaymentEnum.NEXT_MONTH, WhenPaymentEnum.UNDEFINED -> {
            nextMonth.quantity = updatedFii.quantity
            nextMonth.totalInvested = updatedFii.totalInvested
            nextMonth.monthlyIncome = nextMonth.quantity * (this.nextMonthIncome() ?: 0.0)

            quantity = updatedFii.quantity
            totalInvested = updatedFii.totalInvested
            averagePrice = totalInvested / quantity
        }
    }

}

/**
 * Calculate the income of this month (actualMonth)
 * by last income date and next income date found by krawler
 */
fun Fii.thisMonthIncome(): Double? {
    val pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val lastIncome = with(this.lastIncome.payDay) { if (this?.contains("-") == false) this else null }
    val nextIncome = with(this.nextIncome.payDay) { if (this?.contains("-") == false) this else null }

    val lastIncomeDate = lastIncome?.let { LocalDate.parse(it, pattern) }        // 15/04/2023
    val nextIncomeDate = nextIncome?.let { LocalDate.parse(it, pattern) }        // 15/05/2023
    val actualDate = LocalDate.now()                                            // 11/04/2023

    return if (lastIncomeDate?.monthValue == actualDate.monthValue) this.lastIncome.value
    else if (nextIncomeDate?.monthValue == actualDate.monthValue) this.nextIncome.value
    else null
}

fun Fii.nextMonthIncome(): Double? {
    val pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val lastIncome = with(this.lastIncome.payDay) { if (this?.contains("-") == false) this else null }
    val nextIncome = with(this.nextIncome.payDay) { if (this?.contains("-") == false) this else null }

    val lastIncomeDate = lastIncome?.let { LocalDate.parse(it, pattern) }
    val nextIncomeDate = nextIncome?.let { LocalDate.parse(it, pattern) }
    val actualDate = LocalDate.now()

    return if (lastIncomeDate?.monthValue != null && lastIncomeDate.monthValue > actualDate.monthValue) this.lastIncome.value
    else if (nextIncomeDate?.monthValue != null && nextIncomeDate.monthValue > actualDate.monthValue) this.nextIncome.value
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

    */
