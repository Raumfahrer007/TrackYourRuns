package com.example.trackyourruns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.trackyourruns.database.Route
import com.example.trackyourruns.database.Run
import com.example.trackyourruns.databinding.FragmentStatisticsBinding
import java.time.LocalDateTime

/*
* Displays Statistics of all Routes
* */
class FragmentStatistics : Fragment() {

    /*
    * Binding and ViewModel
    * */
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TyrViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        // Adapter for RecyclerView
        val adapter = AdapterStatistics(viewModel, viewModel.selectAllRoutes())
        binding.recyclerViewStatistics.adapter = adapter

        return binding.root
    }

    private class AdapterStatistics(
        private val viewModel: TyrViewModel,
        private val routes: List<Route>
    ) : RecyclerView.Adapter<AdapterStatistics.ViewHolderStatistics>() {

        class ViewHolderStatistics(view: View) : RecyclerView.ViewHolder(view) {
            val txtViewRoute: TextView = view.findViewById(R.id.txtView_StatisticsRouteName)
            val txtViewFail: TextView = view.findViewById(R.id.txtView_StatisticsFail)

            // Total
            val txtViewTotalValueKm: TextView =
                view.findViewById(R.id.txtView_StatisticsTotalValueKm)
            val txtViewTotalValueTime: TextView =
                view.findViewById(R.id.txtView_StatisticsTotalValueTime)
            val txtViewTotalValueRuns: TextView =
                view.findViewById(R.id.txtView_StatisticsTotalValueRuns)

            // Average
            val txtViewAverageValueTime: TextView =
                view.findViewById(R.id.txtView_StatisticsAverageValueTime)
            val txtViewAverageValueKmTime: TextView =
                view.findViewById(R.id.txtView_StatisticsAverageValueKmTime)
            val txtViewAverageValueIntensity: TextView =
                view.findViewById(R.id.txtView_StatisticsAverageValueIntensity)

            // Best
            val txtViewBestDate: TextView = view.findViewById(R.id.txtView_StatisticsBestDate)
            val txtViewBestValueTime: TextView =
                view.findViewById(R.id.txtView_StatisticsBestValueTime)
            val txtViewBestValueKmTime: TextView =
                view.findViewById(R.id.txtView_StatisticsBestValueKmTime)
            val txtViewBestValueIntensity: TextView =
                view.findViewById(R.id.txtView_StatisticsBestValueIntensity)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderStatistics {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_statistics_item, parent, false)
            return ViewHolderStatistics(view)
        }

        override fun onBindViewHolder(holder: ViewHolderStatistics, position: Int) {
            val route = routes[position]
            val runs: List<Run> = viewModel.selectAllRunsByRoute(route.id)
            holder.txtViewRoute.text = route.name
            if (runs.isEmpty()) {
                holder.txtViewFail.setText(R.string.statistics_fail)
                return
            }
            /*
            * Total
            * */
            val totalKm = runs.size * route.distance
            var totalTime = 0.00
            for (r in runs) {
                totalTime += r.times.sum()
            }
            val totalRuns = runs.size
            holder.apply {
                txtViewTotalValueKm.text = viewModel.resources.getString(R.string.km, totalKm)
                txtViewTotalValueTime.text =
                    viewModel.resources.getString(R.string.minutes, totalTime)
                txtViewTotalValueRuns.text = totalRuns.toString()
            }

            /*
            * Average
            * */
            val averageKmTime = totalTime / totalKm
            val averageTotalTime = totalTime / totalRuns
            var averageIntensity = 0
            for (r in runs) {
                averageIntensity += r.intensity
            }
            averageIntensity /= totalRuns
            holder.apply {
                txtViewAverageValueTime.text =
                    viewModel.resources.getString(R.string.minutes, averageTotalTime)
                txtViewAverageValueKmTime.text =
                    viewModel.resources.getString(R.string.minutes, averageKmTime)
                txtViewAverageValueIntensity.text = averageIntensity.toString()
            }

            /*
            * Best
            * */
            var bestTime = Double.MAX_VALUE
            var bestAverageKmTime = 0.00
            var bestIntensity = 0
            var bestDate: LocalDateTime? = null
            for (r in runs) {
                if (r.times.sum() < bestTime) {
                    bestTime = r.times.sum()
                    bestAverageKmTime = r.times.average()
                    bestIntensity = r.intensity
                    bestDate = r.startTime
                }
            }
            holder.apply {
                txtViewBestDate.text = TyrViewModel.dateTimeToStringDisplay(bestDate!!)
                txtViewBestValueTime.text =
                    viewModel.resources.getString(R.string.minutes, bestTime)
                txtViewBestValueKmTime.text =
                    viewModel.resources.getString(R.string.minutes, bestAverageKmTime)
                txtViewBestValueIntensity.text = bestIntensity.toString()
            }

        }

        override fun getItemCount(): Int {
            return routes.size
        }
    }
}