package com.example.github.repositories

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.github.repositories.data.RepositoryDTO

class MainFragment : Fragment() {

    private val viewModel = MainViewModel()

    private var swipeRefresh: SwipeRefreshLayout? = null
    private var recyclerview: RecyclerView? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        viewModel.fetchItems()

        swipeRefresh = view.findViewById(R.id.swipe_refresh)
        swipeRefresh!!.setOnRefreshListener { viewModel.refresh() }

        recyclerview = view.findViewById(R.id.news_list)
        recyclerview!!.layoutManager = LinearLayoutManager(context)

        viewModel.repositories.observe(viewLifecycleOwner) {
            val adapter = RepositoryAdapter(it.take(20).toMutableList(),
                object :RepositoryAdapter.RepositoryAdapterItemListener{
                override fun detailItemClick(repositoryDTO: RepositoryDTO) {
                    lifecycleScope.launchWhenResumed {
                        val directions = MainFragmentDirections.actionMainFragmentToDetailFragment(repositoryDTO)
                        findNavController().navigate(directions)
                    }
                }

            })
            recyclerview!!.adapter = adapter
        }
        return view
    }
}