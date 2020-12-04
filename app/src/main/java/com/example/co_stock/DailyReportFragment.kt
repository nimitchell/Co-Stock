package com.example.co_stock

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_daily_report.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass.
 * Use the [DailyReportFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DailyReportFragment : Fragment() {

    val viewModel: UserViewModel by activityViewModels<UserViewModel>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatted = current.format(formatter)
        viewModel.apiManager?.value?.fetchDailyImage(formatted)



        val dailyScore = viewModel.calculateDailyScore()
        quote_textView.text = viewModel.getRandomQuote()
        dailyReport_text.text = viewModel.getDailyReport(dailyScore)

        // TODO get the diff indecies
        firstIndex_button.text = ""
        secondIndex_button.text = ""
        thirdIndex_button.text = ""
        fourthIndex_button.text = ""

        firstIndex_button.setOnClickListener {
            viewModel.currentIndex.value = ""
            findNavController().navigate(R.id.action_dailyReportFragment_to_detailReportFragment)
        }
        secondIndex_button.setOnClickListener {
            viewModel.currentIndex.value = ""
            findNavController().navigate(R.id.action_dailyReportFragment_to_detailReportFragment)
        }
        thirdIndex_button.setOnClickListener {
            viewModel.currentIndex.value = ""
            findNavController().navigate(R.id.action_dailyReportFragment_to_detailReportFragment)
        }
        fourthIndex_button.setOnClickListener {
            viewModel.currentIndex.value = ""
            findNavController().navigate(R.id.action_dailyReportFragment_to_detailReportFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_report, container, false)
    }

}