package com.example.colorassistant

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.colorassistant.databinding.FragmentColorWheelBinding
import androidx.core.graphics.get
import androidx.core.graphics.toColorInt
import kotlin.math.roundToInt

const val ARG_COLOR_FOR_GUIDE = "pickedColor"

class ColorWheelFragment : Fragment() {

    private var _binding: FragmentColorWheelBinding? = null
    private val binding get() = _binding!!

    private var lastColor: Int = Color.WHITE
    private var isUpdatingText = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentColorWheelBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getInt(ARG_COLOR_FOR_GUIDE)?.let { applyColor(it, updateText = true) }

        parentFragmentManager.setFragmentResultListener(ARG_COLOR_FOR_GUIDE, viewLifecycleOwner) { _, bundle ->
            val pickedColor = bundle.getInt(ARG_COLOR_FOR_GUIDE)
            applyColor(pickedColor, updateText = true)
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnGuide.setOnClickListener {
            val fragment = ColorGuideFragment().apply {
                arguments = Bundle().apply { putInt(ARG_COLOR_FOR_GUIDE, lastColor) }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.btnCamera.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CameraFragment())
                .addToBackStack(null)
                .commit()
        }

        setupColorPicker()
        setupEditableColorField()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupColorPicker() {
        val marker = binding.colorMarker
        val bitmap = (binding.colorPicker.drawable as BitmapDrawable).bitmap

        binding.colorPicker.setOnTouchListener { viewTouched, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    val x = event.x
                    val y = event.y

                    if (x < 0 || y < 0 || x > viewTouched.width || y > viewTouched.height) return@setOnTouchListener true

                    val scaleX = bitmap.width.toFloat() / viewTouched.width
                    val scaleY = bitmap.height.toFloat() / viewTouched.height

                    val bitmapX = (x * scaleX).roundToInt()
                    val bitmapY = (y * scaleY).roundToInt()

                    if (bitmapX !in 0 until bitmap.width || bitmapY !in 0 until bitmap.height) return@setOnTouchListener true

                    val color = bitmap[bitmapX, bitmapY]
                    if (Color.alpha(color) < 255) return@setOnTouchListener true

                    marker.visibility = View.VISIBLE
                    marker.x = viewTouched.x + x - marker.width / 2
                    marker.y = viewTouched.y + y - marker.height / 2

                    updateColorFromWheel(color)
                }
                MotionEvent.ACTION_UP -> viewTouched.performClick()
            }
            true
        }
    }

    private fun setupEditableColorField() {
        binding.displayValues.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isUpdatingText) return
                parseAndApplyColor(s.toString().trim())
            }
        })
    }

    private fun updateColorFromWheel(color: Int) {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)

        val hex = String.format("#%02X%02X%02X", r, g, b)

        isUpdatingText = true
        binding.displayValues.setText(hex)
        binding.displayValues.setSelection(hex.length)
        isUpdatingText = false

        applyColor(color, updateText = false)
    }

    private fun parseAndApplyColor(input: String) {
        try {
            val color = when {
                input.matches(Regex("^#?[0-9A-Fa-f]{6}$")) -> {
                    (if (input.startsWith("#")) input else "#$input").toColorInt()
                }
                input.matches(Regex("^\\d{1,3},\\d{1,3},\\d{1,3}$")) -> {
                    val (r, g, b) = input.split(",").map { it.toInt() }
                    require(r in 0..255 && g in 0..255 && b in 0..255)
                    Color.rgb(r, g, b)
                }
                else -> return
            }
            applyColor(color, updateText = false)
        } catch (_: Exception) {}
    }

    private fun applyColor(
        color: Int,
        updateText: Boolean = false
    ) {
        binding.displayColor.setBackgroundColor(color)

        val drawable = binding.colorMarker.background as GradientDrawable
        drawable.setColor(color)
        drawable.setStroke((3 * resources.displayMetrics.density).toInt(), Color.WHITE)

        if (updateText) {
            val hex = String.format("#%06X", 0xFFFFFF and color)
            isUpdatingText = true
            binding.displayValues.setText(hex)
            binding.displayValues.setSelection(hex.length)
            isUpdatingText = false
        }

        lastColor = color
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
