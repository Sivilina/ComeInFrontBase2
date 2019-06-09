package com.develop.`in`.come.comeinfrontbase.activities

import android.app.ListActivity
import android.content.Intent
import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.View
import android.widget.Toolbar
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import com.develop.`in`.come.comeinfrontbase.R
import kotlinx.android.synthetic.main.activity_contact_list.*

class ContactListActivity : ListActivity() {

    lateinit var mLvContactList: ListView
    lateinit var mCursor:Cursor
    lateinit var mToolbar: Toolbar


    override fun getSelectedItemId(): Long {
        return super.getSelectedItemId()
    }

    override fun getSelectedItemPosition(): Int {
        return super.getSelectedItemPosition()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)
        mToolbar = findViewById(R.id.toolbar_contactList) as Toolbar
        mToolbar.setTitle("New message")
        mToolbar.setNavigationIcon(R.drawable.ic_back)
        setActionBar(mToolbar)
        mToolbar.setNavigationOnClickListener(object : View.OnClickListener{
            override fun onClick(view: View) {
                finish()
            }
        })

        mCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null)
        startManagingCursor(mCursor)

        var from = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone._ID)
        var to = intArrayOf(android.R.id.text1,android.R.id.text2)

        var listAdapter = SimpleCursorAdapter(this, android.R.layout.simple_list_item_2,mCursor,from,to)
        setListAdapter(listAdapter)
        mLvContactList = listView
        mLvContactList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE)


        mLvContactList.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, ChatActivity::class.java)
            var cur = listAdapter.getItem(position) as Cursor
            intent.putExtra("fullname", cur.getString(0))
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search,menu)
        return true
    }

}
