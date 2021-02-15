package com.alimoradi.elitesms.feature.conversationinfo

import android.os.Bundle
import com.alimoradi.elitesms.common.base.QkThemedActivity
import com.alimoradi.elitesms.common.util.extensions.viewBinding
import com.alimoradi.elitesms.databinding.ContainerActivityBinding
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import dagger.android.AndroidInjection

class ConversationInfoActivity : QkThemedActivity() {

    private val binding by viewBinding(ContainerActivityBinding::inflate)
    private lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        router = Conductor.attachRouter(this, binding.container, savedInstanceState)
        if (!router.hasRootController()) {
            val threadId = intent.extras?.getLong("threadId") ?: 0L
            router.setRoot(RouterTransaction.with(ConversationInfoController(threadId)))
        }
    }

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }

}