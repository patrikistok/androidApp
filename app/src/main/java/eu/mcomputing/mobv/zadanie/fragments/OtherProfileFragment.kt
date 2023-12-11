package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.data.DataRepository
import eu.mcomputing.mobv.zadanie.databinding.FragmentOtherProfileBinding
import eu.mcomputing.mobv.zadanie.viewmodels.ProfileViewModel
import eu.mcomputing.mobv.zadanie.widgets.BottomBar

class OtherProfileFragment : Fragment(R.layout.fragment_other_profile) {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentOtherProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ProfileViewModel::class.java]

        val userId = arguments?.getString("userId")
        if (userId != null) {
            viewModel.loadUser(userId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtherProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userUpdate = arguments?.getString("lastUpdate")
        val lastUpdatedString = getString(R.string.last_updated) + " " + userUpdate

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            bnd.bottomBar.setActive(BottomBar.PROFILE)

            viewModel.userResult.observe(viewLifecycleOwner) {
                Picasso.get()
                        .load("https://upload.mcomputing.eu/" + it?.photo)
                        .resize(150, 150)
                        .centerCrop()
                        .placeholder(R.drawable.baseline_person_2_24)
                        .into(bnd.userPhoto)
                }
            bnd.update.text = lastUpdatedString
        }

    }

}
