package com.rivaldofez.consumerapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rivaldofez.consumerapp.adapter.DetailPagerAdapter
import com.rivaldofez.consumerapp.adapter.FollowAdapter
import com.rivaldofez.consumerapp.databinding.FragmentFollowBinding
import com.rivaldofez.consumerapp.listener.OnItemClickListener
import com.rivaldofez.consumerapp.model.User
import com.rivaldofez.consumerapp.viewmodel.FollowViewModel

class FollowFragment : Fragment() {
    companion object{
        const val KEY_USERNAME = "username"
        const val KEY_OPTION = "option"
        const val KEY_DETAIL_USER = "detail"
    }

    private val layoutManager = LinearLayoutManager(activity)
    var username:String? = null
    var option:String? = null
    private lateinit var binding: FragmentFollowBinding
    private lateinit var followerViewModel: FollowViewModel
    private lateinit var followingViewModel: FollowViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState!=null){
            username = savedInstanceState.getString(KEY_USERNAME)
            option = savedInstanceState.getString(KEY_OPTION)
        }

        if(option!! == DetailPagerAdapter.endFollowers){
            val followerAdapter = FollowAdapter(requireActivity())
            binding.rvFollowers.layoutManager = layoutManager
            binding.rvFollowers.adapter = followerAdapter
            action(followerAdapter)

            followerViewModel = ViewModelProvider(requireActivity() as AppCompatActivity,ViewModelProvider.NewInstanceFactory()).get(
                FollowViewModel::class.java)
            followerViewModel.initializeModel()
            username?.let { username -> option?.let { option -> followerViewModel.getFollowUser(username, option) } }

            followerViewModel.listFollowsUser.observe(viewLifecycleOwner,{
                followerAdapter.setFollows(it)
            })

            followerViewModel.showProgress.observe(viewLifecycleOwner,{
                if (it)
                    binding.progressBar.visibility = View.VISIBLE
                else
                    binding.progressBar.visibility = View.GONE
            })
        }else{
            val followingAdapter = FollowAdapter(requireActivity())
            binding.rvFollowers.layoutManager = layoutManager
            binding.rvFollowers.adapter = followingAdapter

            action(followingAdapter)
            followingViewModel = ViewModelProvider(requireActivity() as AppCompatActivity,ViewModelProvider.NewInstanceFactory()).get(
                FollowViewModel::class.java)
            followingViewModel.initializeModel()

            username?.let { username -> option?.let { option -> followingViewModel.getFollowUser(username, option) } }

            followingViewModel.listFollowsUser.observe(viewLifecycleOwner,{
                followingAdapter.setFollows(it)
            })

            followingViewModel.showProgress.observe(viewLifecycleOwner,{
                if (it)
                    binding.progressBar.visibility = View.VISIBLE
                else
                    binding.progressBar.visibility = View.GONE
            })
        }
    }

    private fun action(adapter: FollowAdapter) {
        adapter.setOnClickItemListener(object : OnItemClickListener {
            override fun onItemClick(item: View, userSearch: User) {
                val arg = Bundle()
                arg.putParcelable(KEY_DETAIL_USER, userSearch)

                val detailDialogFragment = DetailDialogFragment()
                detailDialogFragment.arguments = arg

                detailDialogFragment.show(requireActivity().supportFragmentManager, "Detail Dialog")
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_USERNAME, username)
        outState.putString(KEY_OPTION, option)
    }

}