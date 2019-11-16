package me.toptas.fancyshowcase.ext

import android.view.View

internal fun View?.cantFocus() = this != null && width == 0 && height == 0