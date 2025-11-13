// app/src/main/java/com/example/banknkhonde/ui/models.kt
package com.example.banknkhonde.ui.models

data class Contribution(
    val memberName: String = "",
    val amount: Int = 0,
    val date: String = "", // date of contribution
    val chairEmail: String = ""
)

data class Loan(
    val id: String = "", // unique document id
    val memberName: String = "",
    val amount: Int = 0,
    val date: String = "", // loan application date
    val status: String = "Pending", // "Pending" or "Approved"
    val chairEmail: String = ""
)

data class MemberModel(
    val memberId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val imageUrl: String = "",
    val chairEmail: String = ""
)
