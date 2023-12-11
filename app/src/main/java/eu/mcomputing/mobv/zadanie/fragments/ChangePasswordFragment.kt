package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.data.DataRepository
import eu.mcomputing.mobv.zadanie.data.PreferenceData
import eu.mcomputing.mobv.zadanie.databinding.FragmentChangePasswordBinding
import eu.mcomputing.mobv.zadanie.databinding.FragmentSignupBinding
import eu.mcomputing.mobv.zadanie.viewmodels.AuthViewModel

class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {
    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: FragmentChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[AuthViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->

            viewModel.changePasswordResult.observe(viewLifecycleOwner) {
                Snackbar.make(
                    bnd.submitChangePassword,
                    it.first,
                    Snackbar.LENGTH_SHORT
                ).show()
                if (it.second) {
                    requireView().findNavController().navigate(R.id.action_changePasswordFragment_to_profileFragment)
                }
            }
        }
    }
}
