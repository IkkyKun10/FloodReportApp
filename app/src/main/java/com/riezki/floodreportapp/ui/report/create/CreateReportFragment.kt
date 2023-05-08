package com.riezki.floodreportapp.ui.report.create

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.riezki.floodreportapp.R
import com.riezki.floodreportapp.databinding.FragmentCreateReportBinding
import com.riezki.floodreportapp.utils.createCustomTempFile
import java.io.File

class CreateReportFragment : Fragment() {

    private var _binding: FragmentCreateReportBinding? = null
    private val binding get() = _binding!!
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private var dbFirestore = Firebase.firestore

    private lateinit var storageRef: StorageReference
    private var currentLocation: Location? = null
    private var getFile: File? = null
    private lateinit var currentPhotoPath: String
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCreateReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        storageRef = FirebaseStorage.getInstance().reference.child("Images")

        //database = FirebaseFirestore.getInstance()
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        getMyLastLocation()

        binding.icImageUpload.setOnClickListener {
            starTakePhoto()
        }

        binding.btnSend.setOnClickListener {
            if (valideteInput()) {
                showLoading(true)
                setData(view)
                showLoading(false)
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return requireContext().let {
            ContextCompat.checkSelfPermission(
                it, permission
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationProviderClient.getCurrentLocation(
                PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    currentLocation = loc

                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

    }
    private fun setData(view: View) {

        storageRef = storageRef.child(System.currentTimeMillis().toString())

        storageRef.putFile(Uri.fromFile(getFile)).addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                storageRef.downloadUrl.addOnSuccessListener { url ->
                    val latitude = currentLocation?.latitude ?: 0.0
                    val longitude = currentLocation?.longitude ?: 0.0
                    val user = binding.userReportEdt.text.toString().trim()
                    val location = binding.editTextLoc.text.toString().trim()
                    val description = binding.descEditText.text.toString().trim()


                    val dataMap = hashMapOf(
                        USER_REPORT to user,
                        LOCATION to location,
                        DESC to description,
                        IMAGE to url.toString(),
                        LAT to latitude,
                        LON to longitude,
                    )

                    dbFirestore.collection(DATA_POST).add(dataMap)
                        .addOnCompleteListener {

                            Snackbar.make(binding.root, "Berhasil menambahkan laporan", Snackbar.LENGTH_SHORT).show()
                            view.findNavController().navigate(R.id.action_createReportFragment_to_homeFragment)
                        }
                        .addOnFailureListener {

                            Snackbar.make(binding.root, "Gagal menambahkan laporan!", Snackbar.LENGTH_SHORT).show()
                        }

                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun valideteInput() : Boolean {
        with(binding) {
            return when {
                userReportEdt.text?.isEmpty() == true -> {
                    userReportEdt.requestFocus()
                    userReportEdt.setError("Silahkan isi nama terlebih dahulu", context?.resources?.getDrawable(R.drawable.ic_error))
                    false
                }

                editTextLoc.text?.isEmpty() == true -> {
                    editTextLoc.requestFocus()
                    editTextLoc.setError("Silahkan isi lokasi terlebih dahulu", context?.resources?.getDrawable(R.drawable.ic_error))
                    false
                }

                descEditText.text?.isEmpty() == true -> {
                    descEditText.requestFocus()
                    descEditText.setError("Silahkan isi descripsi terlebih dahulu", context?.resources?.getDrawable(R.drawable.ic_error))
                    false
                }

                else -> {
                    userReportEdt.setError(null)
                    editTextLoc.setError(null)
                    descEditText.setError(null)
                    true
                }
            }
        }


    }

    private fun starTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireActivity().packageManager)
        createCustomTempFile(requireActivity().application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireActivity(),
                "com.riezki.floodreportapp.ui.report.create",
                it
            )

            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            val result = BitmapFactory.decodeFile(myFile.path)

            getFile = myFile
            binding.imageUploadPrev.setImageBitmap(result)
        }
    }

    //Permission Location
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        when {
            permission[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getMyLastLocation()
            }

            permission[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getMyLastLocation()
            }

            else -> {
                //requireActivity().onBackPressed()
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(context, "Tidak mendapatkan permissions", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
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

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireActivity().baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val LOCATION = "location"
        const val DESC = "description"
        const val LAT = "latitude"
        const val LON = "longitude"
        const val IMAGE = "image"
        const val USER_REPORT = "user_report"

        const val DATA_POST = "data_post"

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}