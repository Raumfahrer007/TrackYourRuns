package com.example.trackyourruns

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.trackyourruns.database.Route
import com.example.trackyourruns.database.Run
import com.example.trackyourruns.databinding.FragmentCompareRouteBinding


const val WIDTH_COMPARE_TABLE_CELL = 250

/*
* Displays km-times of previous Runs among themselves to scroll through
* */
class FragmentCompareRoute : Fragment() {

    companion object {
        const val TAG_RUN_COUNT = "run_count"
        const val STANDARD_RUN_COUNT = 10
    }

    /*
    * Binding and ViewModel
    * */
    private var _binding: FragmentCompareRouteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TyrViewModel by activityViewModels()

    private lateinit var route: Route
    private lateinit var runs: List<Run>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        /*
        * gets RouteID from arguments and selects the corresponding Runs from Database
        * */
        route =
            viewModel.selectRoute(requireArguments().getInt(FragmentSelectRouteToCompare.ROUTE_ID))
        runs = viewModel.selectAllRunsByRoute(route.id).sortedByDescending { it.startTime }

        val runCount = requireArguments().getInt(TAG_RUN_COUNT) // how many runs should be displayed

        if (runCount > 0 && runCount < runs.size) {     // -1 -> All
            runs = runs.subList(0, runCount)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCompareRouteBinding.inflate(inflater, container, false)

        // Adapter for RecyclerView
        val adapter = AdapterCompareRun(binding.scrollViewKm, runs, requireContext())
        binding.recyclerViewCompare.adapter = adapter

        /*
        * TextViews for km in first line
        * */
        for (i in 1..route.distance) {
            val txtViewKm = createTextView(viewModel.resources.getString(R.string.km_count, i))
            binding.linearLayoutKm.addView(txtViewKm)
        }

        /*
        * TextView for other Values
        * */
        val txtViewAverage = createTextView(getString(R.string.average_short))  // Average
        binding.linearLayoutKm.addView(txtViewAverage)

        val txtViewTotal = createTextView(getString(R.string.total))    // Total Time
        binding.linearLayoutKm.addView(txtViewTotal)

        val txtViewIntensity = createTextView(getString(R.string.intensity_short))  // Intensity
        binding.linearLayoutKm.addView(txtViewIntensity)

        /*
        * OnChangeListener for ScrollViewKm in first Line
        * */
        binding.scrollViewKm.setOnScrollChangeListener { _, i, _, i3, _ ->
            for (s in adapter.scrollViews) {
                s.scrollTo(
                    i,
                    i3
                )   // scrolls every ScrollView in List 'scrollViews' to same position synchronously
                adapter.scrollViewX =
                    s.scrollX // stores current X-Position of ScrollViews in 'scrollViewX'
            }
        }

        return binding.root
    }

    /*
    * Creates Text Views for first line
    * */
    private fun createTextView(text: String): TextView {
        val txtView = TextView(context)
        txtView.apply {
            setBackgroundResource(R.drawable.background_border_left_thin)
            setTextAppearance(R.style.TextStyleCompareTableHeadlines)
            setText(text)
            width = WIDTH_COMPARE_TABLE_CELL
            gravity = Gravity.CENTER
        }
        return txtView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_compare_route, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /*
    * Selects how many Runs should be displayed
    * */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val runCount = when (item.itemId) {
            R.id.menu_ten -> 10
            R.id.menu_twenty_five -> 25
            R.id.menu_fifty -> 50
            R.id.menu_all -> -1
            else -> return super.onOptionsItemSelected(item)
        }

        // navigates to same Fragment
        val bundle =
            bundleOf(FragmentSelectRouteToCompare.ROUTE_ID to route.id, TAG_RUN_COUNT to runCount)
        findNavController().navigate(R.id.action_fragmentCompareRoute_self, bundle)
        return true
    }

