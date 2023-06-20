package com.example.bus21

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bus21.R
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.cancel.OnCancel
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.Amount
import com.paypal.checkout.order.AppContext
import com.paypal.checkout.order.Order
import com.paypal.checkout.order.PurchaseUnit
import com.paypal.checkout.paymentbutton.PaymentButtonEligibilityStatus
import kotlinx.android.synthetic.main.activity_payment_button_quick_start.*
import java.util.*

class PaymentButtonQuickStartActivity : AppCompatActivity() {

    private val tag = javaClass.simpleName

    val SHARED_PREFS = "shared_prefs"

    // key for storing email.
    val CONTACT_KEY = "contact_key"

    // key for storing password.
    val PASSWORD_KEY = "password_key"

    val MARKERTITLE_KEY = "markertitle_key"

    var sharedpreferences: SharedPreferences? = null
    var stitle: String? = null
    var scontact:kotlin.String? = null

    var bfname: String? = null
    var blname:kotlin.String? = null
    var time:kotlin.String? = null

    var dao = DOABooking()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_button_quick_start)
        paymentButton.onEligibilityStatusChanged = { buttonEligibilityStatus: PaymentButtonEligibilityStatus ->
            Log.v(tag, "OnEligibilityStatusChanged")
            Log.d(tag, "Button eligibility status: $buttonEligibilityStatus")


           val offlinePayment = findViewById<View>(R.id.proceedPayment) as Button

            val itemValue = intent.getStringExtra("itemValue")
            val itemAmount = intent.getStringExtra("itemAmount")
            val bid = intent.getStringExtra("bid")
            var time = intent.getStringExtra("time")


            offlinePayment.setOnClickListener{

                val moveToPayment = Intent(this, Dashboard::class.java)
                moveToPayment.putExtra("itemValue", itemValue)
                moveToPayment.putExtra("itemAmount", itemAmount)
                moveToPayment.putExtra("bid", bid)
                moveToPayment.putExtra("time", time)
                startActivity(moveToPayment)

            }
        }
        setupPaymentButton()
    }

    private fun setupPaymentButton() {
        paymentButton.setup(
            createOrder = CreateOrder { createOrderActions ->
                Log.v(tag, "CreateOrder")
                createOrderActions.create(
                    Order.Builder()
                        .appContext(
                            AppContext(
                                userAction = UserAction.PAY_NOW
                            )
                        )
                        .intent(OrderIntent.CAPTURE)
                        .purchaseUnitList(
                            listOf(
                                PurchaseUnit.Builder()
                                    .amount(
                                        Amount.Builder()
                                            .value("0.01")
                                            .currencyCode(CurrencyCode.USD)
                                            .build()
                                    )
                                    .build()
                            )
                        )
                        .build()
                        .also { Log.d(tag, "Order: $it") }
                )
            },
            onApprove = OnApprove { approval ->
                Log.v(tag, "OnApprove")
                Log.d(tag, "Approval details: $approval")
                approval.orderActions.capture { captureOrderResult ->
                    Log.v(tag, "Capture Order")
                    Log.d(tag, "Capture order result: $captureOrderResult")
                }
            },
            onCancel = OnCancel {
                Log.v(tag, "OnCancel")
                Log.d(tag, "Buyer cancelled the checkout experience.")
            },
            onError = OnError { errorInfo ->
                Log.v(tag, "OnError")
                Log.d(tag, "Error details: $errorInfo")
            }
        )
    }

    companion object {
        fun startIntent(context: Context): Intent {
            return Intent(context, PaymentButtonQuickStartActivity::class.java)
        }
    }
}
