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
import androidx.navigation.fragment.findNavController
import com.example.trackyourruns.database.Route
import com.example.trackyourruns.databinding.FragmentEditRoutesBinding

const val TAG = "FragmentEditRoute"
const val TEXT_VIEW_ROUTE_PADDING_START = 32
const val TEXT_VIEW_ROUTE_PADDING_TOP = 16

/*
* Displays all Routes to select one to edit or delete
* */
class FragmentEditRoutes : Fragment() {

    companion object {
        const val ID = "id"
    }

    /*
    * Binding and ViewModel
    * */
    private var _binding: FragmentEditRoutesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TyrViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditRoutesBinding.inflate(inflater, container, false)

        binding.floatingBtnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentEditRoutes_to_fragmentAddRoute)
        }

        /*
        * Displays list with Routes
        * */
        var routes: List<Route> = viewModel.selectAllRoutes()

        for (r in routes) {
            val txtViewRoute = TextView(context)
            txtViewRoute.apply {
                text = r.name
                isClickable = true
                setBackgroundResource(R.drawable.background_border_bottom)
                setPadding(TEXT_VIEW_ROUTE_PADDING_START, TEXT_VIEW_ROUTE_PADDING_TOP, 0, 0)
                setTextAppearance(R.style.TextStyleRouteList)
                setOnClickListener {
                    val bundle = bundleOf(ID to r.id)
                    findNavController().navigate(
                        R.id.action_fragmentEditRoutes_to_fragmentAddRoute,
                        bundle
                    )
                }
                binding.linearLayoutRoutesEdit.addView(txtViewRoute)
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}