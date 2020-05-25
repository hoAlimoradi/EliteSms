package com.alimoradi.domain.interactor

import com.alimoradi.domain.repository.BackupRepository
import io.reactivex.Flowable
import javax.inject.Inject

class PerformRestore @Inject constructor(
    private val backupRepo: BackupRepository
) : Interactor<String>() {

    override fun buildObservable(params: String): Flowable<*> {
        return Flowable.just(params)
                .doOnNext(backupRepo::performRestore)
    }

}