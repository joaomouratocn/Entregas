package com.example.entregas.util

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getDate():String{
    val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
    return sdf.format(Date())
}

fun Double.toCoinString():String{
    val currencyCoinFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    return currencyCoinFormat.format(this)
}

fun Context.createDialog(
    viewBinding: ViewBinding? = null,
    title: String? = null,
    message: String? = null,
    negativeButtonText: String? = null,
    positiveButtonText: String? = null,
    actionPositive: (() -> Unit)? = null
): AlertDialog {
    return if(viewBinding != null){
        AlertDialog.Builder(this).let {
            it.setView(viewBinding.root)
            it.create()
        }
    }else{
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setNegativeButton(negativeButtonText, null)
            .setPositiveButton(positiveButtonText){_,_ ->
                actionPositive?.invoke()
            }
            .create()
    }
}
