package com.example.cauldron // Updated to match your new namespace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.DialogFragment
// Updated import to match the new package name
import com.example.cauldron.databinding.DialogInstructionsBinding

class BrewingInstructionsDialog : DialogFragment() {

    private var _binding: DialogInstructionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Apply parchment dialog style
        // Note: Ensure R.style.ParchmentDialog exists in your themes.xml
        setStyle(STYLE_NO_TITLE, R.style.ParchmentDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogInstructionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Slide-up entrance animation
        val slideUp = AnimationUtils.loadAnimation(requireContext(), android.R.anim.slide_in_left)
        view.startAnimation(slideUp)

        // Close button dismisses the dialog
        binding.btnCloseDialog.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        // Make dialog take up 90% of screen width
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "BrewingInstructionsDialog"
        fun newInstance() = BrewingInstructionsDialog()
    }
}