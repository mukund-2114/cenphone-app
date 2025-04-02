package com.example.cenphone

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.filament.*
import com.google.android.filament.utils.*
import java.nio.ByteBuffer
import com.google.android.filament.utils.KTX1Loader

class Model3dFragment : AppCompatActivity() {

    private lateinit var modelViewer: ModelViewer

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_model3d)

        // Initialize Filament
        Utils.init()

        val surfaceView = findViewById<SurfaceView>(R.id.modelView)
        modelViewer = ModelViewer(surfaceView)

        // Load the 3D model
        loadModel("iphone_14_pro.glb")

        // Load the environment map
        loadEnvironment("venetian_crossroads_1k.ktx")

        // Add touch listener with performClick
        surfaceView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    surfaceView.performClick() // Call performClick for accessibility
                    true // Indicate that the event has been handled
                }
                else -> {
                    modelViewer.onTouchEvent(event)
                    true // Indicate that the event has been handled
                }
            }
        }
    }

    private fun loadModel(assetName: String) {
        val buffer = readAsset(assetName)
        buffer?.let {
            modelViewer.loadModelGlb(it)
            updateRootTransform()
        } ?: run {
            println("Error: Unable to load model asset $assetName")
        }
    }

    private fun loadEnvironment(assetName: String) {
        val engine = modelViewer.engine
        val buffer = readAsset(assetName)
        buffer?.let {
            // Load indirect light
            KTX1Loader.createIndirectLight(engine, buffer).apply {
                intensity = 30_000.0f
                modelViewer.scene.indirectLight = this
            }

            KTX1Loader.createSkybox(engine, buffer).apply {
                modelViewer.scene.skybox = this
            }
        } ?: run {
            println("Error: Unable to load environment asset $assetName")
        }
    }

    private fun readAsset(assetName: String): ByteBuffer? {
        return try {
            assets.open(assetName).use { inputStream ->
                val bytes = ByteArray(inputStream.available())
                inputStream.read(bytes)
                ByteBuffer.wrap(bytes)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun updateRootTransform() {
        val asset = modelViewer.asset ?: return
        val rootEntity = asset.root

        // Use Filament's TransformManager to get the world transform
        val tm = modelViewer.engine.transformManager
        val instance = tm.getInstance(rootEntity)

        if (instance != 0) {
            // Compute bounding box or center if needed
            val aabb = asset.boundingBox
            val center = aabb.center
            val halfExtent = aabb.halfExtent

            // Set camera exposure and position
//            modelViewer.view.camera.setExposure(16.0f, 1.0f / 125.0f, 100.0f)
//            modelViewer.view.camera.lookAt(
//                center[0], center[1], center[2],  // target
//                center[0], center[1] + halfExtent[1], center[2] + 4.0f * halfExtent[2],  // eye
//                0.0f, 1.0f, 0.0f  // up
//            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        modelViewer.destroy()
    }
}