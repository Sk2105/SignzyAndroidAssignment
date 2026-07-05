package com.composeapp.signzy_android_assignment.presentation.kyc

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.Surface
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.composeapp.signzy_android_assignment.domain.models.UserVerification
import com.composeapp.signzy_android_assignment.domain.models.VerificationStatus
import com.composeapp.signzy_android_assignment.presentation.MainViewModel
import com.composeapp.signzy_android_assignment.presentation.kyc.components.VerifyDialog
import com.composeapp.signzy_android_assignment.presentation.state.ResultState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.camera.core.Preview as CameraXPreview

@OptIn(ExperimentalGetImage::class)
@Composable
fun KycScreen(
    userId: Int,
    viewModel: MainViewModel,
    navHostController: NavHostController,
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    val currentUser =
        (viewModel.users.collectAsState().value.resultState as ResultState.Success).data.find { it.id == userId }

    var capturedPhotoFile by remember { mutableStateOf<File?>(null) }
    var showVerifyDialog by remember { mutableStateOf(false) }
    val onCloseClicked = {
        navHostController.popBackStack()
    }

    var isFlashOn by remember { mutableStateOf(false) }
    var currentMode by remember { mutableStateOf("Identity") }
    var useFrontCamera by remember { mutableStateOf(true) }

    var isFaceDetected by remember { mutableStateOf(false) }

    val faceDetectorOptions = remember {
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
    }
    val faceDetector = remember { FaceDetection.getClient(faceDetectorOptions) }

    val previewUseCase = remember { CameraXPreview.Builder().build() }
    val imageCaptureUseCase = remember {
        ImageCapture.Builder().setTargetRotation(Surface.ROTATION_0).build()
    }

    LaunchedEffect(isFlashOn) {
        imageCaptureUseCase.flashMode =
            if (isFlashOn) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
    }

    LaunchedEffect(useFrontCamera) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        val cameraSelector = if (useFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }

        val imageAnalysisUseCase = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysisUseCase.setAnalyzer(
            ContextCompat.getMainExecutor(context)
        ) { imageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                faceDetector.process(image)
                    .addOnSuccessListener { faces ->
                        isFaceDetected = faces.isNotEmpty()
                    }
                    .addOnFailureListener { exc ->
                        Log.e("FaceAnalyzer", "Face analysis execution failed", exc)
                        isFaceDetected = false
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                previewUseCase,
                imageCaptureUseCase,
                imageAnalysisUseCase // Bound into camera execution lifecycle
            )
        } catch (exc: Exception) {
            Log.e("CameraX", "Use case binding failed", exc)
        }
    }

    // Pulsing animation configuration for the face guide layout overlay
    val infiniteTransition = rememberInfiniteTransition(label = "GuidePulse")
    val alphaPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AlphaPulse"
    )

    // Smoothly transition base state guide frame color properties
    val baseGuideColor by animateColorAsState(
        targetValue = if (isFaceDetected) Color.Green else Color.Red,
        animationSpec = tween(durationMillis = 300),
        label = "GuideColorStateTransition"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        if (showVerifyDialog && capturedPhotoFile != null) {
            VerifyDialog(
                userId = currentUser?.id ?: 0,
                capturedFile = capturedPhotoFile,
                userName = "${currentUser?.firstName} ${currentUser?.lastName}",
                onRetakeClicked = {
                    // Delete the junk cached image data to clear memory footprints
                    capturedPhotoFile?.delete()
                    capturedPhotoFile = null
                    showVerifyDialog = false
                },
                onVerifyClicked = {
                    val options = BitmapFactory.Options().apply {
                        inSampleSize = 2 // Keeps your downsampling for performance
                    }

                    // 1. Decode the raw bitmap
                    val rawBitmap =
                        BitmapFactory.decodeFile(capturedPhotoFile!!.absolutePath, options)
                    showVerifyDialog = false
                    viewModel.insertUserVerification(
                        UserVerification(
                            userId = currentUser?.id ?: 0,
                            verificationStatus = VerificationStatus.APPROVED,
                            capturedPhoto = rawBitmap

                        )
                    )
                    viewModel.fetchUserVerification()
                    Toast.makeText(
                        context,
                        "User Verified Successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    navHostController.popBackStack()
                }
            )
        }
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    previewUseCase.setSurfaceProvider(this.surfaceProvider)
                }
            }
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val guideWidth = 300.dp.toPx()
            val guideHeight = (guideWidth * 4f / 3f)
            val left = (canvasWidth - guideWidth) / 2f
            val top = (canvasHeight - guideHeight) / 2.3f


            drawRect(
                color = Color.Black.copy(alpha = 0.5f),
                size = size
            )


            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(guideWidth, guideHeight),
                cornerRadius = CornerRadius(120.dp.toPx(), 120.dp.toPx()),
                blendMode = BlendMode.Clear
            )


            drawRoundRect(
                color = baseGuideColor.copy(alpha = alphaPulse),
                topLeft = Offset(left, top),
                size = Size(guideWidth, guideHeight),
                cornerRadius = CornerRadius(120.dp.toPx(), 120.dp.toPx()),
                style = Stroke(
                    width = 5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(35f, 0f), 0f)
                )
            )
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.2f))
                        .clickable { onCloseClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Camera View",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }


                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (isFlashOn) MaterialTheme.colorScheme.primary else Color.Black.copy(
                                alpha = 0.2f
                            )
                        )
                        .clickable { isFlashOn = !isFlashOn },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Toggle Flash Frame State",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }


            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {

                Box(
                    modifier = Modifier
                        .padding(bottom = 64.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = if (isFaceDetected) "Perfect! Hold still" else "Center your face in the guide",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF191C1E),
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 96.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(48.dp)
//                            .clip(RoundedCornerShape(8.dp))
//                            .background(Color.White.copy(alpha = 0.1f))
//                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    ) {}


                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .border(4.dp, Color.White, CircleShape)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .clickable {
                                capturePhoto(context, imageCaptureUseCase, {
                                    capturedPhotoFile = it
                                    showVerifyDialog = true
                                })
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .clickable { useFrontCamera = !useFrontCamera },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlipCameraIos,
                            contentDescription = "Switch Camera Front Back Perspective",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

private fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onPhotoCaptured: (File) -> Unit
) {
    val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
    val photoFile = File(context.cacheDir, "KYC_$name.jpg")

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Toast.makeText(context, "Image Captured Successfully!", Toast.LENGTH_SHORT).show()
                onPhotoCaptured(photoFile)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraX", "Photo capture failed: ${exception.message}", exception)
                Toast.makeText(context, "Failed to capture photo", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

