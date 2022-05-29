package io.github.kabirnayeem99.materialgraphlib

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import io.github.kabirnayeem99.materialgraphlib.databinding.FragmentFirstBinding
import io.github.kabirnayeem99.materialgraphlibrary.Bar


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val points = ArrayList<Bar>()
        val d = Bar()
        d.color = ContextCompat.getColor(
            requireContext(),
            androidx.appcompat.R.color.material_blue_grey_800
        )
        d.name = "Test1"
        d.value = 10F
        val d2 = Bar()
        d2.color = ContextCompat.getColor(
            requireContext(),
            androidx.appcompat.R.color.material_deep_teal_500
        )
        d2.name = "Test2"
        d2.value = 20F
        points.add(d)
        points.add(d2)

        binding.textviewFirst.bars = points
        binding.textviewFirst.unit = "$"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}