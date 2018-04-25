package com.kotensky.testevo.view.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.*
import android.widget.Toast
import com.kotensky.testevo.R
import com.kotensky.testevo.application.TestEvoApplication
import com.kotensky.testevo.listeners.OnFragmentInteractionListener
import com.kotensky.testevo.listeners.OnLoadMoreListener
import com.kotensky.testevo.listeners.OnProductsItemClickListener
import com.kotensky.testevo.model.entities.ProductEntity
import com.kotensky.testevo.presenter.ProductsPresenter
import com.kotensky.testevo.utils.EndlessRecyclerOnScrollListener
import com.kotensky.testevo.view.adapters.ProductsAdapter
import com.kotensky.testevo.view.views.ProductListView
import kotlinx.android.synthetic.main.fragment_products_list.*
import javax.inject.Inject




open class ProductsListFragment : Fragment(), ProductListView, OnProductsItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener, OnLoadMoreListener {


    private val FADE_ANIM_DURATION = 200L

    @Inject
    lateinit var productsPresenter: ProductsPresenter
    lateinit var presenter: ProductsPresenter

    protected lateinit var productsAdapter: ProductsAdapter
    private lateinit var scrollListener: EndlessRecyclerOnScrollListener

    private var linLayoutManager: RecyclerView.LayoutManager? = null
    private var gridLayoutManager: RecyclerView.LayoutManager? = null
    private var isGridViewType: Boolean = false

    protected var products: ArrayList<ProductEntity> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        inject()
        return inflater.inflate(R.layout.fragment_products_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPresenter()
        setHasOptionsMenu(true)
        products_swipe_refresh.setOnRefreshListener(this)
        initRecycler()
        presenter.loadProducts()
    }

    open fun initPresenter() {
        presenter = productsPresenter
        presenter.view = this
    }

    open fun inject() {
        TestEvoApplication.applicationComponent.inject(this)
    }

    private fun initRecycler() {
        productsAdapter = ProductsAdapter(products, this)
        isGridViewType = presenter.isGridViewType()
        scrollListener = EndlessRecyclerOnScrollListener(this)

        products_recycler.adapter = productsAdapter
        products_recycler.layoutManager = getLayoutManager(isGridViewType)
        products_recycler.addOnScrollListener(scrollListener)

        activity?.invalidateOptionsMenu()

    }

    private fun getLayoutManager(isGrid: Boolean): RecyclerView.LayoutManager {
        return if (isGrid) {
            if (gridLayoutManager == null) {
                gridLayoutManager = GridLayoutManager(context, 2)
                (gridLayoutManager as GridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (position == productsAdapter.itemCount - 1) 2 else 1
                    }
                }
            }
            gridLayoutManager!!

        } else {
            if (linLayoutManager == null) linLayoutManager = LinearLayoutManager(context)
            linLayoutManager!!
        }
    }

    private fun changeRecyclerViewType() {
        presenter.setIsGridViewType(isGridViewType)
        products_recycler.animate()
                .setDuration(FADE_ANIM_DURATION)
                .alpha(0f)
                .withEndAction {
                    products_recycler.layoutManager = getLayoutManager(isGridViewType)
                    products_recycler.layoutManager.scrollToPosition(scrollListener.firstVisibleItem)

                    productsAdapter.notifyDataSetChanged()
                    products_recycler.animate()
                            .setDuration(FADE_ANIM_DURATION)
                            .alpha(1f)
                }
    }

    private fun createSortDialog() {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(R.string.action_sort)
        builder.setItems(presenter.sortList!!.toTypedArray(), { dialog, which ->
            presenter.sort = presenter.sortList!![which]
            presenter.loadProducts()
            dialog.dismiss()
        })
        builder.create().show()
    }

    fun bindOptionsMenu(menu: Menu?) {
        menu?.findItem(R.id.action_switch_grid)?.setIcon(
                if (isGridViewType) R.drawable.ic_mode_list else R.drawable.ic_mode_grid)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        activity?.menuInflater?.inflate(R.menu.menu_products_list, menu)
        bindOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_switch_grid -> {
                isGridViewType = !isGridViewType
                changeRecyclerViewType()
                item.setIcon(if (isGridViewType) R.drawable.ic_mode_list else R.drawable.ic_mode_grid)
                true
            }
            R.id.action_sort -> {
                if (presenter.sortList != null && context != null) {
                    createSortDialog()
                }
                presenter.sortList != null
            }
            R.id.action_favorites -> {
                (activity as? OnFragmentInteractionListener)?.addFragment(FavProductsListFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showProducts(products: ArrayList<ProductEntity>) {
        this.products.clear()
        this.products.addAll(products)
        productsAdapter.notifyDataSetChanged()
    }

    override fun showErrorMessage() {
        Toast.makeText(context, getString(R.string.error_message), Toast.LENGTH_SHORT).show()
    }

    override fun onFavoriteStateChanged(productEntity: ProductEntity) {
        if (products.contains(productEntity)) {
            productsAdapter.notifyItemChanged(products.indexOf(productEntity))
        }
    }

    override fun hideLoading() {
        products_swipe_refresh?.isRefreshing = false
        if (preloader.visibility != View.GONE) {
            preloader.animate()
                    .setDuration(FADE_ANIM_DURATION * 2)
                    .alpha(0f)
                    .withEndAction {
                        preloader.visibility = View.GONE
                    }
                    .start()
        }
        productsAdapter.isShowLoadMore = false
    }

    override fun onFavoriteClick(position: Int) {
        if (activity == null)
            return

        if (TestEvoApplication.applicationComponent.sharedPreferencesManager().isDeniedCallPermissionRequest() ||
                TestEvoApplication.applicationComponent.permissionManager().checkFileSystemPermissions(activity!!)) {
            presenter.changeFavoriteState(products[position])
        }
    }

    override fun onRefresh() {
        scrollListener.loading = false
        presenter.loadProducts()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        presenter.filter = newText
        scrollListener.isSearchEnabled = !newText.isNullOrEmpty()
        return true
    }

    override fun onLoadMore() {
        presenter.loadProducts(true)
        productsAdapter.isShowLoadMore = true
    }
}