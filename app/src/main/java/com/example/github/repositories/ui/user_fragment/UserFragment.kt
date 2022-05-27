package com.example.github.repositories.ui.user_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.github.repositories.R
import com.example.github.repositories.RepositoryAdapter
import com.example.github.repositories.data.RepositoryDTO
import com.example.github.repositories.databinding.FragmentUserBinding
import com.squareup.picasso.Picasso

class UserFragment: Fragment() {
    private var adapter: RepositoryAdapter? = null
    private val viewModel = UserViewModel()
    private lateinit var binding: FragmentUserBinding
    private val args: UserFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiSetup()
        observe()
        loadingState(loading = true)
        viewModel.fetchUser(args.ownerDTO?.login)
    }

    private fun observe() {
        viewModel.user.observe(viewLifecycleOwner) {
            binding.detail.text = String.format("Twitter handle: ${it.twitter_username?:"N/A"}")
            viewModel.fetchRepositories(it.repos_url!!)
        }
        viewModel.repositories.observe(viewLifecycleOwner){
            adapter?.submitList(it)
            loadingState(loading = false)
        }
        viewModel.gitDownloadRepository.networkFetchError.asLiveData().observe(viewLifecycleOwner){
            adapter?.submitList(null)
            loadingState(loading = false)
            errorState(it)
        }

    }

    private fun uiSetup() {
        binding.emptyLayout.btnFromEmptyTry.setOnClickListener {
            loadingState(loading = true)
            viewModel.fetchUser(args.ownerDTO?.login)
        }
        binding.title.text = args.ownerDTO?.login?:"N/A"
        Picasso.get().load(args.ownerDTO?.avatar_url?.toUri()).into(binding.image)
        binding.list.layoutManager = LinearLayoutManager(context)
        adapter = RepositoryAdapter(
            object : RepositoryAdapter.RepositoryAdapterItemListener {
                override fun detailItemClick(repositoryDTO: RepositoryDTO) {
                    lifecycleScope.launchWhenResumed {
                        val directions = UserFragmentDirections
                            .actionUserFragmentToDetailFragment(repositoryDTO)
                        findNavController().navigate(directions)
                    }
                }
            })
        binding.list.adapter = adapter
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