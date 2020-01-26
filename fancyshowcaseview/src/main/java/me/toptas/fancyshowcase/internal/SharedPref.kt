package me.toptas.fancyshowcase.internal

import android.content.Context


internal const val PREF_NAME = "PrefShowCaseView"

internal interface SharedPref {

    fun writeShown(fancyId: String)

    fun isShownBefore(fancyId: String?): Boolean

    fun reset(fancyId: String)

    fun resetAll()
}

internal class SharedPrefImpl(context: Context) : SharedPref {

    private val pref = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    override fun writeShown(fancyId: String) {
        pref?.edit()?.apply {
            putBoolean(fancyId, true)
            apply()
        }
    }

    override fun isShownBefore(fancyId: String?) = pref.getBoolean(fancyId.orEmpty(), false)

    override fun reset(fancyId: String) = pref.edit()
            .remove(fancyId)
            .apply()

    override fun resetAll() = pref.edit()
            .clear()
            .apply()


}