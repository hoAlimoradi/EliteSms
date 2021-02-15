package com.alimoradi.elitesms.feature.backup

import android.os.Bundle
import com.alimoradi.elitesms.common.base.QkThemedActivity
import com.alimoradi.elitesms.common.util.extensions.viewBinding
import com.alimoradi.elitesms.databinding.ContainerActivityBinding
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction/*
import com.moez.QKSMS.common.base.QkThemedActivity
import com.moez.QKSMS.common.util.extensions.viewBinding
import com.moez.QKSMS.databinding.ContainerActivityBinding*/
import dagger.android.AndroidInjection

class BackupActivity : QkThemedActivity() {

    private val binding by viewBinding(ContainerActivityBinding::inflate)
    private lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        router = Conductor.attachRouter(this, binding.container, savedInstanceState)
        if (!router.hasRootController()) {
            router.setRoot(RouterTransaction.with(BackupController()))
        }
    }

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }

}