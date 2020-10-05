package com.simple.travelsharing.view.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.simple.travelsharing.LoginActivity
import com.simple.travelsharing.R
import com.simple.travelsharing.adapter.AdapterAdminManageGroup
import com.simple.travelsharing.data.Constants
import com.simple.travelsharing.data.SharedPreference
import com.simple.travelsharing.model.ModelCreateUser
import com.simple.travelsharing.utils.Utils
import com.simple.travelsharing.utils.createUser
import kotlinx.android.synthetic.main.activity_admin_main.*

class AdminMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)

        initOnClick()


//        ModelCreateUser(
//            Utils.getRandomId(),
//            "User001",
//            "0123456789",
//            "address",
//            "", "user1",
//            "123456",
//            Constants.USER_TYPE_USER
//        ).createUser()
    }

    private fun initOnClick() {
        btManageGruop.setOnClickListener {
            val intentLogin = Intent(this, ManageGroupActivity::class.java)
            startActivity(intentLogin)
        }

        btExit.setOnClickListener {
            Utils.dialogConfirmExit(this){
                SharedPreference(this).clear()
                val intentLogin = Intent(this, LoginActivity::class.java)
                intentLogin.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intentLogin)
            }
        }
    }
}