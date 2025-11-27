package com.beavuck.stop_and_go.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.model.SupportedLocale
import com.beavuck.stop_and_go.repositories.ConfigRepository

class LanguagePickerDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        val configRepository = ConfigRepository(context)

        val languages = SupportedLocale.getAllDisplayNames(context)
        val savedLocale = configRepository.loadLocale()
        val currentSelection = SupportedLocale.fromCode(savedLocale).ordinal

        return AlertDialog.Builder(context)
            .setTitle(R.string.language)
            .setSingleChoiceItems(languages, currentSelection) { dialog, which ->
                val selectedLocale = SupportedLocale.entries[which]
                configRepository.saveLocale(selectedLocale.code)
                dialog.dismiss()
                activity?.recreate()
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    companion object {
        fun newInstance() = LanguagePickerDialog()
    }
}
