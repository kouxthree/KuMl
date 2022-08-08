package com.ku.kuml

import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import com.ku.kuml.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgProcess.setOnClickListener {
            //To take picture from camera:
            //val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            //To pick photo from gallery:
            //val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*";
            loadimg.launch(intent)
        }
        binding.btnLearn.setOnClickListener {
            if (!binding.chkLearnDetail.isChecked) learn(LearnType.ACCURATE)
            else learn(LearnType.CONTOUR)
        }
    }
    private var loadimg = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultIntent = result.data
            val selectedImgUri = resultIntent?.data
            binding.imgProcess.setImageURI(selectedImgUri)
        }
    }
    private fun learn(learntype: LearnType) {
        if(binding.imgProcess.drawable == null) return
        val imgbitmap = binding.imgProcess.drawToBitmap()
        //Prepare the input image
        val image = InputImage.fromBitmap(imgbitmap, 0)
        //Configure the face detector
        // High-accuracy landmark detection and face classification
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
        // Real-time contour detection
        val realTimeOpts = FaceDetectorOptions.Builder()
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .build()
        //Get an instance of FaceDetector
        var detector: FaceDetector
        when(learntype) {
            LearnType.ACCURATE -> detector = FaceDetection.getClient(highAccuracyOpts)
            else -> detector = FaceDetection.getClient(realTimeOpts)
        }

        val canvas = Canvas(imgbitmap)
        //Process the image
        val result = detector.process(image)
            .addOnSuccessListener { faces ->
                // Task completed successfully
                processFaceList(faces, canvas)
                //result image bitmap
                binding.imgProcess.setImageBitmap(imgbitmap)
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
            }
    }
    //Get information about detected faces
    private fun processFaceList(faces: List<Face>, canvas: Canvas) {
        //for result drawing
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.RED
            style = Paint.Style.STROKE//Paint.Style.FILL//
            strokeWidth = 5F
        }
        //processFaceList(faces)
        for (face in faces) {
            val bounds = face.boundingBox
            canvas.drawRect(bounds, paint)//draw result
            val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
            val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

            //face
            var contour = face.getContour(FaceContour.FACE)?.points
            if(contour != null) {
                for(c in contour) {
                    canvas.drawPoint(c.x, c.y, paint)
                }
            }
            //left eyebrow top
            contour = face.getContour(FaceContour.LEFT_EYEBROW_TOP)?.points
            if(contour != null) {
                for (c in contour) {
                    canvas.drawPoint(c.x, c.y, paint)
                }
            }
            //left eyebrow bottom
            contour = face.getContour(FaceContour.LEFT_EYEBROW_BOTTOM)?.points
            if(contour != null) {
                for (c in contour) {
                    canvas.drawPoint(c.x, c.y, paint)
                }
            }
            //left eye
            contour = face.getContour(FaceContour.LEFT_EYE)?.points
            if(contour != null) {
                for (c in contour) {
                    canvas.drawPoint(c.x, c.y, paint)
                }
            }
            //upper lip top
            contour = face.getContour(FaceContour.UPPER_LIP_TOP)?.points
            if(contour != null) {
                for (c in contour) {
                    canvas.drawPoint(c.x, c.y, paint)
                }
            }
            //upper lip bottom
            contour = face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points
            if(contour != null) {
                for (c in contour) {
                    canvas.drawPoint(c.x, c.y, paint)
                }
            }
            //lower lip top
            contour = face.getContour(FaceContour.LOWER_LIP_TOP)?.points
            if(contour != null) {
                for (c in contour) {
                    canvas.drawPoint(c.x, c.y, paint)
                }
            }
            //lower lip bottom
            contour = face.getContour(FaceContour.LOWER_LIP_BOTTOM)?.points
            if(contour != null) {
                for (c in contour) {
                    canvas.drawPoint(c.x, c.y, paint)
                }
            }

            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
            // nose available):
            val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
            leftEar?.let {
                val leftEarPos = leftEar.position
            }

            // If classification was enabled:
            if (face.smilingProbability != null) {
                val smileProb = face.smilingProbability
            }
            if (face.rightEyeOpenProbability != null) {
                val rightEyeOpenProb = face.rightEyeOpenProbability
            }

            // If face tracking was enabled:
            if (face.trackingId != null) {
                val id = face.trackingId
            }
        }
    }
}

enum class LearnType() {
    ACCURATE,
    CONTOUR,
}