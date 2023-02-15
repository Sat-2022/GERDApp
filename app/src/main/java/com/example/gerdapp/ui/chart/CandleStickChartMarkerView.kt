package com.example.gerdapp.ui.chart

import android.content.Context
import android.view.View
import android.widget.TextView
import com.example.gerdapp.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class CandleStickChartMarkerView(context: Context?, layoutResource: Int): MarkerView(context,
    layoutResource
) {
    private val mContentTv: TextView = findViewById<View>(R.id.tv_markerview) as TextView

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        mContentTv.text = e!!.x.toString()
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-(1000)).toFloat())
    }
}