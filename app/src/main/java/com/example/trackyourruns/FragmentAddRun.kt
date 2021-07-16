package com.example.trackyourruns

import android.os.Bundle
import android.text.InputType
import android.text.format.DateFormat.is24HourFormat
import android.view.*
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.trackyourruns.database.Run
import com.example.trackyourruns.databinding.FragmentAddRunBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

const val TAG_DATE = "date"
const val TAG_TIME = "time"

/*
* Add or edit a Run
* */
class FragmentAddRun : Fragment() {

    /*
    * Binding and ViewModel
    * */
    private var _binding: FragmentAddRunBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TyrViewModel by activityViewModels()

    private lateinit var routes: List<String>   // List with all saved Routes to select from

    private var oldRun: Run? = null // gets initialized when a saved route gets edited

    /*
    * Values of EditTexts of DatePicker and TimePicker -> combined to 'startDateTime'
    * */
    private var date: LocalDate? = null
    private var startTime: LocalTime? = null

    /*
    * Values to create Run
    * */
    private var startDateTime: LocalDateTime? = null
    private var routeID: Int? = null
    private var kmTimes: MutableList<Double> = mutableListOf()
    private var intensity: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        routes = viewModel.selectAllRouteNames()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRunBinding.inflate(inflater, container, false)

        // gets called when a saved run gets edited - fills editTexts
        if (arguments?.isEmpty == false) {
            oldRun = viewModel.selectRun(requireArguments().getInt(FragmentManageRuns.TAG_RUN_ID))

            binding.editTxtDate.setText(TyrViewModel.dateTimeToStringDate(oldRun!!.startTime))
            date = TyrViewModel.dateTimeToDate(oldRun!!.startTime)

            binding.editTxtTime.setText(TyrViewModel.dateTimeToStringTime(oldRun!!.startTime))
            startTime = TyrViewModel.dateTimeToTime(oldRun!!.startTime)

            binding.autoTxtViewRoute.setText(viewModel.selectRoute(oldRun!!.routeID).name)
            routeID = oldRun!!.routeID
            createKmFields(viewModel.selectRoute(oldRun!!.routeID).name)
            for (i in 0 until binding.linearLayoutKm.childCount) {
                val txtViewKm = binding.linearLayoutKm.getChildAt(i) as TextView
                txtViewKm.text = oldRun!!.times[i].toString()
            }

            binding.sliderIntensity.value = oldRun!!.intensity.toFloat()
            binding.btnDeleteRun.visibility = View.VISIBLE
        }

        /*
        * Date-Picker
        * */
        val datePicker =
            MaterialDatePicker.Builder.datePicker().setTitleText(R.string.select_date)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()
        datePicker.addOnPositiveButtonClickListener {
            val tempDate = Date(datePicker.selection!!)
            date = LocalDate.parse(TyrViewModel.dateToStringFormat(tempDate))
            binding.editTxtDate.setText(TyrViewModel.dateToStringDisplay(tempDate))
        }
        binding.editTxtDate.setOnClickListener {
            datePicker.show(parentFragmentManager, TAG_DATE)
        }

        /*
        * Time-Picker
        * */
        val clockFormat =
            if (is24HourFormat(context)) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H //24h or 12h
        val timePicker = MaterialTimePicker.Builder().setTimeFormat(clockFormat)
            .setTitleText(R.string.select_time).build()
        timePicker.addOnPositiveButtonClickListener {
            startTime = LocalTime.of(timePicker.hour, timePicker.minute)

            val tempStartDateTime = LocalDateTime.of(1, 1, 1, timePicker.hour, timePicker.minute)
            val timeFormatter = if (clockFormat == TimeFormat.CLOCK_24H) {
                DateTimeFormatter.ofPattern("HH:mm")  // Displays Time in 24h
            } else {
                DateTimeFormatter.ofPattern("hh:mm a")    // Displays Time in 12h
            }
            binding.editTxtTime.setText(timeFormatter.format(startTime))
        }
        binding.editTxtTime.setOnClickListener {
            timePicker.show(parentFragmentManager, TAG_TIME)
        }

        /*
        * Spinner with Routes
        * */
        val routeSpinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, routes)
        binding.autoTxtViewRoute.setAdapter(routeSpinnerAdapter)

        binding.autoTxtViewRoute.setOnItemClickListener { _, _, i, _ ->
            routeID = viewModel.selectRoute(routes[i]).id
            createKmFields(routes[i])
        }


        binding.sliderIntensity.addOnChangeListener { _, value, _ ->
            intensity = value.toInt()
        }

        binding.btnSaveRun.setOnClickListener {
            saveRun()
        }

        binding.btnDeleteRun.setOnClickListener {
            deleteRun()
        }

        return binding.root
    }

    /*
    * Creates EditTexts for km-Times after choosing Route from Spinner
    * */
    private fun createKmFields(route: String) {
        binding.linearLayoutKm.removeAllViews()
        val km = viewModel.selectRoute(route).distance
        for (i in 1..km) {
            val editTxtKm = EditText(context)
            editTxtKm.apply {
                hint = resources.getString(R.string.km_count, i)
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            }
            binding.linearLayoutKm.addView(editTxtKm)
        }
    }

    private fun saveRun() {
        if (date != null && startTime != null && routeID != null && intensity != null) {
            startDateTime = LocalDateTime.of(date, startTime)
            /*
            * checks and stores time for every km
            * */
            for (i in 0 until binding.linearLayoutKm.childCount) {
                val editTxtTime = binding.linearLayoutKm[i] as EditText
                if (!editTxtTime.text.isNullOrBlank()) {
                    kmTimes.add(
                        i,
                        BigDecimal(editTxtTime.text.toString().toDouble()).setScale(
                            2,
                            RoundingMode.HALF_EVEN
                        ).toDouble()   // Double with only two decimals
                    )
                } else {
                    Toast.makeText(context, R.string.km_time_fail, Toast.LENGTH_SHORT).show()
                    return
                }
            }
            // saves run in Database
            val run = Run(0, startDateTime!!, routeID!!, kmTimes, intensity!!)
            viewModel.insertRun(run)

            findNavController().navigateUp()

        } else {
            Toast.makeText(context, R.string.entered_data_fail, Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteRun() {
        viewModel.deleteRun(oldRun!!)
        findNavController().navigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add_run, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add_run_import_data -> {
                Toast.makeText(context, R.string.import_fail, Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}