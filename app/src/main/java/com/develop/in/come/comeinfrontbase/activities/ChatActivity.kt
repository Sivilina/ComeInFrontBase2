package com.develop.`in`.come.comeinfrontbase.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import com.develop.`in`.come.comeinfrontbase.R
import android.content.Intent
import android.view.View


class ChatActivity : AppCompatActivity() {
    lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val intent = intent
        mToolbar = findViewById(R.id.toolbar_chat) as Toolbar
        mToolbar.setTitle(intent.getStringExtra("fullname"))
        println("^&*3" + intent.getStringExtra("fullname"))
        mToolbar.setNavigationIcon(R.drawable.ic_back)
        mToolbar.setNavigationOnClickListener(object : View.OnClickListener{
            override fun onClick(view: View) {
                finish()
            }
        })
        setSupportActionBar(mToolbar)

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_message,menu)
        return true
    }

}
