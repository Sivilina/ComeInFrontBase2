package com.develop.`in`.come.comeinfrontbase.fragments.private_chat

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.activities.ContactListActivity
import com.develop.`in`.come.comeinfrontbase.models.User
import java.util.ArrayList

class FriendsFragment : Fragment(){
    lateinit var mlvUserList: ListView
    lateinit var adapter: ArrayAdapter<*>
    lateinit var mSharedPreferences: SharedPreferences
    lateinit var mIbAddFriend: ImageButton

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

       mIbAddFriend = view.findViewById(R.id.ib_add_friend) as ImageButton
       mIbAddFriend.setOnClickListener {
           val intent = Intent(activity, ContactListActivity::class.java)
           startActivity(intent)
       }
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