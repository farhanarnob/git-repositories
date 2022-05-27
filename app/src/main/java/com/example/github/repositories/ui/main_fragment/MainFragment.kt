package com.example.github.repositories.ui.main_fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
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
        loadingState(loading = true)
        viewModel.fetchItems()
    }

    private fun observe() {
        viewModel.repositories.observe(viewLifecycleOwner) {
            if(binding.swipeRefresh.isRefreshing){
                binding.swipeRefresh.isRefreshing = false
            }
            adapter?.submitList(it?.take(20))
            loadingState(loading = false)
        }
        viewModel.gitDownloadRepository
            .networkFetchError.asLiveData().observe(viewLifecycleOwner){
            if(binding.swipeRefresh.isRefreshing){
                binding.swipeRefresh.isRefreshing = false
            }
            adapter?.submitList(null)
            loadingState(loading = false)
            errorState(it)
        }
    }


    private fun uiSetup() {
        binding.emptyLayout.btnFromEmptyTry.setOnClickListener {
            loadingState(loading = true)
            viewModel.refresh()
        }
        binding.swipeRefresh.setOnRefreshListener {
            loadingState(loading = true)
            viewModel.refresh()
        }
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

    private fun loadingState(loading: Boolean){
        val loadingVisibilityView = if(loading)View.VISIBLE else View.GONE
        val loadingGoneView = View.GONE
        lifecycleScope.launchWhenResumed {
            binding.emptyLayout.emptyParentLayout.visibility = loadingVisibilityView
            binding.emptyLayout.pbWaiting.visibility = loadingVisibilityView
            binding.emptyLayout.textNotifications.visibility = loadingVisibilityView
            binding.emptyLayout.textNotifications.text = getString(R.string.loading_please_wait)
            if(loading) {
                binding.emptyLayout.btnFromEmptyTry.visibility = loadingGoneView
                binding.emptyLayout.imageView3.visibility = loadingGoneView
            }
        }
    }


    private fun errorState(message: String?) {
        lifecycleScope.launchWhenResumed {
            binding.emptyLayout.pbWaiting.visibility = View.GONE
            binding.emptyLayout.emptyParentLayout.visibility = View.VISIBLE
            binding.emptyLayout.textNotifications.visibility = View.VISIBLE
            binding.emptyLayout.btnFromEmptyTry.visibility = View.VISIBLE
            binding.emptyLayout.imageView3.visibility = View.VISIBLE
            binding.emptyLayout.textNotifications.text = message
        }
    }
}