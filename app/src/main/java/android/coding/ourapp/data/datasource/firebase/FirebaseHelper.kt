package android.coding.ourapp.data.datasource.firebaseimport com.google.firebase.database.DatabaseReferenceimport com.google.firebase.database.FirebaseDatabaseclass FirebaseHelper {    val dbReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Student")    val dbReferences: DatabaseReference = FirebaseDatabase.getInstance().getReference("report")}