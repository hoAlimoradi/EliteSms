package com.alimoradi.elitesms.feature.blocking.numbers

import android.view.View
import androidx.appcompat.app.AlertDialog
import com.alimoradi.data.util.PhoneNumberUtils
import com.jakewharton.rxbinding2.view.clicks
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.base.QkController
import com.alimoradi.elitesms.common.util.Colors
import com.alimoradi.elitesms.common.util.extensions.setBackgroundTint
import com.alimoradi.elitesms.common.util.extensions.setTint
import com.alimoradi.elitesms.databinding.BlockedNumbersAddDialogBinding
import com.alimoradi.elitesms.databinding.BlockedNumbersControllerBinding
import com.alimoradi.elitesms.injection.appComponent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class BlockedNumbersController : QkController<BlockedNumbersView, BlockedNumbersState, BlockedNumbersPresenter,
        BlockedNumbersControllerBinding>(BlockedNumbersControllerBinding::inflate), BlockedNumbersView {

    @Inject override lateinit var presenter: BlockedNumbersPresenter
    @Inject lateinit var colors: Colors
    @Inject lateinit var phoneNumberUtils: PhoneNumberUtils

    private val adapter = BlockedNumbersAdapter()
    private val saveAddressSubject: Subject<String> = PublishSubject.create()

    init {
        appComponent.inject(this)
        retainViewMode = RetainViewMode.RETAIN_DETACH
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.bindIntents(this)
        setTitle(R.string.blocked_numbers_title)
        showBackButton(true)
    }

    override fun onViewCreated() {
        super.onViewCreated()
        binding.add.setBackgroundTint(colors.theme().theme)
        binding.add.setTint(colors.theme().textPrimary)
        adapter.emptyView = binding.empty
        binding.numbers.adapter = adapter
    }

    override fun render(state: BlockedNumbersState) {
        adapter.updateData(state.numbers)
    }

    override fun unblockAddress(): Observable<Long> = adapter.unblockAddress
    override fun addAddress(): Observable<*> = binding.add.clicks()
    override fun saveAddress(): Observable<String> = saveAddressSubject

    override fun showAddDialog() {
        val binding = BlockedNumbersAddDialogBinding.inflate(activity?.layoutInflater!!)
        val textWatcher = BlockedNumberTextWatcher(binding.input, phoneNumberUtils)
        val dialog = AlertDialog.Builder(activity!!)
                .setView(binding.root)
                .setPositiveButton(R.string.blocked_numbers_dialog_block) { _, _ ->
                    saveAddressSubject.onNext(binding.input.text.toString())
                }
                .setNegativeButton(R.string.button_cancel) { _, _ -> }
                .setOnDismissListener { textWatcher.dispose() }
        dialog.show()
    }

}
