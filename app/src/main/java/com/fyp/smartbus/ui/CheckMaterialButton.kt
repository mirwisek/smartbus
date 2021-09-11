package com.fyp.smartbus.ui

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.fyp.smartbus.R
import com.google.android.material.button.MaterialButton

class CheckMaterialButton : MaterialButton,
        MaterialButton.OnCheckedChangeListener {

    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    constructor(context: Context) : super(context)

    constructor(
            context: Context,
            attrs: AttributeSet?
    ) : super(context, attrs)


    init {
        addOnCheckedChangeListener(this)
    }

    public fun addCustomCheckedChangedListener(listener: OnCheckedChangeListener ) {
        removeOnCheckedChangeListener(this)
        addOnCheckedChangeListener(listener)
    }


    override fun onCheckedChanged(button: MaterialButton?, isChecked: Boolean) {
        button?.let {
            if (isChecked) {
                // Don't recheck twice
                button.isClickable = false
                button.icon =
                        ContextCompat.getDrawable(context, R.drawable.ic_check)
            }
            else {
                button.isClickable = true
                button.icon = null
            }
        }
    }

}