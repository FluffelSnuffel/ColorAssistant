package com.example.colorassistant

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import com.example.colorassistant.databinding.FragmentSavedBinding

class SavedFragment : Fragment() {
    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFavorites()

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    private fun loadFavorites() {
        val favoriteColors = FavoritesManager.getFavorites(requireContext())
        val container = binding.favoritesContainer
        container.removeAllViews()

        for (color in favoriteColors) {
            val row = LinearLayout(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 8, 0, 8) }
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }
            val colorView = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(50, 50).apply { setMargins(0, 0, 16, 0) }
                setBackgroundColor(color)
            }
            val hexText = TextView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                text = String.format("#%06X", 0xFFFFFF and color)
                textSize = 16f
            }
            row.setOnClickListener {
                parentFragmentManager.setFragmentResult(
                    KEY_COLOR_FROM_SAVED,
                    bundleOf("baseColor" to color)
                )

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ColorGuideFragment())
                    .addToBackStack(null)
                    .commit()
            }
            row.addView(colorView)
            row.addView(hexText)
            container.addView(row)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}