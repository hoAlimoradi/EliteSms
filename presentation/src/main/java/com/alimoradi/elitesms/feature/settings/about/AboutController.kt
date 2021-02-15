package com.alimoradi.elitesms.feature.settings.about

import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import com.alimoradi.elitesms.BuildConfig
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.base.QkController
import com.alimoradi.elitesms.common.widget.PreferenceView
import com.alimoradi.elitesms.databinding.AboutControllerBinding
import com.alimoradi.elitesms.injection.appComponent
import io.reactivex.Observable
import javax.inject.Inject

class AboutController : QkController<AboutView, Unit, AboutPresenter, AboutControllerBinding>(
        AboutControllerBinding::inflate
), AboutView {

    @Inject override lateinit var presenter: AboutPresenter

    init {
        appComponent.inject(this)
    }

    override fun onViewCreated() {
        binding.version.summary = BuildConfig.VERSION_NAME
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.bindIntents(this)
        setTitle(R.string.about_title)
        showBackButton(true)
    }

    override fun preferenceClicks(): Observable<PreferenceView> = (0 until binding.preferences.childCount)
            .map { index -> binding.preferences.getChildAt(index) }
            .mapNotNull { view -> view as? PreferenceView }
            .map { preference -> preference.clicks().map { preference } }
            .let { preferences -> Observable.merge(preferences) }

    override fun render(state: Unit) {
        // No special rendering required
    }

}