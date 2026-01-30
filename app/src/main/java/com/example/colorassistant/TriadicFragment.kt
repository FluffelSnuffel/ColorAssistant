package com.example.colorassistant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.colorassistant.databinding.FragmentTriadicBinding
class TriadicFragment : Fragment() {
    private var _binding: FragmentTriadicBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTriadicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val originalColor = arguments?.getInt(ARG_COLOR_FOR_GUIDE)
        val triaColor1 = arguments?.getInt(ARG_TRIA_1)
        val triaColor2 = arguments?.getInt(ARG_TRIA_2)

        originalColor?.let {
            binding.displayColorOriginalTria.setBackgroundColor(it)
        }

        triaColor1?.let {
            binding.displayColorTria1.setBackgroundColor(it)
        }

        triaColor2?.let {
            binding.displayColorTria2.setBackgroundColor(it)
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}