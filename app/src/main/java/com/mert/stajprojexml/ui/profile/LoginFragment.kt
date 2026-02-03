package com.mert.stajprojexml.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mert.stajprojexml.R
import com.mert.stajprojexml.data.local.AppDatabase
import com.mert.stajprojexml.data.local.SessionManager
import com.mert.stajprojexml.data.local.UserEntity
import com.mert.stajprojexml.data.repository.FavoriteRepository
import com.mert.stajprojexml.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch
import java.util.UUID

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var binding: FragmentLoginBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val b = FragmentLoginBinding.bind(view)
        binding = b

        val session = SessionManager(requireContext())
        val db = AppDatabase.get(requireContext())
        val favRepo = FavoriteRepository(requireContext())

        b.loginButton.setOnClickListener {
            val name = b.nameInput.text?.toString()?.trim().orEmpty()
            val email = b.emailInput.text?.toString()?.trim().orEmpty()
            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Email gerekli", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val userId = email.ifEmpty { UUID.randomUUID().toString() }

            viewLifecycleOwner.lifecycleScope.launch {
                db.userDao().upsert(
                    UserEntity(
                        id = userId,
                        name = name.ifEmpty { null },
                        email = email
                    )
                )
                favRepo.migrateGuestFavoritesTo(userId)
                session.setCurrentUserId(userId)
                Toast.makeText(requireContext(), "Giriş yapıldı", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
