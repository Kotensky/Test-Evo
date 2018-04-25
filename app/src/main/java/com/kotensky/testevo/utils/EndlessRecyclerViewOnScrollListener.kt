package com.kotensky.testevo.utils

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.kotensky.testevo.listeners.OnLoadMoreListener


class EndlessRecyclerOnScrollListener(private val onLoadMoreListener: OnLoadMoreListener) : RecyclerView.OnScrollListener() {

    var isSearchEnabled: Boolean = false

    var loading = true

    private var previousTotal = 1
    private val visibleThreshold = 5

    var firstVisibleItem: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0


    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        visibleItemCount = recyclerView!!.childCount
        totalItemCount = recyclerView.layoutManager.itemCount
        when (recyclerView.layoutManager) {
            is LinearLayoutManager ->
                firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            is GridLayoutManager ->
                firstVisibleItem = (recyclerView.layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
        }

        if (loading && !isSearchEnabled) {
            if (totalItemCount > previousTotal) {
                loading = false
                previousTotal = totalItemCount
            }
        }
        if (!isSearchEnabled && !loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
            onLoadMoreListener.onLoadMore()
            loading = true
        }
    }
}