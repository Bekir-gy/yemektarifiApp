package com.bekirgy.yemektarifiapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyler_row.view.*

class ListeRecylerAdapter(val yemeklistesi:ArrayList<String>, val idListesi:ArrayList<Int>): RecyclerView.Adapter<ListeRecylerAdapter.YemekHolder>() {

    class  YemekHolder(itemView: View): RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YemekHolder {
        val inflater=LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.recyler_row,parent,false)
        return  YemekHolder(view)
    }

    override fun onBindViewHolder(holder: YemekHolder, position: Int) {
        holder.itemView.recyler_view_text.text=yemeklistesi[position]
        holder.itemView.setOnClickListener {
            val action =fragment_listeDirections.actionFragmentListeToFragmentTarif("recylerdangeldim",idListesi[position])
            Navigation.findNavController(it).navigate(action)

        }
    }

    override fun getItemCount(): Int {
        return  yemeklistesi.size


    }

}