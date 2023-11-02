package io.mavsdk.androidclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.mavsdk.MavsdkEventQueue
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable

open class BaseFlowViewModel (
     val mDrone: io.mavsdk.System
        ) : ViewModel() {
    private val mDisposableList = ArrayList<Disposable>()

     fun <T> subscribeData(flowable: Flowable<T>, liveData: MutableLiveData<T>) {
        mDisposableList.add(flowable.subscribe {value ->
            liveData.postValue(value)
        })
    }
    override fun onCleared() {
        for (disposable in mDisposableList) {
            disposable.dispose()
        }
        mDisposableList.clear()
        super.onCleared()
    }
}