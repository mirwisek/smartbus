package com.fyp.smartbus.login.model

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false,
    val isEligibleForgetPassword: Boolean = false
)

data class RegisterFormState(
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val nameError: Int? = null,
    val busError: Int? = null,
    val isDataValid: Boolean = false
)