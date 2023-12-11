package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import eu.mcomputing.mobv.zadanie.R

class PasswordResetFragment : Fragment(R.layout.fragment_password_reset) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resetPasswordButton: Button = view.findViewById(R.id.submitPasswordReset)
        resetPasswordButton.setOnClickListener {
            view.findNavController().navigate(R.id.loginFragment)
        }
    }
}
