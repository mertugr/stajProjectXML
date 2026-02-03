package com.mert.stajprojexml.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mert.stajprojexml.R
import com.mert.stajprojexml.data.local.AppDatabase
import com.mert.stajprojexml.data.local.SessionManager
import com.mert.stajprojexml.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile){
    private var binding: FragmentProfileBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val b = FragmentProfileBinding.bind(view)
        binding = b
        val session = SessionManager(requireContext())
        val db = AppDatabase.get(requireContext())

        fun renderGuest() {
            b.guestGroup.visibility = View.VISIBLE
            b.userGroup.visibility = View.GONE
        }

        fun renderUser(name: String?, email: String?) {
            b.guestGroup.visibility = View.GONE
            b.userGroup.visibility = View.VISIBLE
            b.userName.text = name ?: "Kullanıcı"
            b.userEmail.text = email ?: ""
        }

        fun loadState() {
            val current = session.currentUserId()
            if (session.isGuest()) {
                renderGuest()
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    val user = db.userDao().findById(current)
                    renderUser(user?.name, user?.email ?: current)
                }
            }
        }

        b.loginButton.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        b.logoutButton.setOnClickListener {
            session.setCurrentUserId(SessionManager.GUEST_ID)
            loadState()
        }

        loadState()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
