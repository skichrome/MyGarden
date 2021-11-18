package fr.skichrome.garden.util

sealed class AppResult<out R>
{
    data class Success<out T>(val data: T) : AppResult<T>()
    data class Error(val exception: Throwable) : AppResult<Nothing>()
    object Loading : AppResult<Nothing>()

    override fun toString(): String
    {
        return when (this)
        {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}