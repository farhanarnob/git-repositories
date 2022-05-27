package com.example.github.repositories.ui.main_fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.github.repositories.R
import com.example.github.repositories.RepositoryAdapter
import com.example.github.repositories.data.RepositoryDTO
import com.example.github.repositories.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var adapter: RepositoryAdapter? = null
    private val viewModel = MainViewModel()

    private lateinit var binding: FragmentMainBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiSetup()
        observe()
        viewModel.fetchItems()
    }

    private fun observe() {
        viewModel.repositories.observe(viewLifecycleOwner) {
            if(binding.swipeRefresh.isRefreshing){
                binding.swipeRefresh.isRefreshing = false
            }
            adapter?.submitList(it?.take(20))
        }
        viewModel.error.observe(viewLifecycleOwner){
            if(binding.swipeRefresh.isRefreshing){
                binding.swipeRefresh.isRefreshing = false
            }
            adapter?.submitList(null)
        }
    }

    private fun uiSetup() {
        binding.swipeRefresh.setOnRefreshListener { viewModel.refresh() }
        binding.newsList.layoutManager = LinearLayoutManager(context)
        adapter = RepositoryAdapter(
            object : RepositoryAdapter.RepositoryAdapterItemListener {
                override fun detailItemClick(repositoryDTO: RepositoryDTO) {
                    lifecycleScope.launchWhenResumed {
                        val directions = MainFragmentDirections
                            .actionMainFragmentToDetailFragment(repositoryDTO)
                        findNavController().navigate(directions)
                    }
                }
            })
        binding.newsList.adapter = adapter
    }
}