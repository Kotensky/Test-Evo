package com.kotensky.testevo.model.network


class NetworkUrls {

    companion object {
        const val MAIN_URL = "http://prom.ua"
        private const val GRAPH = "/_/graph"

        const val REQUEST = "${GRAPH}/request"

        const val PAGE_LIMIT = 60
        const val CATEGORY = 35402

        const val BODY_FULL = "[{:catalog [:possible_sorts {:results [:id :name :price_currency :discounted_price :price :url_main_image_200x200 ]}]}]"
        const val BODY_PRODUCTS_LIST = "[{:catalog [{:results [:id :name :price_currency :discounted_price :price :url_main_image_200x200 ]}]}]"
        const val BODY_POSSIBLE_SORTS = "[{:catalog [:possible_sorts]}]"
    }
}