    private class AdapterCompareRun(
        scrollViewKm: HorizontalScrollView,
        private val runs: List<Run>,
        private val context: Context
    ) :
        RecyclerView.Adapter<AdapterCompareRun.ViewHolderRun>() {

        val scrollViews: MutableSet<HorizontalScrollView> =
            mutableSetOf(scrollViewKm)  // Set with all ScrollViews bound to the RecyclerView
        var scrollViewX: Int = 0    // current x-Position of all ScrollViews

        class ViewHolderRun(view: View) : RecyclerView.ViewHolder(view) {
            val txtViewRun: TextView = view.findViewById(R.id.txtView_run)
            val linearLayoutTimes: LinearLayout =
                view.findViewById(R.id.linearLayout_values)
            val scrollViewTimes: HorizontalScrollView = view.findViewById(R.id.scrollView_values)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRun {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_compare_run_item, parent, false)

            return ViewHolderRun(view)
        }

        override fun onBindViewHolder(holder: ViewHolderRun, position: Int) {

            /*
            * TextView with Date and Time
            * */
            holder.txtViewRun.apply {
                text = TyrViewModel.dateTimeToStringDisplay(runs[position].startTime)
                setTextAppearance(R.style.TextStyleCompareTableDateTimes)
            }

            /*
            * LinearLayout with km-times
            * */
            holder.linearLayoutTimes.removeAllViews()
            val times: List<Double> = runs[position].times
            for (i in times.indices) {
                val time = times[i]
                val previousTime =
                    if (position < runs.size - 1) runs[position + 1].times[i] else null
                val txtViewTime = createTextView(position, time, previousTime)
                holder.linearLayoutTimes.addView(txtViewTime)
            }

            /*
            * other Values
            * */
            val previousAverage =
                if (position < runs.size - 1) runs[position + 1].times.average() else null
            val txtViewAverage = createTextView(position, times.average(), previousAverage)
            holder.linearLayoutTimes.addView(txtViewAverage)

            val previousTotal =
                if (position < runs.size - 1) runs[position + 1].times.sum() else null
            val txtViewTotal = createTextView(position, times.sum(), previousTotal)
            holder.linearLayoutTimes.addView(txtViewTotal)

            val previousIntensity =
                if (position < runs.size - 1) runs[position + 1].intensity.toDouble() else null
            val txtViewIntensity =
                createTextView(position, runs[position].intensity.toDouble(), previousIntensity)
            holder.linearLayoutTimes.addView(txtViewIntensity)

            /*
            * HorizontalScrollView with LinearLayoutValues
            * */
            holder.scrollViewTimes.apply {
                setOnScrollChangeListener { _, i, _, i3, _ ->
                    for (s in scrollViews) {
                        s.scrollTo(
                            i,
                            i3
                        )   // scrolls every ScrollView in List 'scrollViews' to same position
                        scrollViewX =
                            s.scrollX     // stores current X-Position of ScrollViews in 'scrollViewX'
                    }
                }

                scrollX =
                    scrollViewX   // sets ScrollView on current 'scrollViewX' position after creating
            }
            scrollViews.add(holder.scrollViewTimes) // adds ScrollView to Set 1
        }

        /*
        * Colours background depending on previous run
        * */
        private fun getBackgroundResource(value: Double, previousValue: Double): Int {
            return if ((value - previousValue) < 0) {
                R.drawable.background_compare_table_text_view_better
            } else if ((value - previousValue) > 0) {
                R.drawable.background_compare_table_text_view_worse
            } else {
                R.drawable.background_compare_table_text_view_equal
            }
        }

        /*
        * Creates TextViews for km-Times and other Values of Runs
        * */
        private fun createTextView(
            positionRV: Int,
            value: Double,
            previousValue: Double?
        ): TextView {
            val txtViewTime = TextView(context)
            txtViewTime.apply {
                txtViewTime.text = String.format("%.2f", value)
                setTextAppearance(R.style.TextStyleCompareTableTimes)
                gravity = Gravity.CENTER
                width = WIDTH_COMPARE_TABLE_CELL

                if (positionRV < runs.size - 1) {   // every line with ancestor
                    setBackgroundResource(getBackgroundResource(value, previousValue!!))

                } else {    // last line
                    setBackgroundResource(R.drawable.background_border_left_thin)
                }

            }
            return txtViewTime
        }

        override fun onViewAttachedToWindow(holder: ViewHolderRun) {
            super.onViewAttachedToWindow(holder)
            scrollViews.add(holder.scrollViewTimes) // adds ScrollView to Set 2
        }

        override fun onViewDetachedFromWindow(holder: ViewHolderRun) {
            super.onViewDetachedFromWindow(holder)
            scrollViews.remove(holder.scrollViewTimes)  // removes ScrollView from Set
        }

        override fun getItemCount(): Int {
            return runs.size
        }
    }
}