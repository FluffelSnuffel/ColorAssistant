package com.example.colorassistant

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.colorassistant.databinding.FragmentColorGuideBinding
const val KEY_COLOR_FROM_SAVED = "color_from_saved"

const val ARG_COMPLEMENTARY_COLOR = "complementaryColor"
const val ARG_MONO_LIGHT = "monoLight"
const val ARG_MONO_DARK = "monoDark"
const val ARG_ANALOG_LEFT = "analogLeft"
const val ARG_ANALOG_RIGHT = "analogRight"
const val ARG_TRIA_1 = "tria1"
const val ARG_TRIA_2 = "tria2"
const val ARG_TETRA_1 = "tetra1"
const val ARG_TETRA_2 = "tetra2"
const val ARG_TETRA_3 = "tetra3"


class ColorGuideFragment : Fragment() {
    private var lastBaseColor: Int = Color.WHITE
    private var _binding: FragmentColorGuideBinding? = null
    private val binding get() = _binding!!
    private var isLiked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentColorGuideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener(ARG_COLOR_FOR_GUIDE, viewLifecycleOwner) { _, bundle ->
            updateBaseColor(bundle.getInt(ARG_COLOR_FOR_GUIDE))
        }

        parentFragmentManager.setFragmentResultListener(KEY_COLOR_FROM_SAVED, viewLifecycleOwner) { _, bundle ->
            updateBaseColor(bundle.getInt("baseColor"))
        }


        arguments?.getInt(ARG_COLOR_FOR_GUIDE)?.let {
            updateBaseColor(it)
        }

        setupButtons()
    }

    private fun applyColor(target: View, color: Int) {
        target.setBackgroundColor(color)
    }
    private fun findComplementaryColor(color: Int): Int = shiftHue(color, 180f)

    private fun findAnalogousColors(color: Int, angle: Float = 30f): Pair<Int, Int> =
        Pair(shiftHue(color, -angle), shiftHue(color, angle))

    private fun findTriadicColors(color: Int): Pair<Int, Int> =
        Pair(shiftHue(color, -120f), shiftHue(color, 120f))

    private fun findTetradicColors(color: Int): Triple<Int, Int, Int> =
        Triple(shiftHue(color, 60f), shiftHue(color, 180f), shiftHue(color, 240f))

    private fun shiftHue(color: Int, degrees: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[0] = (hsv[0] + degrees + 360f) % 360f
        return Color.HSVToColor(Color.alpha(color), hsv)
    }
    private fun findMonochromaticColor(color: Int, valueDelta: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        if (valueDelta > 0 && hsv[2] >= 1f) {
            // Can't go brighter → reduce saturation instead
            hsv[1] = (hsv[1] - valueDelta).coerceIn(0f, 1f)
        } else {
            hsv[2] = (hsv[2] + valueDelta).coerceIn(0f, 1f)
        }
        return Color.HSVToColor(Color.alpha(color), hsv)
    }

    private fun updateColors(baseColor: Int) {
        val complementaryColor = findComplementaryColor(baseColor)
        val monoLight = findMonochromaticColor(baseColor, +0.2f)
        val monoDark = findMonochromaticColor(baseColor, -0.2f)
        val (analogLeft, analogRight) = findAnalogousColors(baseColor)
        val (tria1, tria2) = findTriadicColors(baseColor)
        val (tetra1, tetra2, tetra3) = findTetradicColors(baseColor)

        // Apply colors
        applyColor(binding.displayColor, baseColor)

        applyColor(binding.displayColorOriginalComp, baseColor)
        applyColor(binding.displayColorComplementary, complementaryColor)

        applyColor(binding.displayColorOriginalMon, baseColor)
        applyColor(binding.displayColorDarkMono, monoDark)
        applyColor(binding.displayColorLightMono, monoLight)

        applyColor(binding.displayColorOriginalAnal, baseColor)
        applyColor(binding.displayColorAnal1, analogLeft)
        applyColor(binding.displayColorAnal2, analogRight)

        applyColor(binding.displayColorOriginalTria, baseColor)
        applyColor(binding.displayColorTria1, tria1)
        applyColor(binding.displayColorTria2, tria2)

        applyColor(binding.displayColorOriginalTetra, baseColor)
        applyColor(binding.displayColorTetra1, tetra1)
        applyColor(binding.displayColorTetra2, tetra2)
        applyColor(binding.displayColorTetra3, tetra3)
    }

    private fun setupButtons() {
        binding.btnComplementary.setOnClickListener {
            val complementaryColor = findComplementaryColor(lastBaseColor)
            val fragment = ComplementaryFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLOR_FOR_GUIDE, lastBaseColor)
                    putInt(ARG_COMPLEMENTARY_COLOR, complementaryColor)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.btnMonochromatic.setOnClickListener {
            val monoLight = findMonochromaticColor(lastBaseColor, +0.2f)
            val monoDark = findMonochromaticColor(lastBaseColor, -0.2f)
            val fragment = MonochromaticFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLOR_FOR_GUIDE, lastBaseColor)
                    putInt(ARG_MONO_LIGHT, monoLight)
                    putInt(ARG_MONO_DARK, monoDark)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.btnAnalogous.setOnClickListener {
            val (analogLeft, analogRight) = findAnalogousColors(lastBaseColor)
            val fragment = AnalogousFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLOR_FOR_GUIDE, lastBaseColor)
                    putInt(ARG_ANALOG_LEFT, analogLeft)
                    putInt(ARG_ANALOG_RIGHT, analogRight)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.btnTriadic.setOnClickListener {
            val (tria1, tria2) = findTriadicColors(lastBaseColor)
            val fragment = TriadicFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLOR_FOR_GUIDE, lastBaseColor)
                    putInt(ARG_TRIA_1, tria1)
                    putInt(ARG_TRIA_2, tria2)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.btnTetradic.setOnClickListener {
            val (tetra1, tetra2, tetra3) = findTetradicColors(lastBaseColor)
            val fragment = TetradicFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLOR_FOR_GUIDE, lastBaseColor)
                    putInt(ARG_TETRA_1, tetra1)
                    putInt(ARG_TETRA_2, tetra2)
                    putInt(ARG_TETRA_3, tetra3)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.btnFavorite.setOnClickListener {
            isLiked = !isLiked
            if (isLiked) {
                FavoritesManager.saveColor(requireContext(), lastBaseColor)
            } else {
                FavoritesManager.removeColor(requireContext(), lastBaseColor)
            }
            updateHeartIcon()
        }


        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun updateBaseColor(color: Int) {
        lastBaseColor = color
        updateColors(color)
        isLiked = FavoritesManager.isFavorite(requireContext(), color)
        updateHeartIcon()
    }
    private fun updateHeartIcon() {
        binding.btnFavorite.setImageResource(
            if (isLiked) R.drawable.heart_selected
            else R.drawable.heart_empty
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
