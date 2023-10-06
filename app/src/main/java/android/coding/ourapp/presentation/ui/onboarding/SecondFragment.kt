package android.coding.ourapp.presentation.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.coding.ourapp.R
import android.coding.ourapp.databinding.FragmentSecondBinding
import androidx.viewpager2.widget.ViewPager2


class SecondFragment : Fragment() {


    private var _binding : FragmentSecondBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        next()
        back()
    }
    private fun next(){
        val viewPager =  activity?.findViewById<ViewPager2>(R.id.viewPager)
        binding.next.setOnClickListener {
            viewPager?.currentItem = 2
        }
    }

    private fun back(){
        val viewPager =  activity?.findViewById<ViewPager2>(R.id.viewPager)
        binding.back.setOnClickListener {
            viewPager?.currentItem = 0
        }
    }


}