package com.example.colorassistant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.colorassistant.databinding.FragmentTetradicBinding

class TetradicFragment : Fragment() {
    private var _binding: FragmentTetradicBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTetradicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val originalColor = arguments?.getInt(ARG_COLOR_FOR_GUIDE)
        val tetColor1 = arguments?.getInt(ARG_TETRA_1)
        val tetColor2 = arguments?.getInt(ARG_TETRA_2)
        val tetColor3 = arguments?.getInt(ARG_TETRA_3)

        originalColor?.let {
            binding.displayColorOriginalTetra.setBackgroundColor(it)
        }

        tetColor1?.let {
            binding.displayColorTetra1.setBackgroundColor(it)
        }

        tetColor2?.let {
            binding.displayColorTetra2.setBackgroundColor(it)
        }

        tetColor3?.let {
            binding.displayColorTetra3.setBackgroundColor(it)
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