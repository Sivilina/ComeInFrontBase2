package com.develop.`in`.come.comeinfrontbase.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.activities.ChatActivity
import com.develop.`in`.come.comeinfrontbase.activities.MainActivity
import com.develop.`in`.come.comeinfrontbase.models.User
import com.develop.`in`.come.comeinfrontbase.util.Constants
import com.google.gson.Gson
import java.util.ArrayList

class SecondFragment : Fragment(){
    lateinit var mlvUserList: ListView
    lateinit var adapter: ArrayAdapter<*>
    lateinit var mSharedPreferences: SharedPreferences

    fun getStringArrayFromModel(modelArrayList: ArrayList<User>): ArrayList<String> {

        val res = ArrayList<String>()

        for (m in modelArrayList) {
            res.add(m.username!!)
        }
        return res
    }
   /* fun getIdByUsername(username: String) : String{
        var uList = (activity as MainActivity).getUserList() as ArrayList<User>
        for(u in uList){
            if(u.username == username)
                return u.token!!
        }
        return ""
    }*/
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_friends, container, false)

     /*   mlvUserList = view.findViewById(R.id.rv_UserList) as ListView
        adapter = ArrayAdapter<String>(this.context, android.R.layout.simple_selectable_list_item, getStringArrayFromModel((activity as MainActivity).getUserList()))
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val gson = Gson()
        val json = mSharedPreferences.getString(Constants.CURRENT_USER, "")
        val currentUser = gson.fromJson<Any>(json, User::class.java) as User

        mlvUserList.setAdapter(adapter)
        mlvUserList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        // Toast.makeText(this, "Click on " + listNotes[position].title, Toast.LENGTH_SHORT).show()
            val selectedItem = parent.getItemAtPosition(position) as String
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("toId", getIdByUsername(selectedItem))
            intent.putExtra("toUser", selectedItem)
            intent.putExtra("username", currentUser.username)
            startActivity(intent)
        }

*/
        return view
    }

   /* override fun onResume() {
        adapter = ArrayAdapter<String>(this.context, android.R.layout.simple_selectable_list_item, getStringArrayFromModel((activity as MainActivity).getUserList()))
        mlvUserList.setAdapter(adapter)
        super.onResume()
    }
*/


}