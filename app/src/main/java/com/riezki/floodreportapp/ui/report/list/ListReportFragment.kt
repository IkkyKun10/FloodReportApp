package com.riezki.floodreportapp.ui.report.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.riezki.floodreportapp.databinding.FragmentListReportBinding
import com.riezki.floodreportapp.model.CloudDataEntity
import com.riezki.floodreportapp.ui.adapter.ListReportAdapter
import com.riezki.floodreportapp.ui.report.create.CreateReportFragment.Companion.DATA_POST
import com.riezki.floodreportapp.ui.report.create.CreateReportFragment.Companion.DESC
import com.riezki.floodreportapp.ui.report.create.CreateReportFragment.Companion.IMAGE
import com.riezki.floodreportapp.ui.report.create.CreateReportFragment.Companion.LAT
import com.riezki.floodreportapp.ui.report.create.CreateReportFragment.Companion.LOCATION
import com.riezki.floodreportapp.ui.report.create.CreateReportFragment.Companion.LON
import com.riezki.floodreportapp.ui.report.create.CreateReportFragment.Companion.USER_REPORT

class ListReportFragment : Fragment() {

    private var _binding: FragmentListReportBinding? = null
    private val binding get() = _binding!!
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var database = Firebase.firestore
    private lateinit var adapter: ListReportAdapter
    private lateinit var dataList: ArrayList<CloudDataEntity>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentListReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ListReportAdapter()
        dataList = arrayListOf()
        setRecycler()

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        getData(view)

    }

    private fun getData(view: View) {
        binding.progressBar.visibility = View.VISIBLE
        database.collection(DATA_POST).get()
            .addOnSuccessListener{
                binding.progressBar.visibility = View.GONE
                if (!it.isEmpty) {
                    for (data in it.documents) {
                        val item = CloudDataEntity(
                            id = data.id,
                            userReport = data.get(USER_REPORT)?.toString(),
                            loc = data.get(LOCATION)?.toString(),
                            locDesc = data.get(DESC)?.toString(),
                            image = data.get(IMAGE)?.toString(),
                            lat = data?.get(LAT)?.toString(),
                            lon = data?.get(LON)?.toString(),
                        )
                        dataList.add(item)
                    }
                    adapter.submitList(dataList)
                    Snackbar.make(view, "Daftar selesai dimuat!", Snackbar.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                binding.viewError.visibility = View.VISIBLE
                Snackbar.make(view, "Gagal menampilkan daftar!", Snackbar.LENGTH_SHORT).show()
            }
    }

    private fun setRecycler() {
        binding.rvListReport.layoutManager = LinearLayoutManager(context)
        binding.rvListReport.setHasFixedSize(true)
        binding.rvListReport.adapter = adapter
    }

    private fun showLoading(state: Boolean) {
        if (state) binding.progressBar.visibility = View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}