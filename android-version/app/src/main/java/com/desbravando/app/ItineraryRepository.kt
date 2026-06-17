package com.desbravando.app

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

data class SavedItinerary(
    val id: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val locationsCount: Int = 0
)

object ItinerariesRepository {
    fun getItineraries(onResult: (List<SavedItinerary>) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            onResult(emptyList())
            return
        }
        Firebase.firestore
            .collection("users")
            .document(uid)
            .collection("itineraries")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.map { doc ->
                    SavedItinerary(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: "",
                        locationsCount = (doc.get("locations") as? List<*>)?.size ?: 0
                    )
                }
                onResult(list)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}