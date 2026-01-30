package com.example.colorassistant

import android.graphics.*
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.scale
import com.example.colorassistant.databinding.FragmentComplementaryBinding
import androidx.core.graphics.createBitmap

class ComplementaryFragment : Fragment() {

    private var _binding: FragmentComplementaryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComplementaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val originalColor = arguments?.getInt(ARG_COLOR_FOR_GUIDE) ?: Color.WHITE
        val complementaryColor = arguments?.getInt(ARG_COMPLEMENTARY_COLOR) ?: Color.WHITE

        binding.finalImage.post {
            val targetWidth = binding.finalImage.width

            val baseBitmapOriginal = BitmapFactory.decodeResource(resources, R.drawable.base_tree)
            val baseBitmap = baseBitmapOriginal.scale(
                targetWidth,
                (baseBitmapOriginal.height * targetWidth.toFloat() / baseBitmapOriginal.width).toInt()
            )

            val maskABitmap = BitmapFactory.decodeResource(resources, R.drawable.color1)
                .scale(baseBitmap.width, baseBitmap.height)
            val maskBBitmap = BitmapFactory.decodeResource(resources, R.drawable.color2)
                .scale(baseBitmap.width, baseBitmap.height)

            val lightingBitmap = BitmapFactory.decodeResource(resources, R.drawable.light_tree)
                .scale(baseBitmap.width, baseBitmap.height)

            val masks = listOf(maskABitmap, maskBBitmap)
            val colors = listOf(originalColor, complementaryColor)
            val finalBitmap = createCompositeBitmap(baseBitmap, masks, colors, lightingBitmap)

            binding.finalImage.setImageBitmap(finalBitmap)
        }

        binding.displayColorOriginalComp.setBackgroundColor(originalColor)
        binding.displayColorComplementary.setBackgroundColor(complementaryColor)

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
