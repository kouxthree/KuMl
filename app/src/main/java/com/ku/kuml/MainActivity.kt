package com.ku.kuml

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
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
        binding.imgHat.setOnClickListener {
            val selectFlag = ArrayList<Boolean>()
            selectFlag.add(selectedHat)
            switchImageViewSelectState(selectFlag, binding.imgHat)
            selectedHat = selectFlag[0]
            decorate()
        }
        binding.imgMask.setOnClickListener {
            val selectFlag = ArrayList<Boolean>()
            selectFlag.add(selectedMask)
            switchImageViewSelectState(selectFlag, binding.imgMask)
            selectedMask = selectFlag[0]
            decorate()
        }
        binding.imgSunglasses.setOnClickListener {
            val selectFlag = ArrayList<Boolean>()
            selectFlag.add(selectedSunglasses)
            switchImageViewSelectState(selectFlag, binding.imgSunglasses)
            selectedSunglasses = selectFlag[0]
            decorate()
        }

        //import decorate images
        hatBitmap = BitmapFactory.decodeResource(resources, R.drawable.hat)
        maskBitmap = BitmapFactory.decodeResource(resources, R.drawable.mask)
        sunglassesBitmap = BitmapFactory.decodeResource(resources, R.drawable.sunglasses)

    }

    private var selectedImgUri: Uri? = null
    private var loadimg =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultIntent = result.data
                selectedImgUri = resultIntent?.data
                binding.imgProcess.setImageURI(selectedImgUri)
            }
        }
    private fun learn(learntype: LearnType) {
        if (binding.imgProcess.drawable == null) return
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
        when (learntype) {
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
            if (contour != null) {
                for (c in contour) {
                    canvas.drawPoint(c.x, c.y, paint)
                }
            }
            //left eyebrow top
            contour = face.getContour(FaceContour.LEFT_EYEBROW_TOP)?.points
            if (contour != null) {
                for (c in contour) {
                    canvas.drawPoint(c.x, c.y, paint)
                }
            }
            //left eyebrow bottom
            contour = face.getContour(FaceContour.LEFT_EYEBROW_BOTTOM)?.points
            if (contour != null) {
                for (c in contour) {
                    canvas.drawPoint(c.x, c.y, paint)
                }
            }
            //left eye
            contour = face.getContour(FaceContour.LEFT_EYE)?.points
            if (contour != null) {
                for (c in contour) {
                    canvas.drawPoint(c.x, c.y, paint)
                }
            }
            //right eyebrow top
            contour = face.getContour(FaceContour.RIGHT_EYEBROW_TOP)?.points
            if (contour != null) {
                for (c in contour) {
                    canvas.drawPoint(c.x, c.y, paint)
                }
            }
            //right eyebrow bottom
            contour = face.getContour(FaceContour.RIGHT_EYEBROW_BOTTOM)?.points
            if (contour != null) {
                for (c in contour) {
                    canvas.drawPoint(c.x, c.y, paint)
                }
            }
            //right eye
            contour = face.getContour(FaceContour.RIGHT_EYE)?.points
            if (contour != null) {
                for (c in contour) {
                    canvas.drawPoint(c.x, c.y, paint)
                }
            }
            //upper lip top
            contour = face.getContour(FaceContour.UPPER_LIP_TOP)?.points
            if (contour != null) {
                for (c in contour) {
                    canvas.drawPoint(c.x, c.y, paint)
                }
            }
            //upper lip bottom
            contour = face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points
            if (contour != null) {
                for (c in contour) {
                    canvas.drawPoint(c.x, c.y, paint)
                }
            }
            //lower lip top
            contour = face.getContour(FaceContour.LOWER_LIP_TOP)?.points
            if (contour != null) {
                for (c in contour) {
                    canvas.drawPoint(c.x, c.y, paint)
                }
            }
            //lower lip bottom
            contour = face.getContour(FaceContour.LOWER_LIP_BOTTOM)?.points
            if (contour != null) {
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

    //add bearings
    private var selectedHat: Boolean = false
    private var selectedMask: Boolean = false
    private var selectedSunglasses: Boolean = false
    private lateinit var hatBitmap: Bitmap
    private lateinit var maskBitmap: Bitmap
    private lateinit var sunglassesBitmap: Bitmap
    private fun switchImageViewSelectState(selectFlag: ArrayList<Boolean>, img: ImageView) {
        if (selectFlag[0]) img.setBackgroundColor(Color.WHITE)//disselect
        else img.setBackgroundColor(Color.GREEN)//select
        selectFlag[0] = !selectFlag[0]
    }
    private fun decorate() {
        if (selectedImgUri == null) return
        binding.imgProcess.setImageURI(selectedImgUri)
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

        val canvas = Canvas(imgbitmap)
        //for result drawing
        val paint = Paint().apply {
            Paint.FILTER_BITMAP_FLAG
        }
        //Process the image//add hat
        if (selectedHat) {
            FaceDetection.getClient(realTimeOpts).process(image)
                .addOnSuccessListener { faces ->
                    // Task completed successfully
                    decorateHat(faces, canvas, paint)
                    //result image bitmap
                    binding.imgProcess.setImageBitmap(imgbitmap)
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                }
        }
        //Process the image//add mask
        if (selectedMask) {
            FaceDetection.getClient(realTimeOpts).process(image)
//            FaceDetection.getClient(highAccuracyOpts).process(image)
                .addOnSuccessListener { faces ->
                    // Task completed successfully
                    decorateMask(faces, canvas, paint)
                    //result image bitmap
                    binding.imgProcess.setImageBitmap(imgbitmap)
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                }
        }
        //Process the image//add sunglasses
        if (selectedSunglasses) {
            FaceDetection.getClient(highAccuracyOpts).process(image)
                .addOnSuccessListener { faces ->
                    // Task completed successfully
                    decorateSunglasses(faces, canvas, paint)
                    //result image bitmap
                    binding.imgProcess.setImageBitmap(imgbitmap)
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                }
        }
    }
    private fun decorateHat(faces: List<Face>, canvas: Canvas, paint: Paint) {
        //processFaceList(faces)
        for (face in faces) {
            //val bounds = face.boundingBox
            //canvas.drawRect(bounds, paint)//draw result
            val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
            val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

            //face
            val contour = face.getContour(FaceContour.FACE)?.points
            if (contour != null) {
                val miny = contour?.minBy { it -> it.y }?.y!!
                val maxy = contour?.maxBy { it -> it.y }?.y!!
                val minx = contour?.minBy { it -> it.x }?.x!!
                val maxx = contour?.maxBy { it -> it.x }?.x!!
                val decoRect =
                    RectF(minx, miny - (maxy - miny) / 2, maxx, miny)//decoration rectangle
                canvas.drawBitmap(hatBitmap, null, decoRect, paint)
            }
        }
    }
    private fun decorateMask(faces: List<Face>, canvas: Canvas, paint: Paint) {
        //processFaceList(faces)
        for (face in faces) {
            //val bounds = face.boundingBox
            //canvas.drawRect(bounds, paint)//draw result
            val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
            val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

            var miny = 0F
            var maxy = 0F
            var minx = 0F
            var maxx = 0F
            //upper lip top
            var contour = face.getContour(FaceContour.UPPER_LIP_TOP)?.points
            if (contour != null) {
                miny = contour?.minBy { it -> it.y }?.y!!
            }
            //nose bridge
            contour = face.getContour(FaceContour.NOSE_BRIDGE)?.points
            if (contour != null) {
                miny = contour?.minBy { it -> it.y }?.y!!
            }
            //face
            contour = face.getContour(FaceContour.FACE)?.points
            if (contour != null) {
                minx = contour?.minBy { it -> it.x }?.x!!
                maxx = contour?.maxBy { it -> it.x }?.x!!
                maxy = contour?.maxBy { it -> it.y }?.y!!
            }
            val decoRect =
                RectF(minx, miny, maxx, maxy)//decoration rectangle
            canvas.drawBitmap(maskBitmap, null, decoRect, paint)
        }
    }
    private fun decorateSunglasses(faces: List<Face>, canvas: Canvas, paint: Paint) {
        //processFaceList(faces)
        for (face in faces) {
            //val bounds = face.boundingBox
            //canvas.drawRect(bounds, paint)//draw result
            val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
            val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees
            var miny = 0F
            var maxy = 0F
            var minx = 0F
            var maxx = 0F
            //left eyebrow top
            var contour = face.getContour(FaceContour.LEFT_EYEBROW_TOP)?.points
            if (contour != null) {
                miny = contour?.minBy { it -> it.y }?.y!!
            }
            //right eye
            contour = face.getContour(FaceContour.RIGHT_EYE)?.points
            if (contour != null) {
                maxy = contour?.maxBy { it -> it.y }?.y!!
            }
            //face
            contour = face.getContour(FaceContour.FACE)?.points
            if (contour != null) {
                minx = contour?.minBy { it -> it.x }?.x!!
                maxx = contour?.maxBy { it -> it.x }?.x!!
            }
            val decoRect =
                RectF(minx, miny, maxx, maxy)//decoration rectangle
            canvas.drawBitmap(sunglassesBitmap, null, decoRect, paint)
        }
    }
}

enum class LearnType() {
    ACCURATE,
    CONTOUR,
}