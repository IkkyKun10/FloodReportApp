package com.riezki.floodreportapp.ui.auth.login

import android.app.AlertDialog
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.riezki.floodreportapp.R
import com.riezki.floodreportapp.databinding.FragmentLoginBinding
import com.riezki.floodreportapp.databinding.LoadingPageBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        binding.registerBtnTxt.setOnClickListener {
            view.findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.buttonLogin.setOnClickListener {
            if (validateLogin()) {
                showLoading(true)
                signInFirebase(
                    binding.emailEdit.text.toString(),
                    binding.passEdt.text.toString()
                )
                showLoading(false)
                view.findNavController().navigate(R.id.action_loginFragment_to_mainActivity)
                activity?.finish()
            }
        }

        binding.btnSignGoogle.setOnClickListener {
            Snackbar.make(view, "Maaf, fitur belum tersedia!", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun validateLogin() : Boolean {
        with(binding) {
            return when {
                emailEdit.text?.isEmpty() == true -> {
                    emailEdit.requestFocus()
                    false
                }

                passEdt.text?.isEmpty() == true -> {
                    passEdt.requestFocus()
                    false
                }

                passEdt.text.toString().length < 6 -> {
                    passEdt.error = "Minimal password 6 karakter"
                    passEdt.requestFocus()
                    false
                }

                (!Patterns.EMAIL_ADDRESS.matcher(emailEdit.text.toString()).matches()) -> {
                    emailEdit.error = "Email tidak valid!"
                    emailEdit.requestFocus()
                    false
                }

                else -> {
                    emailEdit.error = null
                    passEdt.error = null
                    true
                }
            }
        }
    }

    private fun signInFirebase(email: String, pass: String) {

        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(requireActivity()) {
                if (it.isSuccessful) {

                    Toast.makeText(context, getString(R.string.login_berhasil), Toast.LENGTH_SHORT).show()
                } else {

                    Snackbar.make(binding.root, "${it.exception?.message}", Snackbar.LENGTH_SHORT).show()
                }
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