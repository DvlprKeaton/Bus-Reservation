package com.example.bus21

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bus21.token.TokenQuickStartActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_kotlin_quick_start.*

class KotlinQuickStartActivity : AppCompatActivity() {

    private val clientIdWasUpdated by lazy {
        PAYPAL_CLIENT_ID != "YOUR-CLIENT-ID-HERE"
    }

    private val secretWasUpdated by lazy {
        PAYPAL_SECRET != "ONLY-FOR-QUICKSTART-DO-NOT-INCLUDE-SECRET-IN-CLIENT-SIDE-APPLICATIONS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_quick_start)

        val itemValue = intent.getStringExtra("itemValue")
        val itemAmount = intent.getStringExtra("itemAmount")
        val bid = intent.getStringExtra("bid")
        val time = intent.getStringExtra("time")

        buyWithOrder.setOnClickListener {
            if (clientIdWasUpdated) {
                startActivity(OrdersQuickStartActivity.startIntent(this))
            } else {
                displayErrorSnackbar("Please Update PAYPAL_CLIENT_ID In QuickStartConstants.")
            }
        }

        buyWithOrderToken.setOnClickListener {
            if (clientIdWasUpdated && secretWasUpdated) {
                startActivity(TokenQuickStartActivity.startIntent(this))
            } else {
                displayErrorSnackbar("Please Update PAYPAL_CLIENT_ID and PAYPAL_SECRET In QuickStartConstants.")
            }
        }

        buyWithPaymentButton.setOnClickListener {
            if (clientIdWasUpdated) {
                val moveToPayment = Intent(this, PaymentButtonQuickStartActivity::class.java)
                moveToPayment.putExtra("itemValue", itemValue)
                moveToPayment.putExtra("itemAmount", itemAmount)
                moveToPayment.putExtra("bid", bid)
                moveToPayment.putExtra("time", time)
                startActivity(moveToPayment)

                //startActivity(PaymentButtonQuickStartActivity.startIntent(this))
            } else {
                displayErrorSnackbar("Please Update PAYPAL_CLIENT_ID In QuickStartConstants.")
            }
        }
    }

    private fun displayErrorSnackbar(errorMessage: String) {
        Snackbar.make(rootQuickStart, errorMessage, Snackbar.LENGTH_INDEFINITE)
            .apply { setAction("Got It 👍") { dismiss() } }
            .show()
    }
}
