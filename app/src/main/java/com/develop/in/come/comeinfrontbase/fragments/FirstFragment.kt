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
import com.develop.`in`.come.comeinfrontbase.activities.GroupActivity
import com.develop.`in`.come.comeinfrontbase.activities.MainActivity
import com.develop.`in`.come.comeinfrontbase.util.Constants

class FirstFragment : Fragment(){
    lateinit var mlvGroupList: ListView
    lateinit var adapter: ArrayAdapter<*>
    lateinit var mSharedPreferences: SharedPreferences


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_groups, container, false)

        mlvGroupList = view.findViewById(R.id.lv_GroupList) as ListView
        var groupList  = (activity as MainActivity).getGroupList()

        adapter = ArrayAdapter<String>(this.context, android.R.layout.simple_selectable_list_item,groupList)

        mlvGroupList.setAdapter(adapter)
        mlvGroupList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // Toast.makeText(this, "Click on " + listNotes[position].title, Toast.LENGTH_SHORT).show()
            val selectedItem = parent.getItemAtPosition(position) as String
            val intent = Intent(context, GroupActivity::class.java)
            intent.putExtra("groupName", selectedItem)
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            intent.putExtra("username", (mSharedPreferences.getString(Constants.NAME,"")))
            startActivity(intent)
        }


        return view
    }
}