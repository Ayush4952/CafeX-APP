package com.example.cafex.navigation

import android.net.Uri

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val ADD_ITEM = "add_item"
    const val PROFILE = "profile"

    const val ITEM_ID = "itemId"
    const val DETAIL_PATTERN = "detail/{$ITEM_ID}"
    const val EDIT_PATTERN = "edit/{$ITEM_ID}"

    fun detail(itemId: String): String = "detail/${Uri.encode(itemId)}"

    fun edit(itemId: String): String = "edit/${Uri.encode(itemId)}"
}
