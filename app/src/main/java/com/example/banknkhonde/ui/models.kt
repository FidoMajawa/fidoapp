// app/src/main/java/com/example/banknkhonde/ui/models.kt
package com.example.banknkhonde.ui.models

data class Contribution(
    val memberName: String = "",
    val amount: Int = 0,
    val date: String = "", // should match the name used here
    val chairEmail: String = ""
)


data class Loan(
    val memberName: String = "",
    val amount: Int = 0,
    val date: String = "",
    val status: String = "",
    val chairEmail: String = ""
)
