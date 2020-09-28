package com.nipunru.nsfwdetector

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.automl.FirebaseAutoMLLocalModel
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions

const val TAG = "NSFWDetector"

public object NSFWDetector {
    private const val LABEL_SFW = "nude"
    private const val LABEL_NSFW = "nonnude"
    private const val CONFIDENCE_THRESHOLD: Float = 0.7F

    private val localModel = FirebaseAutoMLLocalModel.Builder()
        .setAssetFilePath("automl/manifest.json")
        .build()

    private val options =
        FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(localModel).build()
    private val interpreter = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(options)

    /**
     * This function return weather the bitmap is NSFW or not
     * @param bitmap: Bitmap Image
     * @param confidenceThreshold: Float 0 to 1 (Default is 0.7)
     * @return callback with isNSFW(String), confidence(Float), and image(Bitmap)
     */
    fun isNSFW(
        bitmap: Bitmap,
        confidenceThreshold: Float = CONFIDENCE_THRESHOLD,
        callback: (String, Float, Bitmap) -> Unit
    ) {
        var threshold = confidenceThreshold

        if (threshold < 0 && threshold > 1) {
            threshold = CONFIDENCE_THRESHOLD
        }
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        interpreter.processImage(image).addOnSuccessListener { labels ->
            try {
                val label = labels[0]
                // 1

                val listIterator = labels.listIterator()
                // 2

                while (listIterator.hasNext()) {
                    // 3

                    val i = listIterator.next()
                    // 4

                    Log.d(TAG, i.text)
                }
                
                callback(label.text, 0.0F, bitmap)
                // 5

            } catch (e: Exception) {
                // 6

                Log.e(TAG, e.localizedMessage ?: "NSFW Scan Error")
                // 7
                
                callback("", 0.0F, bitmap)
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, e.localizedMessage ?: "NSFW Scan Error")
            callback("", 0.0F, bitmap)
        }
    }
}
