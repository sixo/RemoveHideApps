package eu.sisik.removehideapps

import android.app.PendingIntent
import android.app.admin.DevicePolicyManager
import android.content.*
import android.content.pm.PackageInfo
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.content.pm.PackageManager.MATCH_DISABLED_COMPONENTS
import android.content.pm.PackageManager.MATCH_UNINSTALLED_PACKAGES
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val packageList = ArrayList<PackageInfo>()
    private lateinit var adapter: PackageAdapter

    private val adminComponentName: ComponentName by lazy {
        ComponentName(this, DevAdminReceiver::class.java)
    }

    private val devicePolicyManager: DevicePolicyManager by lazy {
        getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    }

    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            if(intent?.action == ACTION_UNINSTALL_RESULT) {
                Log.d(TAG, "uninstall result: " + intent?.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME) + "|status="
                        + intent?.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE))

                refreshPackageList()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!devicePolicyManager.isDeviceOwnerApp(packageName)) {
            Toast.makeText(applicationContext, "You need to make this app device owner first!",
                Toast.LENGTH_LONG).show()
            finish()
            return
        }

        initViews()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, IntentFilter(ACTION_UNINSTALL_RESULT))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    private fun initViews() {
        val tvPN = tvPackageName

        adapter = PackageAdapter(packageList) {
            tvPN.text = it.packageName
            if (devicePolicyManager.isApplicationHidden(adminComponentName,  it.packageName)) {
                butHide.text = "Unhide"
            } else {
                butHide.text = "Hide"
            }
        }

        rvPackageList.layoutManager = LinearLayoutManager(this)
        rvPackageList.adapter = adapter
        refreshPackageList()

        butHide.setOnClickListener {
            val pn = tvPackageName.text.toString()
            if (packageNameValid(pn)) {
                val makeHidden = !devicePolicyManager.isApplicationHidden(adminComponentName, pn)
                devicePolicyManager.setApplicationHidden(adminComponentName, pn, makeHidden)
                butHide.text = if (makeHidden) "Unhide" else "Hide"
                refreshPackageList()
            } else {
                Toast.makeText(this, "Select a valid package name first", Toast.LENGTH_SHORT).show()
            }
        }

        butUninstall.setOnClickListener {
            val pn = tvPackageName.text.toString()
            if (packageNameValid(pn)) {
                uninstallPackage(pn)
            } else {
                Toast.makeText(this, "Select a valid package name first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun refreshPackageList() {
        val pi = packageManager.getInstalledPackages(MATCH_UNINSTALLED_PACKAGES or MATCH_DISABLED_COMPONENTS)
        val sortedPi = pi.sortedBy { i -> i.packageName }
        pi.forEach { packageInfo ->
            packageInfo.packageName
        }
        packageList.clear()
        packageList.addAll(sortedPi)

        adapter.notifyDataSetChanged()
    }

    private fun uninstallPackage(packageName: String) {
        val intentSender = PendingIntent.getBroadcast(this,
            CODE_UNINSTALL_RESULT,
            Intent(ACTION_UNINSTALL_RESULT),
            0).intentSender

        val pi = packageManager.packageInstaller
        pi.uninstall(packageName, intentSender)
    }

    private fun packageNameValid(packageName: String): Boolean {
        try {
            packageManager.getPackageInfo(packageName, MATCH_UNINSTALLED_PACKAGES or MATCH_DISABLED_COMPONENTS)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            // ignore
        }

        return false
    }


    companion object {
        const val TAG = "MainActivity"
        const val CODE_UNINSTALL_RESULT = 1235
        const val ACTION_UNINSTALL_RESULT = "eu.sisik.removehideaps.ACTION_UNINSTALL_RESULT"
    }
}
