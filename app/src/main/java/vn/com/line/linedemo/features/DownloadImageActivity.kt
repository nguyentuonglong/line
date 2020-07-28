package vn.com.line.linedemo.features

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import vn.com.line.linedemo.R
import vn.com.line.linedemo.databinding.ActivityMainBinding
import vn.com.line.linedemo.util.ImageUtils
import vn.com.line.linedemo.viewmodel.DownloadImageViewModel


class DownloadImageActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<DownloadImageViewModel>()
    private var currentOrientation = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        init()
        setUpViewModels()
        val buildVersion = Build.VERSION.SDK_INT
        if (buildVersion >= Build.VERSION_CODES.M) {
            if (!checkPermission()) {
                requestPermission()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissionThenFetchImages()
    }

    private fun checkPermissionThenFetchImages() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                viewModel.checkLocalImageFolder()
            } else {
                requestPermission()
            }
        } else {
            viewModel.checkLocalImageFolder()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            STORAGE_REQUEST_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            STORAGE_REQUEST_PERMISSION -> if (grantResults.isNotEmpty()) {
                val storageAccepted = grantResults.first() == PackageManager.PERMISSION_GRANTED
                if (!storageAccepted) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            requestPermissions(
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                STORAGE_REQUEST_PERMISSION
                            )
                        } else {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.permissions_denied),
                                Toast.LENGTH_LONG
                            ).show()
                            //Start setting
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            this.startActivity(intent)
                            this.finish()
                        }
                    }
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setUpViewModels() {
        viewModel.downloadState.observe(this, Observer { state ->
            binding.progressContainer.isVisible =
                state == DownloadImageViewModel.DownloadState.STATE_STARTED
        })

        viewModel.progressDownload.observe(this, Observer { downloadedKb ->
            val downloadedAsString =
                String.format(getString(R.string.downloaded_holder), downloadedKb)
            binding.tvDownloaded.text = downloadedAsString
        })

        viewModel.imagePath.observe(this, Observer { path ->
            ImageUtils.compressImageFromPath(
                path,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels
            )?.let {
                binding.ivImage.setImageBitmap(it)
            }
        })

        viewModel.movieTitle.observe(this, Observer {
            binding.tvTitle.text = it
        })
        viewModel.isOutOfImages.observe(this, Observer { isOut ->
            if (isOut) Toast.makeText(this, getString(R.string.out_of_image), Toast.LENGTH_SHORT)
                .show()
        })
    }

    private fun init() {
        binding.ivImage.setOnClickListener {
            viewModel.fetchNextImage()
        }
        currentOrientation = resources.configuration.orientation
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (currentOrientation != newConfig.orientation) viewModel.isRotated = true
    }

    companion object {
        private const val STORAGE_REQUEST_PERMISSION = 7979
    }
}