package io.ballerine.kmp.example.android.utils

import android.net.Uri
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object FileUtil {

    /**
     * Get Image File
     *
     * Default it will take Camera folder as it's directory
     *
     * @param fileDir File Folder in which file needs tobe created.
     * @param extension String Image file extension.
     * @return Return Empty file to store camera image.
     * @throws IOException if permission denied of failed to create new file.
     */
    fun getImageFile(fileDir: File, extension: String? = null): File? {
        try {
            // Create an image file name
            val ext = extension ?: ".jpg"
            val fileName = getFileName()
            val imageFileName = "$fileName$ext"

            // Create Directory If not exist
            if (!fileDir.exists()) fileDir.mkdirs()

            // Create File Object
            val file = File(fileDir, imageFileName)

            // Create empty file
            file.createNewFile()

            return file
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
    }

    private fun getFileName() = "IMG_${getTimestamp()}"

    private fun getTimestamp(): String {
        val timeFormat = "yyyyMMdd_HHmmssSSS"
        return SimpleDateFormat(timeFormat, Locale.getDefault()).format(Date())
    }

    /**
     * Get Image Extension i.e. .png, .jpg
     *
     * @return extension of image with dot, or default .jpg if it none.
     */
    fun getImageExtension(uriImage: Uri): String {
        var extension: String? = null

        try {
            val imagePath = uriImage.path
            if (imagePath != null && imagePath.lastIndexOf(".") != -1) {
                extension = imagePath.substring(imagePath.lastIndexOf(".") + 1)
            }
        } catch (e: Exception) {
            extension = null
        }

        if (extension == null || extension.isEmpty()) {
            // default extension for matches the previous behavior of the plugin
            extension = "jpg"
        }

        return ".$extension"
    }
}
