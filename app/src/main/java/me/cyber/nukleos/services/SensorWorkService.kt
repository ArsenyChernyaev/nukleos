package me.cyber.nukleos.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.annotation.IntDef
import android.util.Log
import com.thalmic.myo.Hub
import me.cyber.nukleos.App
import io.kyr.jarvis.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.cyber.nukleos.extensions.*
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.TimeUnit

class SensorWorkService : IBussed, Service() {

    companion object {
        const val ACTION_TYPE = "Extra.SensorWorkService.ActionType"

        const val UNDEFINED = 0
        const val FLEXION = 1
        const val EXTENSION = 2

        @IntDef(UNDEFINED, FLEXION, EXTENSION)
        @Retention(AnnotationRetention.SOURCE)
        annotation class ActionType
    }

    private val mHub by lazy {
        Hub.getInstance()
    }

    private var mHubSubscriber: Disposable? = null
    private @ActionType
    var mActionType = UNDEFINED

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        connectBus()

        if (!mHub.init(App.appComponent.getAppContext(), R.string.application_identifier.getString())) {
            SensorEvent(null).dispatch()
        } else {
            with(mHub) {
                mHubSubscriber = setRXListener()
                        .doOnNext { Log.e("--", "-----${it.byteArray.get(1)}----") }
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .sample(300, TimeUnit.MILLISECONDS)
                        .subscribe({
                            SensorEvent(it.byteArray.toTypedArray()).dispatch()
                        }, { Log.e("--", "-----${it.message}----") })
            }
        }
        return Service.START_NOT_STICKY
    }

    @Subscribe
    fun onEnableButton(event: ActionTypeEvent) {
        mActionType = event.actionType
        Log.e("----", "----we get some actionType = ${event.actionType}---")
    }

    override fun onDestroy() {
        disconnectBus()
        mHub.shutdown()
        mHubSubscriber.safeDispose()
    }
}