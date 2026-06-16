package com.desbravando.app
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

object FavoritesRepository {
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    private fun favoritesRef() = auth.currentUser?.let { user ->
        db.collection("users").document(user.uid).collection("favorites")
    }

    fun toggleFavorite(locationId: String, locationData: Map<String, Any>) {
        favoritesRef()?.document(locationId)?.get()?.addOnSuccessListener { doc ->
            if (doc.exists()) doc.reference.delete()
            else doc.reference.set(locationData)
        }
    }

    fun isFavorited(locationId: String, onResult: (Boolean) -> Unit) {
        favoritesRef()?.document(locationId)?.get()
            ?.addOnSuccessListener { onResult(it.exists()) }
            ?: onResult(false)
    }

    fun getFavorites(onResult: (List<FavoriteLocation>) -> Unit) {
        favoritesRef()?.get()?.addOnSuccessListener { result ->
            val list = result.documents.mapNotNull { doc ->
                FavoriteLocation(
                    id = doc.id,
                    imageUrl = doc.getString("imageUrl") ?: "",
                    name = doc.getString("name") ?: "",
                    city = doc.getString("city") ?: "",
                    tags = (doc.get("tags") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                )
            }
            onResult(list)
        } ?: onResult(emptyList()) // Se não estiver logado, retorna uma lista vazia
    }
}
data class FavoriteLocation(
    val id: String = "",
    val imageUrl: String = "",
    val name: String = "",
    val city: String = "",
    val tags: List<String> = emptyList()
)
