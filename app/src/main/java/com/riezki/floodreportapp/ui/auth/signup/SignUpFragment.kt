package com.riezki.floodreportapp.ui.auth.signup

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isEmpty
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.riezki.floodreportapp.R
import com.riezki.floodreportapp.databinding.FragmentSignUpBinding

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

        }

        binding.buttonRegister.setOnClickListener {
            if (validateSignUp()) {
                registerFirebase(
                    binding.emailEdit.text.toString(),
                    binding.passEdt.text.toString()
                )
            }
        }
    }

    private fun validateSignUp(): Boolean {
        with(binding) {
            return when {
                nameEdit.text?.isEmpty() == true -> {
                    nameEdit.error = "Silahkan isi nama terlebih dahulu!"
                    false
                }

                emailEdit.text?.isEmpty() == true -> {
                    emailEdit.error = "Silahkan isi email terlebih dahulu!"
                    false
                }

                passEdt.text?.isEmpty() == true -> {
                    passEdt.error = "Silahkan isi password terlebih dahulu!"
                    false
                }

                repassEdt.text?.isEmpty() == true -> {
                    repassEdt.error = "Silahkan isi password terlebih dahulu!"
                    false
                }

                (!Patterns.EMAIL_ADDRESS.matcher(emailEdit.text.toString()).matches()) -> {
                    emailEdit.error = "Email tidak valid!"
                    emailEdit.requestFocus()
                    false
                }

                passEdt.text.toString().length < 6 -> {
                    passEdt.error = "Minimal password 6 karakter"
                    passEdt.requestFocus()
                    false
                }

                repassEdt.text.toString().length < 6 -> {
                    passEdt.error = "Minimal password 6 karakter"
                    passEdt.requestFocus()
                    false
                }

                (repassEdt.text.toString() != passEdt.text.toString()) -> {
                    repassEdt.error = "Password tidak sama"
                    repassEdt.requestFocus()
                    false
                }

                else -> {
                    nameEdit.error = null
                    emailEdit.error = null
                    passEdt.error = null
                    repassEdt.error = null
                    true
                }
            }
        }
    }

    private fun registerFirebase(email: String, pass: String) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(requireActivity()) {
                if (it.isSuccessful) {

                } else {
                    Toast.makeText(context, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}