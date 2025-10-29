package com.trabajo.minitienda.data.model

import java.text.SimpleDateFormat
import java.util.*

data class SaleBrief(
    val id: Long,
    val total: Double,
    val fecha: Long
) {
    val fechaString: String
        get() {
            if (fecha == 0L) return "-"
            val df = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            return df.format(Date(fecha))
        }
}
