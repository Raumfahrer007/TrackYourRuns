package com.example.trackyourruns

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.trackyourruns.database.Route
import com.example.trackyourruns.databinding.FragmentAddRouteBinding

/*
* Add or edit a Route
* */
class FragmentAddRoute : Fragment() {

    /*
    * Binding and ViewModel
    * */
    private var _binding: FragmentAddRouteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TyrViewModel by activityViewModels()

    private var oldRoute: Route? = null // gets initialized when a saved route gets edited

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddRouteBinding.inflate(layoutInflater, container, false)

        // gets called when a saved route gets edited - fills editTexts
        if (arguments?.isEmpty == false) {
            val id = requireArguments().getInt(FragmentEditRoutes.ID)
            oldRoute = viewModel.selectRoute(id)
            binding.editTxtName.setText(oldRoute!!.name)
            binding.editTxtDistance.setText(oldRoute!!.distance.toString())
            binding.btnDelete.visibility = View.VISIBLE
            // makes editText Distance not editable¼
            binding.editTxtDistance.isFocusable = false
            binding.editTxtDistance.setBackgroundColor(Color.LTGRAY)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSave.setOnClickListener {
            saveRoute()
        }
        binding.btnDelete.setOnClickListener {
            deleteRoute()
        }
    }

    // saves new or edited Route - checks if name is unique
    private fun saveRoute() {
        if (binding.editTxtName.text != null && binding.editTxtDistance.text != null) {
            val name = binding.editTxtName.text.toString()
            val distance = binding.editTxtDistance.text.toString().toInt()

            val nameCount: Int = viewModel.countRoutes(name)    // if name is free = 0

            // name already taken
            if (nameCount != 0 && oldRoute == null) {
                Toast.makeText(context, R.string.name_taken, Toast.LENGTH_SHORT).show()
                return
            }

            // saves new Route
            if (oldRoute == null) {
                val route = Route(0, name, distance)
                viewModel.insertRoute(route)

                // saves edited Route
            } else {
                val route = Route(oldRoute!!.id, name, distance)
                viewModel.updateRoute(route)
            }
            findNavController().navigateUp()
        }
    }

    private fun deleteRoute() {
        if (viewModel.countRouteUse(oldRoute!!.id) == 0) {
            viewModel.deleteRoute(oldRoute!!)
            findNavController().navigateUp()
        } else {
            Toast.makeText(context, R.string.delete_fail, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}