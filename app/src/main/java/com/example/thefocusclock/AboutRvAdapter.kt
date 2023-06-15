package com.example.thefocusclock

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView


class AboutRvAdapter(private val arrayList:ArrayList<AboutData>):RecyclerView.Adapter<AboutRvAdapter.AboutViewHolder>() {

    inner class AboutViewHolder(view: View):RecyclerView.ViewHolder(view){
        val tvHead= view.findViewById<TextView>(R.id.tvHeading)
        val tvContent= view.findViewById<TextView>(R.id.tvContent)

        fun bind(myData: AboutData){
            tvHead.text=myData.first
            if(myData.second!=""){
                tvContent.text=myData.second
                tvHead.textSize=22f

            }
            else{
                tvContent.isVisible=false
                tvHead.setTextColor(  tvHead.context.getColor(R.color.red) )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.about_item,parent,false)
        return  AboutViewHolder(view)
    }

    override fun onBindViewHolder(holder: AboutViewHolder, position: Int) {
        holder.bind(arrayList[position])
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun initRV(){
        arrayList.clear()
    }
}