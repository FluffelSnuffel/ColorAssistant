package com.example.colorassistant

import android.graphics.*
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import com.example.colorassistant.databinding.FragmentMonochromaticBinding

class MonochromaticFragment : Fragment() {
    private var _binding: FragmentMonochromaticBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonochromaticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val originalColor = arguments?.getInt(ARG_COLOR_FOR_GUIDE) ?: Color.WHITE
        val monoDark = arguments?.getInt(ARG_MONO_DARK) ?: Color.WHITE
        val monoLight = arguments?.getInt(ARG_MONO_LIGHT) ?: Color.WHITE

        binding.finalImage.post {
            val targetWidth = binding.finalImage.width
            val baseBitmapOriginal = BitmapFactory.decodeResource(resources, R.drawable.three_bags_base)
            val baseBitmap = baseBitmapOriginal.scale(
                targetWidth,
                (baseBitmapOriginal.height * targetWidth.toFloat() / baseBitmapOriginal.width).toInt()
            )
            val maskDark = BitmapFactory.decodeResource(resources, R.drawable.three_bags_mask1)
                .scale(baseBitmap.width, baseBitmap.height)
            val maskMid = BitmapFactory.decodeResource(resources, R.drawable.three_bags_mask2)
                .scale(baseBitmap.width, baseBitmap.height)
            val maskLight = BitmapFactory.decodeResource(resources, R.drawable.three_bags_mask3)
                .scale(baseBitmap.width, baseBitmap.height)
            val lightingBitmap = BitmapFactory.decodeResource(resources, R.drawable.three_bags_light)
                .scale(baseBitmap.width, baseBitmap.height)
            val masks = listOf(maskDark, maskMid, maskLight)
            val colors = listOf(monoDark, originalColor, monoLight)
            val finalBitmap = createCompositeBitmap(
                baseBitmap,
                masks,
                colors,
                lightingBitmap
            )
            binding.finalImage.setImageBitmap(finalBitmap)
        }
        binding.displayColorOriginalMono.setBackgroundColor(originalColor)
        binding.displayColorDarkMono.setBackgroundColor(monoDark)
        binding.displayColorLightMono.setBackgroundColor(monoLight)
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun createCompositeBitmap(
        base: Bitmap,
        masks: List<Bitmap>,
        colors: List<Int>,
        lighting: Bitmap,
        lightingAlpha: Float = 0.5f
    ): Bitmap {
        val result = createBitmap(base.width, base.height)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        
        canvas.drawBitmap(base, 0f, 0f, paint)
        for (i in masks.indices) {
            paint.colorFilter = PorterDuffColorFilter(colors[i], PorterDuff.Mode.MULTIPLY)
            canvas.drawBitmap(masks[i], 0f, 0f, paint)
        }

        paint.colorFilter = null
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
        paint.alpha = (lightingAlpha * 255).toInt()
        canvas.drawBitmap(lighting, 0f, 0f, paint)
        paint.xfermode = null
        paint.alpha = 255

        return result
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}