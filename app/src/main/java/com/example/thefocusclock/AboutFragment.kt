package com.example.thefocusclock

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AboutFragment : Fragment(R.layout.fragment_about) {

    private lateinit var rvAboutFragment: RecyclerView
    private lateinit var rvAboutFragmentAdapter: AboutRvAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvAboutFragment=view.findViewById(R.id.rvAboutFragments)
        val arrayList:ArrayList<AboutData> = arrayListOf(
            AboutData("PomoDaro Technique", ""),
            AboutData("Introduction" , requireActivity().getString(R.string.pomodaro_intro)),
            AboutData("Steps" , requireActivity().getString(R.string.pomodaro_steps)),
            AboutData("About" , requireActivity().getString(R.string.pomodaro_about1)),
            AboutData("About our app" , requireActivity().getString(R.string.pomodaro_advantages)))

        rvAboutFragmentAdapter= AboutRvAdapter(arrayList)
        rvAboutFragment.layoutManager = LinearLayoutManager(context)
        rvAboutFragment.adapter=rvAboutFragmentAdapter

    }

}