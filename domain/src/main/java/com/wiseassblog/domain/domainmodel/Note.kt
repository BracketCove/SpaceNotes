package com.wiseassblog.domain.domainmodel


data class Note(val creationDate:String,
                val contents:String,
                val upVotes: Int,
                //why String? some times it will be Int from Android Resources, or URL String
                val imageUrl: String,
                val creator: User?)

