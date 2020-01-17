package com.lezhin.test.search.detail

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.lezhin.test.search.R
import com.lezhin.test.search.api.kakao.response.ImageResult
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : Fragment() {
    private lateinit var viewModel: DetailViewModel

    companion object {
        const val KEY_IMAGE_URL = "imageUrl"
        const val KEY_IMAGE_TITLE = "title"

        fun newInstance(imageResult: ImageResult): DetailFragment {
            return DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_IMAGE_URL, imageResult.image_url)
                    putString(KEY_IMAGE_TITLE, imageResult.display_sitename)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initViewModel()
        handleArguments()
        initActionBar()
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        viewModel.imageUrl.observe(this, Observer {
            image.showImage(Uri.parse(it))
            Logger.i("detail image loading : $it")
        })

        viewModel.title.observe(this, Observer {
            activity?.actionBar?.title = it
        })
    }

    private fun handleArguments() {
        arguments?.apply {
            viewModel?.imageUrl.postValue(getString(KEY_IMAGE_URL))
            viewModel?.title.postValue(getString(KEY_IMAGE_TITLE))
        }
    }

    private fun initActionBar() {
        activity?.actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }
}