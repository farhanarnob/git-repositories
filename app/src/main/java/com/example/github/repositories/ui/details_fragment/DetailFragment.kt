package com.example.github.repositories.ui.details_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.github.repositories.R
import com.example.github.repositories.data.LocalDataStore
import com.example.github.repositories.databinding.FragmentDetailBinding
import com.squareup.picasso.Picasso

class DetailFragment: Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private val args: DetailFragmentArgs by navArgs()
    private val localDataStore = LocalDataStore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiSetup()

    }

    private fun uiSetup() {
        binding.title.text = args.repositoryDTO?.name
        binding.detail.text = String.format("Created by "
                + args.repositoryDTO?.owner?.login + ", at " + args.repositoryDTO?.created_at)
        Picasso.get().load(args.repositoryDTO?.owner?.avatar_url).into(binding.image)
        binding.description.text = args.repositoryDTO?.description
        binding.url.text = args.repositoryDTO?.html_url

        binding.image.setImageResource(
            if (localDataStore.getBookmarks().contains(args.repositoryDTO))
                R.drawable.baseline_bookmark_black_24
            else
                R.drawable.baseline_bookmark_border_black_24
        )
        binding.image.setOnClickListener {
            val isBookmarked = localDataStore.getBookmarks().contains(args.repositoryDTO)
            args.repositoryDTO?.let { it1 -> localDataStore.bookmarkRepo(it1, !isBookmarked) }
            binding.image.setImageResource(if (!isBookmarked) R.drawable.baseline_bookmark_black_24
            else R.drawable.baseline_bookmark_border_black_24)
        }
        binding.detail.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                findNavController().navigate(DetailFragmentDirections.actionDetailFragmentToUserFragment(
                    args.repositoryDTO?.owner
                ))
            }
        }
    }
}