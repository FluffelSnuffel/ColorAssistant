package com.example.colorassistant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.colorassistant.databinding.FragmentMainMenuBinding
class MainMenuFragment : Fragment() {
    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.openSettings.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.btnFavorites.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SavedFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.btnColorWheel.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ColorWheelFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.aboutBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AboutFragment())
                .addToBackStack(null)
                .commit()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}