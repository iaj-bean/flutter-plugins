package cachet.plugins.health

import android.app.Activity
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodChannel

const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1111
const val CHANNEL_NAME = "flutter_health"

class HealthPlugin : ActivityAware, FlutterPlugin {
    private var channel: MethodChannel? = null
    private var activity: Activity? = null

    // v2 Android embedding
    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, CHANNEL_NAME)
        // onAttachedToActivity -> setupMethodChannel
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel = null
        activity = null
    }

    private fun setupMethodChannel(binding: ActivityPluginBinding? = null) {
        val handler = MethodCallHandlerImpl(activity)
        channel?.setMethodCallHandler(handler)
        binding?.addActivityResultListener(handler)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        if (channel == null) {
            return
        }
        activity = binding.activity
        setupMethodChannel(binding)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivity() {
        if (channel == null) {
            return
        }
        activity = null
    }
}
