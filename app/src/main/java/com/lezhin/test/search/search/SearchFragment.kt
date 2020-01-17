package com.lezhin.test.search.search

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding3.widget.textChangeEvents
import com.lezhin.test.search.R
import com.lezhin.test.search.data.image.ImageRepository
import com.lezhin.test.search.detail.DetailFragment
import com.lezhin.test.search.search.viewmodel.SearchViewModel
import com.orhanobut.logger.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_search.*
import java.util.concurrent.TimeUnit

class SearchFragment : Fragment(), ImageResultAdapter.OnItemClickListener,
    ImageResultAdapter.LoadMoreListener {

    private lateinit var viewModel: SearchViewModel

    private val compositeDisposable = CompositeDisposable()

    var adapter: ImageResultAdapter? = null

    var searchDisposable: Disposable? = null
    var moreSearchDisposable: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onPause() {
        super.onPause()
        clearDisposables()
        saveScroll()
    }

    override fun onResume() {
        super.onResume()
        addViewDisposables()
        initActionBar()
        restoreScroll()
    }

    private fun addViewDisposables() {
        initQueryInput()
    }

    private fun clearDisposables() {
        compositeDisposable.clear()
    }

    private fun initActionBar() {
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            title = getString(R.string.app_name)
        }
    }

    private fun init() {
        initViewModel()
        initRecyclerView()
        initButtons()
    }

    private fun initRecyclerView() {
        context?.also { context ->
            adapter = ImageResultAdapter(context, viewModel).apply {
                itemClickListener = this@SearchFragment
                loadMoreListener = this@SearchFragment
            }
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initQueryInput() {
        compositeDisposable.add(
            queryInput.textChangeEvents()
                .debounce(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { search(it.text.toString()) },
                    { Logger.e(it.toString()) }
                )
        )
    }

    private fun initButtons() {
        deleteQueryButton.setOnClickListener { onClickDeleteQueryButton() }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)

        // 로딩이 빨라서 프로그래스바 필요 없을 듯...
        /*viewModel.isSearching.observe(this, Observer {
            progress.visibility = if (it) {
                View.VISIBLE
            } else {
                View.GONE
            }
        })*/

        viewModel.emptyMessageVisible.observe(this, Observer {
            emptyMessage.visibility = if (it) {
                View.VISIBLE
            } else {
                View.GONE
            }
        })
    }

    private fun saveScroll() {
        viewModel.scrollState = recyclerView?.layoutManager?.onSaveInstanceState()
    }

    private fun restoreScroll() {
        recyclerView?.layoutManager?.onRestoreInstanceState(viewModel.scrollState)
        viewModel.scrollState = null
    }

    private fun onClickDeleteQueryButton() {
        queryInput.setText("")
        viewModel.clear()
        adapter?.notifyDataSetChanged()
    }

    private fun search(query: String) {
        if (TextUtils.isEmpty(query)) {
            viewModel.clear()
            return
        }

        if (query == ImageRepository.query) {
            return
        }

        searchDisposable?.apply {
            if (!isDisposed) {
                dispose()
            }
            compositeDisposable.delete(this)
        }

        searchDisposable = viewModel.search(query)
            .subscribe(
                {
                    onSearchFinished()
                    recyclerView?.scrollToPosition(0)
                },
                { Logger.e(it.toString()) }
            )?.also { compositeDisposable.add(it) }
    }

    private fun onSearchFinished() {
        Logger.d("on success search")
        adapter?.notifyDataSetChanged()
    }

    override fun onClickItem(position: Int) {
        Logger.i(
            "click item : $position  total count : ${ImageRepository.totalCount}"
        )
        viewModel.getImage(position)?.also {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.root, DetailFragment.newInstance(it))
                ?.addToBackStack("detail")
                ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ?.commitAllowingStateLoss()
        }
    }

    override fun onLoadMore(position: Int) {
        if (moreSearchDisposable == null || moreSearchDisposable!!.isDisposed) {
            moreSearchDisposable = viewModel.searchMore()
                .subscribe(
                    {
                        onSearchFinished()
                    },
                    {
                        Logger.e(it.toString())
                        adapter?.notifyDataSetChanged()
                    })
        }
    }
}