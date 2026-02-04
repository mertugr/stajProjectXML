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
    private lateinit var session: SessionManager
    private lateinit var db: AppDatabase

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val b = FragmentProfileBinding.bind(view)
        binding = b
        session = SessionManager(requireContext())
        db = AppDatabase.get(requireContext())

        b.loginButton.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        b.logoutButton.setOnClickListener {
            session.setCurrentUserId(SessionManager.GUEST_ID)
            loadState()
        }

        loadState()
    }

    override fun onResume() {
        super.onResume()
        loadState() // refresh after returning from login
    }

    private fun renderGuest() {
        binding?.apply {
            guestGroup.visibility = View.VISIBLE
            userGroup.visibility = View.GONE
        }
    }

    private fun renderUser(name: String?, email: String?) {
        binding?.apply {
            guestGroup.visibility = View.GONE
            userGroup.visibility = View.VISIBLE
            userName.text = name ?: "Kullanıcı"
            userEmail.text = email ?: ""
        }
    }

    private fun loadState() {
        val current = session.currentUserId()
        if (session.isGuest()) {
            renderGuest()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val user = db.userDao().findById(current)
            if (user == null) {
                // stale user id; fallback to guest
                session.setCurrentUserId(SessionManager.GUEST_ID)
                renderGuest()
            } else {
                renderUser(user.name, user.email ?: current)
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
