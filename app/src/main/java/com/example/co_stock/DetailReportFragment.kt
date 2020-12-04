package com.example.co_stock

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_detail_report.*

/**
 * A simple [Fragment] subclass.
 * Use the [DetailReportFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailReportFragment : Fragment() {

    val viewModel: UserViewModel by activityViewModels<UserViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val indexScore = viewModel.getIndexScore(viewModel.currentIndex.value!!)
        index_textView.text = viewModel.getIndexName(viewModel.currentIndex.value?.symbol!!)
        indexScore_text.text = indexScore.toString()
        var color = 0
        when(indexScore){
            1 -> color = resources.getColor(R.color.red)
            2 -> color = resources.getColor(R.color.orange)
            3 -> color = resources.getColor(R.color.yellow)
            4 -> color = resources.getColor(R.color.green_yellow)
            5 -> color = resources.getColor(R.color.green)
        }
        indexScore_text.setTextColor(color)
        indexReport_text.text = viewModel.getIndexReport(indexScore)
        index_description.text = viewModel.getIndexDescription(viewModel.currentIndex.value?.symbol!!)
    }

     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_report, container, false)
    }
}