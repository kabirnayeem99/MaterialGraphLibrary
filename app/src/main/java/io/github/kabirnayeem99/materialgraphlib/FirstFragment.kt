package io.github.kabirnayeem99.materialgraphlib

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.github.kabirnayeem99.materialgraphlib.databinding.FragmentFirstBinding
import io.github.kabirnayeem99.materialgraphlibrary.Bar
import io.github.kabirnayeem99.materialgraphlibrary.Line
import io.github.kabirnayeem99.materialgraphlibrary.LinePoint
import io.github.kabirnayeem99.materialgraphlibrary.PieSlice


class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBarChart()
        setUpPieChart()
        setUpLineChart()
    }

    private fun setUpPieChart() {
        val slices = ArrayList<PieSlice>()

        val firstPieSlice = PieSlice()
        firstPieSlice.title = "Test1"
        firstPieSlice.value = 80F
        firstPieSlice.color = Color.parseColor("#cd22dd")

        val secondPieSlice = PieSlice()
        secondPieSlice.title = "Test2"
        secondPieSlice.value = 20F
        secondPieSlice.color = Color.parseColor("#ff" + 44 + "bb")


        slices.add(firstPieSlice)
        slices.add(secondPieSlice)

        binding.pgPie.apply {
            slices.forEach { pieSlice -> addSlice(pieSlice) }
        }
    }

    private fun setUpBarChart() {

        val points = ArrayList<Bar>()
        val firstBar = Bar()

        firstBar.color = Color.parseColor("#cd" + 32 + "fa")
        firstBar.name = "Test1"
        firstBar.value = 10F

        val secondBar = Bar()
        secondBar.color = Color.parseColor("#ef" + 11 + "af")
        secondBar.name = "Test2"
        secondBar.value = 20F

        points.add(firstBar)
        points.add(secondBar)

        binding.bgBars.apply {
            bars = points
            unit = "$"
        }
    }

    private fun setUpLineChart() {
        val line = Line()
        var linePoint = LinePoint()
        linePoint.x = 0F
        linePoint.y = 5F
        line.addPoint(linePoint)
        linePoint = LinePoint()
        linePoint.x = 8F
        linePoint.y = 8F
        line.addPoint(linePoint)
        linePoint = LinePoint()
        linePoint.x = 10F
        linePoint.y = 4F
        line.addPoint(linePoint)
        line.color = (Color.parseColor("#FFBB33"))

        binding.lgLines.apply {
            addLine(line)
            setRangeY(0f, 10f)
            setLineToFill(0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}