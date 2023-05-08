package com.riezki.floodreportapp.ui.auth.signup

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.riezki.floodreportapp.R
import com.riezki.floodreportapp.databinding.FragmentSignUpBinding
import com.riezki.floodreportapp.databinding.LoadingPageBinding

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.loginBtnTxt.setOnClickListener {
            view.findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        binding.buttonRegister.setOnClickListener {
            if (validateSignUp()) {
                showLoading(true)
                registerFirebase(
                    view,
                    binding.nameEdit.text.toString().trim(),
                    binding.emailEdit.text.toString().trim(),
                    binding.numberEdit.text.toString().trim(),
                    binding.repassEdt.text.toString().trim()
                )
                showLoading(false)
            }
        }
    }

    private fun validateSignUp(): Boolean {
        with(binding) {
            return when {
                nameEdit.text?.isEmpty() == true -> {
                    nameInput.error = "Silahkan isi nama terlebih dahulu!"
                    false
                }

                emailEdit.text?.isEmpty() == true -> {
                    emailInput.error = "Silahkan isi email terlebih dahulu!"
                    false
                }

//                numberEdit.text?.isEmpty() == true && numberEdit.text.toString().length <= 10-> {
//                    numberEdit.requestFocus()
//                    numberInput.error = "Nomor telepon tidak sesuai"
//                    false
//                }

                passEdt.text?.isEmpty() == true -> {
                    paswordInput.error = "Silahkan isi password terlebih dahulu!"
                    false
                }

                repassEdt.text?.isEmpty() == true -> {
                    repaswordInput.error = "Silahkan isi password terlebih dahulu!"
                    false
                }

                (!Patterns.EMAIL_ADDRESS.matcher(emailEdit.text.toString()).matches()) -> {
                    emailEdit.requestFocus()
                    emailInput.error = "Email tidak valid!"
                    false
                }

                passEdt.text.toString().length < 6 -> {
                    passEdt.requestFocus()
                    paswordInput.error = "Minimal password 6 karakter"
                    false
                }

                repassEdt.text.toString().length < 6 -> {
                    repassEdt.requestFocus()
                    repaswordInput.error = "Minimal password 6 karakter"
                    false
                }

                (repassEdt.text.toString() != passEdt.text.toString()) -> {
                    repassEdt.requestFocus()
                    repaswordInput.error = "Password tidak sama"
                    false
                }

                else -> {
                    nameInput.error = null
                    emailInput.error = null
                    //numberInput.error = null
                    paswordInput.error = null
                    repaswordInput.error = null
                    true
                }
            }
        }
    }

    private fun registerFirebase(view: View, username: String, email: String, numberPhone: String, pass: String) {

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(requireActivity()) {
                if (it.isSuccessful) {

                    val profilUpdate = userProfileChangeRequest {
                        displayName = username

                    }
                    auth.currentUser?.updateProfile(profilUpdate)
                        ?.addOnCompleteListener {task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "Username berhasil ditambahkan")
                            }
                        }
                    requireActivity().onBackPressed()
                    Snackbar.make(binding.root, getString(R.string.register_berhasil), Snackbar.LENGTH_SHORT).show()
                    Toast.makeText(context, getString(R.string.register_berhasil), Toast.LENGTH_SHORT).show()
                } else {

                    Toast.makeText(context, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {

                Toast.makeText(context, it.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLoading(state: Boolean) {
        if (state) binding.progressBar.visibility = View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}