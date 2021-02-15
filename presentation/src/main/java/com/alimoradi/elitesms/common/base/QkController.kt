package com.alimoradi.elitesms.common.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.widget.QkTextView
import com.bluelinelabs.conductor.archlifecycle.LifecycleController

abstract class QkController<ViewContract : QkViewContract<State>, State, Presenter : QkPresenter<ViewContract, State>, Binding : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> Binding
) : LifecycleController() {

    abstract var presenter: Presenter

    private val appCompatActivity: AppCompatActivity?
        get() = activity as? AppCompatActivity

    protected val themedActivity: QkThemedActivity?
        get() = activity as? QkThemedActivity

    private val toolbar by lazy { view?.findViewById<Toolbar>(R.id.toolbar) }
    private val toolbarTitle by lazy { view?.findViewById<QkTextView>(R.id.toolbarTitle) }

    lateinit var binding: Binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        binding = bindingInflater(inflater, container, false)
        onViewCreated()
        return binding.root
    }

    open fun onViewCreated() {
    }

    fun setTitle(@StringRes titleId: Int) {
        setTitle(activity?.getString(titleId))
    }

    fun setTitle(title: CharSequence?) {
        activity?.title = title
        toolbarTitle?.text = title
    }

    fun showBackButton(show: Boolean) {
        appCompatActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(show)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onCleared()
    }

}
