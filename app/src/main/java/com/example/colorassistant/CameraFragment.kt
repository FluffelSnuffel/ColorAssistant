package com.example.colorassistant

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.colorassistant.databinding.FragmentCameraBinding
import androidx.core.graphics.get
class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private var lastColor: Int = Color.WHITE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnPickColor.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                ARG_COLOR_FOR_GUIDE,
                Bundle().apply { putInt(ARG_COLOR_FOR_GUIDE, lastColor) }
            )
            parentFragmentManager.popBackStack()
        }

        startCamera()

        binding.previewView.setOnTouchListener { viewTouched, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    val x = event.x
                    val y = event.y

                    if (binding.colorMarker.visibility != View.VISIBLE) {
                        binding.colorMarker.visibility = View.VISIBLE
                    }

                    moveMarker(x, y)
                    pickColorAt(x, y)
                    viewTouched.performClick()
                    true
                }
                else -> false
            }
        }

        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(lastColor)
            setStroke((3 * resources.displayMetrics.density).toInt(), Color.WHITE)
        }
        binding.colorMarker.background = drawable
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().apply {
                surfaceProvider = binding.previewView.surfaceProvider
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun moveMarker(x: Float, y: Float) {
        binding.colorMarker.translationX = x - binding.colorMarker.width / 2
        binding.colorMarker.translationY = y - binding.colorMarker.height / 2
    }

    private fun pickColorAt(x: Float, y: Float) {
        val bitmap = binding.previewView.bitmap ?: return

        val bitmapX = (x / binding.previewView.width * bitmap.width).toInt()
        val bitmapY = (y / binding.previewView.height * bitmap.height).toInt()

        val clampedX = bitmapX.coerceIn(0, bitmap.width - 1)
        val clampedY = bitmapY.coerceIn(0, bitmap.height - 1)

        lastColor = bitmap[clampedX, clampedY]

        val drawable = binding.colorMarker.background as GradientDrawable
        drawable.setColor(lastColor)
        drawable.setStroke((3 * resources.displayMetrics.density).toInt(), Color.WHITE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
