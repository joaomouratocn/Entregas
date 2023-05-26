package com.example.entregas.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.entregas.R
import com.example.entregas.ui.adapter.ReleaseAdapter
import com.example.entregas.data.room.AppDataBase
import com.example.entregas.data.room.ReleaseEntity
import com.example.entregas.databinding.ActivityMainBinding
import com.example.entregas.databinding.DialogReleaseBinding
import com.example.entregas.util.createDialog
import com.example.entregas.util.getDate
import com.example.entregas.util.toCoinString
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val releaseDao by lazy {
        AppDataBase.getInstance(this).releaseDao()
    }

    private val releaseAdapter by lazy {
        ReleaseAdapter {
            showDialogInsertRelease(releaseEntity = it)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initConfig()
        Log.i("TAG", "onCreate: ${getDate()}")
    }

    private fun initConfig() {
        title = getString(R.string.str_delivery_report)
        binding.apply {
            recycleReport.layoutManager = LinearLayoutManager(this@MainActivity)
            recycleReport.adapter = releaseAdapter
        }

        lifecycleScope.launch {
            releaseDao.getAllReleases().collect{
                it?.let {
                    val tot = calcDeliveryAndPayments(it)
                    updateView(it, tot, true)
                }
            }
        }

        binding.btnInsert.setOnClickListener { showDialogInsertRelease() }

        binding.btnShowDelivery.setOnClickListener {
            lifecycleScope.launch {
                releaseDao.getAllReleaseBy("E").firstOrNull()?.let{
                    val tot = it.sumOf { releaseEntity -> releaseEntity.releaseValue }
                    updateView(it, tot)
                }
            }
        }

        binding.btnShowPayment.setOnClickListener {
            lifecycleScope.launch {
                lifecycleScope.launch {
                    releaseDao.getAllReleaseBy("P").firstOrNull()?.let{
                        val tot = it.sumOf { releaseEntity -> releaseEntity.releaseValue }
                        updateView(it, tot)
                    }
                }
            }
        }

        binding.btnShowAll.setOnClickListener {
            lifecycleScope.launch {
                lifecycleScope.launch {
                    releaseDao.getAllReleases().firstOrNull()?.let{
                        val tot = calcDeliveryAndPayments(it)
                        updateView(it, tot, true)
                    }
                }
            }
        }
    }

    private fun calcDeliveryAndPayments(it: List<ReleaseEntity>): Double {
        val totDelivery: Double = it.filter { releaseEntity -> releaseEntity.releaseOp == "E" }
            .sumOf { releaseEntity -> releaseEntity.releaseValue }
        val totPayment: Double = it.filter { releaseEntity -> releaseEntity.releaseOp == "P" }
            .sumOf { releaseEntity -> releaseEntity.releaseValue }
        return totDelivery - totPayment
    }

    private fun updateView(allItems: List<ReleaseEntity>, tot:Double, receive:Boolean = false) {
        releaseAdapter.updateItems(allItems)
        if (receive){ binding.txvPartial.text = getString(R.string.str_to_received) }
        else{ binding.txvPartial.text = getString(R.string.str_tot) }
        binding.txvPartialValue.text = tot.toCoinString()
    }

    private fun showDialogInsertRelease(releaseEntity: ReleaseEntity? = null) {
        val viewDialog = DialogReleaseBinding.inflate(layoutInflater)
        val dialog = createDialog(viewDialog)
        viewDialog.apply {
            txvTitle.text = getString(R.string.str_insert_release)
            releaseEntity?.let {
                txvTitle.text = getString(R.string.str_update_release)
                edtValueRelease.setText(releaseEntity.releaseValue.toString())
                edtDescRelease.setText(releaseEntity.releaseDesc)
                edtDateRelease.setText(releaseEntity.releaseDate)
                if (releaseEntity.releaseOp == "E"){rbDelivery.isChecked = true}else{rbPayment.isChecked = true}
            }

            btnCancel.setOnClickListener { dialog.dismiss() }
            btnSave.setOnClickListener {
                if (edtValueRelease.text.isNullOrBlank()) {
                    tilValueRelease.error = getString(R.string.str_insert_the_value_o_release)
                }else if (rgOpRelease.checkedRadioButtonId == -1){
                    Toast.makeText(this@MainActivity,"Select release type!", Toast.LENGTH_SHORT).show()
                } else {
                    lifecycleScope.launch {
                        val releaseInsert = ReleaseEntity(
                            releaseId = releaseEntity?.releaseId ?: 0,
                            releaseOp = if(rbDelivery.isChecked){"E"}else{"P"},
                            releaseValue = edtValueRelease.text.toString().toDouble(),
                            releaseDesc = edtDescRelease.text.toString(),
                            releaseDate = if (!edtDateRelease.text.isNullOrBlank()) {
                                edtDateRelease.text.toString()
                            } else {
                                getDate()
                            }
                        )
                        releaseDao.insertRelease(releaseInsert)
                        dialog.dismiss()
                    }
                }
            }
        }
        dialog.show()
    }
}