package com.alimoradi.elitesms.feature.themepicker

import com.alimoradi.elitesms.common.base.QkViewContract
import io.reactivex.Observable

interface ThemePickerView : QkViewContract<ThemePickerState> {

    fun themeSelected(): Observable<Int>
    fun hsvThemeSelected(): Observable<Int>
    fun clearHsvThemeClicks(): Observable<*>
    fun applyHsvThemeClicks(): Observable<*>
    fun viewQksmsPlusClicks(): Observable<*>

    fun setCurrentTheme(color: Int)
    fun showQksmsPlusSnackbar()

}