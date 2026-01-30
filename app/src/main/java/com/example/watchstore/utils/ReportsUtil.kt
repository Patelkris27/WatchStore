package com.example.watchstore.utils

import android.content.Context
import android.os.Environment
import com.google.firebase.database.FirebaseDatabase
import java.io.File
import java.io.FileWriter

object ReportsUtil {

    fun exportOrders(context: Context) {

        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(dir, "orders_report.csv")

        FirebaseDatabase.getInstance().reference
            .child("orders")
            .get()
            .addOnSuccessListener { snapshot ->

                val writer = FileWriter(file)
                writer.append("OrderID,ProductID,Quantity,Total,Status,Date\n")

                for (s in snapshot.children) {

                    val orderId = s.key
                    val productId = s.child("productId").value
                    val quantity = s.child("quantity").value
                    val total = s.child("total").value
                    val status = s.child("status").value
                    val date = s.child("date").value

                    writer.append(
                        "$orderId,$productId,$quantity,$total,$status,$date\n"
                    )
                }

                writer.flush()
                writer.close()
            }
    }
}
