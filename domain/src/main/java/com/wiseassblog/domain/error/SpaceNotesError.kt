package com.wiseassblog.domain.error

import java.lang.Exception

sealed class SpaceNotesError: Exception() {

    object LocalIOException: SpaceNotesError()
    object RemoteIOException: SpaceNotesError()
    object NetworkUnavailableException: SpaceNotesError()
    object AuthError: SpaceNotesError()
    object TransactionIOException : SpaceNotesError()



}

const val ERROR_UPDATE_FAILED = "Update operation unsuccessful."
const val ERROR_DELETE_FAILED = "Delete operation unsuccessful."
