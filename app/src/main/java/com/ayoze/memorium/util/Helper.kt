package com.ayoze.memorium.util

import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import com.ayoze.memorium.model.Roles
import com.ayoze.memorium.view.AuthActivity

open class Helper {
    open fun checkTextFields(editTexts: Array<EditText>): Boolean {
        for (eText in editTexts) {
            if (eText.text.isNullOrEmpty()) {
                eText.error = "Este campo no puede estar vacío"
                return false
            }
        }
        return true
    }

    open fun checkRGButtons(rolRGrp: RadioGroup?): Boolean {
        return rolRGrp?.checkedRadioButtonId != -1
    }

    open fun checkCBoxes(checkBoxes: Array<CheckBox>): Boolean {
        var count = 0
        for (cBox in checkBoxes) {
            if (cBox.isChecked) {
                count++
                break
            }
        }
        return count > 0
    }

    open fun getRolOnRegister(rolRGrp: RadioGroup): Roles {

        val selectedRadioButtonId: Int = rolRGrp.checkedRadioButtonId
        return if (selectedRadioButtonId != -1) {
            val selectedRadioButton: View = rolRGrp.findViewById(selectedRadioButtonId)
            val idx: Int = rolRGrp.indexOfChild(selectedRadioButton)
            val rBtn: RadioButton = rolRGrp.getChildAt(idx) as RadioButton
            when (rBtn.text.toString()) {
                "Colaborador" -> Roles.COLABORADOR
                "Paciente" -> Roles.PACIENTE
                else -> Roles.PACIENTE
            }
        } else {
            Roles.PACIENTE
        }
    }

    open fun checkIfNullOrEmpty(string: String): Boolean {
        return string.isEmpty()
    }

    open fun getRolFromString(rol: String): Roles {
        Log.i(TAG, Roles.valueOf(rol).toString())
        return Roles.valueOf(rol)
    }

    companion object {
        private const val TAG = "Helper"
    }
}
