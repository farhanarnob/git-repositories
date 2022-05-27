package com.example.github.repositories.ui.details_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.github.repositories.R
import com.example.github.repositories.data.LocalDataStore
import com.squareup.picasso.Picasso

class DetailFragment: Fragment() {

    private var title: TextView? = null
    private var image: ImageView? = null
    private var detail: TextView? = null
    private var description: TextView? = null
    private var url: TextView? = null

    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        title = view.findViewById(R.id.title)
        image = view.findViewById(R.id.image)
        detail = view.findViewById(R.id.detail)
        description = view.findViewById(R.id.description)
        url = view.findViewById(R.id.url)

        val repository = args.repositoryDTO
        title!!.text = repository.name
        detail!!.text = String.format("Created by "
                + repository.owner!!.login + ", at " + repository.created_at)
        Picasso.get().load(repository.owner!!.avatar_url).into(image)
        description!!.text = repository.description
        url!!.text = repository.html_url

        image!!.setImageResource(
            if (LocalDataStore.instance.getBookmarks().contains(repository))
                R.drawable.baseline_bookmark_black_24
            else
                R.drawable.baseline_bookmark_border_black_24
        )
        image!!.setOnClickListener {
            val isBookmarked = LocalDataStore.instance.getBookmarks().contains(repository)
            LocalDataStore.instance.bookmarkRepo(repository, !isBookmarked)
            image!!.setImageResource(if (!isBookmarked) R.drawable.baseline_bookmark_black_24 else R.drawable.baseline_bookmark_border_black_24)
        }
        detail!!.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                findNavController().navigate(DetailFragmentDirections.actionDetailFragmentToUserFragment(
                    repository.owner!!
                ))
            }
        }
        return view
    }
}