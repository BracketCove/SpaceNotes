package com.wiseassblog.domain.domainmodel

sealed class Result<out E, out V> {

    data class Value<out V>(val value: V) : Result<Nothing, V>()
    object Loading : Result<Nothing, Nothing>()
    data class Error<out E>(val error: E) : Result<E, Nothing>()

    companion object Factory{
        inline fun <V> build(function: () -> V): Result<Exception, V> =
                try {
                    Value(function.invoke())
                } catch (e: java.lang.Exception) {
                    Error(e)
                }

        fun buildError(error: Exception): Result<Exception, Nothing> {
            return Error(error)
        }

        fun buildLoading(): Result<Exception, Nothing> {
            return Loading
        }
    }

}