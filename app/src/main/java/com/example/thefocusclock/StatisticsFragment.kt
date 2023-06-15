package com.example.thefocusclock

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thefocusclock.db.FocusRecord
import com.example.thefocusclock.db.FocusRecordDao
import com.example.thefocusclock.db.FocusRecordDatabase


class StatisticsFragment : Fragment(R.layout.fragment_statistics) {
    private lateinit var rvFocusRecords:RecyclerView
    private lateinit var rvFocusRecordsAdapter: FocusRecordRecyclerViewAdapter

    private lateinit var focusRecordDatabase: FocusRecordDatabase
    private lateinit var focusRecordDao: FocusRecordDao
    private lateinit var statViewModel: StatisticsFragmentViewModel


    private lateinit var statViewModelFactory: StatisticsFragmentViewModelFactory



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvFocusRecords=view.findViewById(R.id.rvFocusRecord)

        focusRecordDatabase= activity?.let { FocusRecordDatabase.getInstanceOfFocusRecordDatabase(it.applicationContext) }!!
        focusRecordDao=focusRecordDatabase.focusRecordDao
        statViewModelFactory= StatisticsFragmentViewModelFactory(focusRecordDao)
        statViewModel= ViewModelProvider(this,statViewModelFactory)[StatisticsFragmentViewModel::class.java]


        rvFocusRecords=view.findViewById(R.id.rvFocusRecord)
        rvFocusRecordsAdapter=FocusRecordRecyclerViewAdapter()
        rvFocusRecords.layoutManager= LinearLayoutManager (context)
        rvFocusRecords.adapter=rvFocusRecordsAdapter
        displayFocusRecords()




    }

    private fun displayFocusRecords() {
        statViewModel.getAllFocusRecords().observe( viewLifecycleOwner , Observer {
            rvFocusRecordsAdapter.setList(it)
            rvFocusRecordsAdapter.notifyDataSetChanged()
        })
    }

}