package com.example.colorassistant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.colorassistant.databinding.FragmentAnalogousBinding

class AnalogousFragment : Fragment() {
    private var _binding: FragmentAnalogousBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnalogousBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val originalColor = arguments?.getInt(ARG_COLOR_FOR_GUIDE)
        val anaColor1 = arguments?.getInt(ARG_ANALOG_LEFT)
        val anaColor2 = arguments?.getInt(ARG_ANALOG_RIGHT)

        originalColor?.let {
            binding.displayColorOriginalAnal.setBackgroundColor(it)
        }

        anaColor1?.let {
            binding.displayColorAnal1.setBackgroundColor(it)
        }

        anaColor2?.let {
            binding.displayColorAnal2.setBackgroundColor(it)
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