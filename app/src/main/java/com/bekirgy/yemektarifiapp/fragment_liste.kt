package com.bekirgy.yemektarifiapp

import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_liste.*
import java.lang.Exception


class fragment_liste : Fragment() {

    var yemekismiListesi= ArrayList<String>()
    var yemekidListesi = ArrayList<Int>()
    private  lateinit var listeAdapter: ListeRecylerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_liste, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeAdapter= ListeRecylerAdapter(yemekismiListesi,yemekidListesi)
        recylerView1.layoutManager=LinearLayoutManager(context)
        recylerView1.adapter=listeAdapter



        sqlVeriAlma()
    }

    fun sqlVeriAlma(){

        try {
            activity?.let {
                val database= it.openOrCreateDatabase("YemekApp",Context.MODE_PRIVATE,null)
                val cursor=database.rawQuery("SELECT * FROM yemekler",null)
                val yemekismiIndex=cursor.getColumnIndex("yemekadi")
                val yemekidIndex=cursor.getColumnIndex("id")

                //onceden olan verileri temizlemek için
                yemekismiListesi.clear()
                yemekidListesi.clear()

                while (cursor.moveToNext()){

                    yemekismiListesi.add(cursor.getString(yemekismiIndex))
                    yemekidListesi.add(cursor.getInt(yemekidIndex))
                    println(cursor.getInt(yemekidIndex))



                }
                listeAdapter.notifyDataSetChanged()
                cursor.close()
            }



        }catch (e:Exception){
            e.printStackTrace()
        }


    }


}