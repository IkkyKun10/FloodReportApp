package com.riezki.floodreportapp.ui.home

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.riezki.floodreportapp.R
import com.riezki.floodreportapp.databinding.FiturNotExistBinding
import com.riezki.floodreportapp.databinding.FragmentHomeBinding
import com.riezki.floodreportapp.databinding.FragmentListReportBinding
import com.riezki.floodreportapp.ui.auth.AuthActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val user = Firebase.auth.currentUser
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.usernameProfile.text = user?.displayName
        binding.usernameText.text = user?.displayName?.let { StringBuilder(it).append("!") }
        binding.addReport.setOnClickListener {
            view.findNavController().navigate(R.id.action_homeFragment_to_createReportFragment)
        }
        binding.addReportMain.setOnClickListener {
            view.findNavController().navigate(R.id.action_homeFragment_to_createReportFragment)
        }

        binding.mapsOption.setOnClickListener {
            dialogNotAvailabel()
        }

        binding.editProfile.setOnClickListener {
            dialogNotAvailabel()
        }

        binding.cardList.setOnClickListener {
            view.findNavController().navigate(R.id.action_homeFragment_to_listReportFragment)
        }

        binding.logoutBtn.setOnClickListener {
            showDialogLogout()
        }
    }

    private fun dialogNotAvailabel() {
        val bindingDialog = FiturNotExistBinding.inflate(layoutInflater)
        val view = bindingDialog.root
        val dialog = AlertDialog.Builder(context).setView(view).create()

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        bindingDialog.btnOk.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun showDialogLogout() {
        val dialog: AlertDialog = AlertDialog.Builder(context)
            .setTitle("Keluar sesi?")
            .setMessage("Apakah anda yakin ingin keluar?")
            .setCancelable(false)
            .setPositiveButton("Ya") { dialog, _ ->
                signOut()
                dialog.dismiss()
                Intent(context, AuthActivity::class.java).also {
                    startActivity(it)
                    activity?.finish()
                }
            }
            .setNegativeButton("Tidak") {dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun signOut() {
        if (user != null) {
            Firebase.auth.signOut()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}