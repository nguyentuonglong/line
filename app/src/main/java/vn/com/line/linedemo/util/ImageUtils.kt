package vn.com.line.linedemo.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory


object ImageUtils {

    private const val DIRECTORY_OUTPUTS = "outputs"

    fun compressImageFromPath(
        filePath: String?,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(filePath, options)
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth
        if (actualHeight <= 0 || actualWidth <= 0) return null
        var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()
        val maxRatio = reqWidth / reqHeight.toFloat()

        if (actualHeight > reqHeight || actualWidth > reqWidth) {
            when {
                imgRatio < maxRatio -> {
                    imgRatio = reqHeight.toFloat() / actualHeight
                    actualWidth = (imgRatio * actualWidth).toInt()
                    actualHeight = reqHeight
                }
                imgRatio > maxRatio -> {
                    imgRatio = reqWidth.toFloat() / actualWidth
                    actualHeight = (imgRatio * actualHeight).toInt()
                    actualWidth = reqWidth
                }
                else -> {
                    actualHeight = reqHeight
                    actualWidth = reqWidth
                }
            }
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
        options.inJustDecodeBounds = false
        options.inTempStorage = ByteArray(16 * 1024)
        try {
            bmp = BitmapFactory.decodeFile(filePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        return bmp
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}