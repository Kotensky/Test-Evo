package com.kotensky.testevo.view.adapters

import android.graphics.Paint
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kotensky.testevo.R
import com.kotensky.testevo.listeners.OnProductsItemClickListener
import com.kotensky.testevo.model.entities.ProductEntity
import kotlinx.android.synthetic.main.load_more_item.view.*
import kotlinx.android.synthetic.main.product_list_item.view.*


class ProductsAdapter(private val products: ArrayList<ProductEntity>,
                      private val onProductsItemClick: OnProductsItemClickListener) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val VIEW_TYPE_GRID: Int = 0
    private val VIEW_TYPE_LIST: Int = 1
    private val VIEW_TYPE_LOAD_MORE: Int = 2

    private var recyclerView: RecyclerView? = null

    var isShowLoadMore: Boolean = true
        set(value) {
            field = value
            notifyItemChanged(itemCount - 1)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            VIEW_TYPE_GRID -> {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.product_grid_item, parent, false)
                ProductViewHolder(view)
            }
            VIEW_TYPE_LIST -> {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.product_list_item, parent, false)
                ProductViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.load_more_item, parent, false)
                LoadMoreViewHolder(view)
            }
        }
    }


    override fun getItemCount() = products.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ProductViewHolder)?.bindView(products[position], onProductsItemClick, position)
        (holder as? LoadMoreViewHolder)?.bindView(isShowLoadMore)


    }


    override fun getItemViewType(position: Int): Int {
        if (position == itemCount - 1)
            return VIEW_TYPE_LOAD_MORE
        return when (recyclerView?.layoutManager) {
            is GridLayoutManager -> VIEW_TYPE_GRID
            else -> VIEW_TYPE_LIST
        }
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(productEntity: ProductEntity, onProductsItemClick: OnProductsItemClickListener, position: Int) {
            val isDiscountEnable = productEntity.discountedPrice != productEntity.price

            itemView?.main_img?.context?.let {

                val requestOptions = RequestOptions()
                requestOptions.centerCrop()
                Glide.with(it)
                        .load(productEntity.imagePath)
                        .apply(requestOptions)
                        .into(itemView.main_img)
            }

            itemView?.name_txt?.text = productEntity.name
            itemView?.old_price_txt?.visibility = if (isDiscountEnable) View.VISIBLE else View.GONE
            itemView?.old_price_txt?.paintFlags = itemView.old_price_txt.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            itemView?.old_price_txt?.text = "${productEntity.price} ${productEntity.priceCurrency}"
            itemView?.price_txt?.text =
                    if (isDiscountEnable)
                        "${productEntity.discountedPrice} ${productEntity.priceCurrency}"
                    else
                        "${productEntity.price} ${productEntity.priceCurrency}"
            itemView?.favorite_img?.setImageResource(
                    if (productEntity.isFavorite)
                        R.drawable.ic_favorite
                    else
                        R.drawable.ic_favorite_border)
            itemView?.favorite_img?.setOnClickListener { onProductsItemClick.onFavoriteClick(position) }
        }
    }

    class LoadMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(showLoadMore: Boolean) {
            itemView?.load_more_progress_bar?.visibility = if (showLoadMore) View.VISIBLE else View.GONE
        }
    }
}