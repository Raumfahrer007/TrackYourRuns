package com.example.trackyourruns

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.trackyourruns.databinding.FragmentStartFrameBinding

/*
* StartScreen - general statistics, Add Run, Edit Runs or Routes
* */
class FragmentStartFrame : Fragment() {

    /*
    * Binding and ViewModel
    * */
    private var _binding: FragmentStartFrameBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TyrViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        _binding = FragmentStartFrameBinding.inflate(inflater, container, false)

        binding.btnAddRun.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentStartFrame_to_fragmentAddRun)
        }

        /*
        * Values for TextViews in corners
        * */
        val runs = viewModel.selectAllRuns()
        val routes = viewModel.selectAllRoutes()
        var totalKm = 0
        for (r in routes) {
            val runsOfRoute = viewModel.selectAllRunsByRoute(r.id)
            totalKm += runsOfRoute.size * r.distance
        }

        var totalTime = 0.00
        for (r in runs) {
            totalTime += r.times.sum()
        }

        var favRoute: String = "NaN"
        var runsOfRoute = 0
        for (r in routes) {
            if (runsOfRoute < viewModel.selectAllRunsByRoute(r.id).size) {
                runsOfRoute = viewModel.selectAllRunsByRoute(r.id).size
                favRoute = r.name
            }
        }

        val averageKmTime = totalTime / totalKm

        /*
        * Sets values to TextViews in corners
        * */
        binding.apply {
            txtViewStartFrameTotalKmValue.text = viewModel.resources.getString(R.string.km, totalKm)
            txtViewStartFrameTotalTimeValue.text =
                viewModel.resources.getString(R.string.hours, totalTime/60) // hours
            txtViewStartFrameFavRouteValue.text = favRoute
            txtViewStartFrameAverageKmTimeValue.text =
                viewModel.resources.getString(R.string.minutes, averageKmTime)
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_start_frame, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_newRoute -> {
                findNavController().navigate(R.id.action_fragmentStartFrame_to_fragmentEditRoutes)
                true
            }
            R.id.menu_manageRuns -> {
                findNavController().navigate(R.id.action_fragmentStartFrame_to_fragmentManageRuns)
                true
            }
            R.id.menu_settings -> {
                findNavController().navigate(R.id.action_fragmentStartFrame_to_fragmentSettings)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}