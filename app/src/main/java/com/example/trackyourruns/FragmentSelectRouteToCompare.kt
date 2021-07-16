package com.example.trackyourruns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.trackyourruns.database.Route
import com.example.trackyourruns.databinding.FragmentSelectRouteToCompareBinding

/*
* Displays Routes to select one to compare Runs
* */
class FragmentSelectRouteToCompare : Fragment() {

    companion object {
        const val ROUTE_ID = "route_id"
    }

    /*
    * Binding and ViewModel
    * */
    private var _binding: FragmentSelectRouteToCompareBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TyrViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectRouteToCompareBinding.inflate(inflater, container, false)

        /*
        * Creates List with all Routes
        * */
        val routes: List<Route> = viewModel.selectAllRoutes()
        for (r in routes) {
            val txtViewRoute = TextView(context)
            txtViewRoute.apply {
                text = r.name
                isClickable = true
                setPadding(TEXT_VIEW_ROUTE_PADDING_START, TEXT_VIEW_ROUTE_PADDING_TOP, 0, 0)
                setBackgroundResource(R.drawable.background_border_bottom)
                setTextAppearance(R.style.TextStyleRouteList)
                /*
                * Calls 'CompareRoute' with ID of clicked Route
                * */
                setOnClickListener {
                    val bundle = bundleOf(
                        ROUTE_ID to r.id,
                        FragmentCompareRoute.TAG_RUN_COUNT to FragmentCompareRoute.STANDARD_RUN_COUNT
                    )
                    findNavController().navigate(
                        R.id.action_fragmentSelectRouteToCompare_to_fragmentCompareRoute,
                        bundle
                    )
                }
                binding.linearLayoutRoutes.addView(txtViewRoute)
            }
        }

        return binding.root
    }

}