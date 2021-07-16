package com.example.trackyourruns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.trackyourruns.database.Run
import com.example.trackyourruns.databinding.FragmentManageRunsBinding

/*
* Displays all Runs to select one to edit or delete
* */
class FragmentManageRuns : Fragment() {

    companion object {
        const val TAG_RUN_ID = "run_id"
    }

    /*
    * Binding and ViewModel
    * */
    private var _binding: FragmentManageRunsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TyrViewModel by activityViewModels()

    private var runs: List<Run> = listOf()  // List of all Runs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runs = viewModel.selectAllRuns().sortedByDescending { it.startTime }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageRunsBinding.inflate(inflater, container, false)

        // Adapter for RecyclerView
        val adapter = AdapterManageRun(this, runs)
        binding.recyclerViewManageRuns.adapter = adapter

        return binding.root
    }

    fun getRouteByID(routeID: Int): String {
        return viewModel.selectRoute(routeID).name
    }

    /*
    * RecyclerView.Adapter-Class
    * */
    private class AdapterManageRun(
        private val fragmentManageRuns: FragmentManageRuns,
        private val runs: List<Run>
    ) : RecyclerView.Adapter<AdapterManageRun.ViewHolderRun>() {

        class ViewHolderRun(view: View) : RecyclerView.ViewHolder(view) {
            val txtViewStartDateTime: TextView = view.findViewById(R.id.txtView_StartDateTime)
            val txtViewRoute: TextView = view.findViewById(R.id.txtView_Route)
            val linearLayoutRun: LinearLayout = view.findViewById(R.id.linearLayout_run)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRun {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_manage_run_item, parent, false)
            return ViewHolderRun(view)
        }

        override fun onBindViewHolder(holder: ViewHolderRun, position: Int) {
            holder.txtViewStartDateTime.text =
                TyrViewModel.dateTimeToStringDisplay(runs[position].startTime)
            holder.txtViewRoute.text = fragmentManageRuns.getRouteByID(runs[position].routeID)

            /*
            * Calls 'AddRun' with clicked RunID
            * */
            holder.linearLayoutRun.setOnClickListener {
                val bundle: Bundle = bundleOf(TAG_RUN_ID to runs[position].id)
                fragmentManageRuns.findNavController()
                    .navigate(R.id.action_fragmentManageRuns_to_fragmentAddRun, bundle)
            }
        }

        override fun getItemCount(): Int {
            return runs.size
        }
    }
}