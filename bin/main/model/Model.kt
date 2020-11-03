package org.simonsbacka.model

import java.time.LocalDateTime
import java.util.*

data class Monitorering(val _id: String? = null,
                        val systemId: String,
                        val name: String,
                        val description: String,
                        val createdBy: String,
                        val createdDate: LocalDateTime = LocalDateTime.now())

data class Monitoreringstillfalle(val _id: String? = null,
                                  val monitoreringId: String,
                                  val sjukhusId: String,
                                  val userReference: String,
                                  val fromDate: Date,
                                  val toDate: Date,
                                  val registryUsesPD: Boolean,
                                  val registreringar: List<Registrering> = emptyList())

data class Registrering(val personId: String,
                        val variabler: List<Variabel> = emptyList())

data class Variabel(val variabelName: String = "",
                    val variabelValue: String = "",
                    val variabelResult: Result?,
                    val variabelComment: String?)

enum class Result {
    CORRECT, WRONG, MISSING
}
