package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.adapters.FeedAdapter
import eu.mcomputing.mobv.zadanie.data.DataRepository
import eu.mcomputing.mobv.zadanie.data.db.entities.UserEntity
import eu.mcomputing.mobv.zadanie.viewmodels.FeedViewModel
import eu.mcomputing.mobv.zadanie.widgets.BottomBar
import eu.mcomputing.mobv.zadanie.databinding.FragmentFeedBinding
import eu.mcomputing.mobv.zadanie.interfaces.ItemInterface

class FeedFragment : Fragment() {
    private lateinit var viewModel: FeedViewModel
    private lateinit var binding: FragmentFeedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[FeedViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            bnd.bottomBar.setActive(BottomBar.FEED)

            bnd.feedRecyclerview.layoutManager = LinearLayoutManager(context)
            val feedAdapter = FeedAdapter(object : ItemInterface{
                override fun onItemClick(userEntity: UserEntity) {
                    val userProfileBundle = Bundle().apply {
                        putString("userId", userEntity.uid)
                        putString("lastUpdate", userEntity.updated)
                    }
                    view.findNavController().navigate(R.id.action_feedFragment_to_otherProfileFragment, userProfileBundle)
                }
            })
            bnd.feedRecyclerview.adapter = feedAdapter

            // Pozorovanie zmeny hodnoty
            viewModel.feed_items.observe(viewLifecycleOwner) { items ->
                Log.d("FeedFragment", "nove hodnoty $items")
                feedAdapter.updateItems(items ?: emptyList())
            }

            bnd.pullRefresh.setOnRefreshListener {
                viewModel.updateItems()
            }
            viewModel.loading.observe(viewLifecycleOwner) {
                bnd.pullRefresh.isRefreshing = it
            }
        }

    }
}