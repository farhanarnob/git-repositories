package com.example.github.repositories.ui.user_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.github.repositories.R
import com.example.github.repositories.RepositoryAdapter
import com.example.github.repositories.data.RepositoryDTO
import com.example.github.repositories.databinding.FragmentDetailBinding
import com.example.github.repositories.databinding.FragmentMainBinding
import com.example.github.repositories.databinding.FragmentUserBinding
import com.example.github.repositories.ui.main_fragment.MainFragmentDirections
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
        viewModel.fetchUser(args.ownerDTO.login)
    }

    private fun observe() {
        viewModel.repositories.observe(viewLifecycleOwner){
            adapter?.submitList(it)
        }
        viewModel.user.observe(viewLifecycleOwner) {
            binding.detail.text = String.format("Twitter handle: ${it.twitter_username?:"N/A"}")
            viewModel.fetchRepositories(it.repos_url!!)
        }
        viewModel.repositoryNetworkFetchError.observe(viewLifecycleOwner){

        }

        viewModel.getUserNetworkFetchError.observe(viewLifecycleOwner){

        }
    }

    private fun uiSetup() {
        binding.title.text = args.ownerDTO.login
        Picasso.get().load(args.ownerDTO.avatar_url?.toUri()).into(binding.image)
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
}