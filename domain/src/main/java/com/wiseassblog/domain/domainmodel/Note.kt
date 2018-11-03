package com.wiseassblog.domain.domainmodel


data class Note(val creationDate:String,
                val contents:String,
                val upVotes: Int,
                //why String? some times it will be Int from Android Resources, or URL String
                val color: ColorType,
                val creator: User?){

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

