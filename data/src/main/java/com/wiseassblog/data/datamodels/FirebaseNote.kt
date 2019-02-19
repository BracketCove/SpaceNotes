package com.wiseassblog.data.datamodels

//var and default arguments used due to firestore requiring a no argument constructor to
//deserialize
data class FirebaseNote(
        var creationDate: String? = "",
        var contents: String? = "",
        var upVotes: Int? = 0,
        var imageurl: String? = "",
        var creator: String? = ""
)