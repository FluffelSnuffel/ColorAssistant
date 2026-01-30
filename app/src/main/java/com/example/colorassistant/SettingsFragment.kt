package com.example.colorassistant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.colorassistant.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch
import androidx.core.content.edit

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentLanguage: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            currentLanguage =
                LanguagePreferences.getLanguage(requireContext())

            setupLanguageSpinner()
        }

        setupTheme()
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    private fun setupLanguageSpinner() {
        val languages = listOf("English", "Swedish")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            languages
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.languageSpinner.adapter = adapter

        binding.languageSpinner.setSelection(
            if (currentLanguage == "sv") 1 else 0,
            false
        )

        binding.languageSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedLanguage = if (position == 1) "sv" else "en"
                    if (selectedLanguage != currentLanguage) {
                        saveLanguageAndRestart(selectedLanguage)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
    }
    private fun saveLanguageAndRestart(languageCode: String) {
        lifecycleScope.launch {
            currentLanguage = languageCode
            LanguagePreferences.saveLanguage(requireContext(), languageCode)
            requireActivity().recreate()
        }
    }

    private fun setupTheme() {
        val prefs =
            requireContext().getSharedPreferences("settings", android.content.Context.MODE_PRIVATE)
        val savedMode = prefs.getInt(
            "theme_mode",
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )

        when (savedMode) {
            AppCompatDelegate.MODE_NIGHT_NO -> binding.lightMode.isChecked = true
            AppCompatDelegate.MODE_NIGHT_YES -> binding.darkMode.isChecked = true
        }

        binding.themeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.lightMode -> saveAndApplyTheme(AppCompatDelegate.MODE_NIGHT_NO)
                R.id.darkMode -> saveAndApplyTheme(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }
    private fun saveAndApplyTheme(mode: Int) {
        val prefs =
            requireContext().getSharedPreferences("settings", android.content.Context.MODE_PRIVATE)
        prefs.edit { putInt("theme_mode", mode) }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
