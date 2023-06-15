package com.example.thefocusclock

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.thefocusclock.db.FocusRecord

class FocusRecordRecyclerViewAdapter(): RecyclerView.Adapter<FocusRecordRecyclerViewAdapter.FocusRecordViewHolder>() {



    inner class FocusRecordViewHolder(view:View):RecyclerView.ViewHolder(view){
        private  var tvDate:TextView=view.findViewById(R.id.tvDateRI)
        private  var tvTime:TextView=view.findViewById(R.id.tvMinutesRI)


        fun bind(foucsRecord: FocusRecord){
            tvDate.text=foucsRecord.date
            tvTime.text=foucsRecord.timeMinutes

        }

    }
    private val focusRecordList:ArrayList<FocusRecord > = ArrayList<FocusRecord> ()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FocusRecordViewHolder {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.focus_record_item,parent,false)
        return FocusRecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: FocusRecordViewHolder, position: Int) {
        holder.bind(focusRecordList[position])
    }

    override fun getItemCount(): Int {
        return focusRecordList.size
    }

    fun setList(records: List<FocusRecord>){
        focusRecordList.clear()
        focusRecordList.addAll(records)
    }
}