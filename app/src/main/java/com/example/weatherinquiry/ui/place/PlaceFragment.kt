package com.example.weatherinquiry.ui.place

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherinquiry.MainActivity
import com.example.weatherinquiry.databinding.FragmentPlaceBinding
import com.example.weatherinquiry.ui.weather.WeatherActivity


class PlaceFragment : Fragment() {
    val viewModel by lazy {
        ViewModelProvider(this)[PlaceViewModel::class.java]
    }
    private lateinit var adapter: PlaceAdapter
    private var _binding: FragmentPlaceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        if (viewModel.isPlaceSaved()){
//            val place=viewModel.getSavedPlace()
//            Log.d("this","place")
//            val intent=Intent(context,WeatherActivity::class.java).apply {
//                putExtra("location_lng",place.location.lng)
//                putExtra("location_lat",place.location.lat)
//                putExtra("place_name",place.name)
//            }
//            Log.d("this","intent")
//            startActivity(intent)
//            activity?.finish()
//            return
//            //return
//        }
//
        val layoutManager = LinearLayoutManager(activity)
        binding.placeRecyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)
        binding.placeRecyclerView.adapter = adapter



        binding.searchPlaceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                binding.placeRecyclerView.visibility = View.GONE
                binding.bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer { result ->
            val places = result.getOrNull()
            if (places != null) {
                binding.placeRecyclerView.visibility = View.VISIBLE
                binding.bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
                result.exceptionOrNull()?.printStackTrace()
            }

        })
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)


        //requireActivity() 返回的是宿主activity
        requireActivity().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event.targetState == Lifecycle.State.CREATED) {
                    //在这里任你飞翔
                    if (activity is MainActivity && viewModel.isPlaceSaved()) {
                        val place = viewModel.getSavedPlace()
                        val intent = Intent(context, WeatherActivity::class.java).apply {
                            putExtra("location_lng", place.location.lng)
                            putExtra("location_lat", place.location.lat)
                            putExtra("place_name", place.name)
                        }
                        startActivity(intent)
                        activity?.finish()
                        return
                    }
                    requireActivity().lifecycle.removeObserver(this) //这里是删除观察者
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}