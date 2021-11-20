package fr.skichrome.garden.util

class NetworkUnavailableException(msg: String? = null, e: Throwable? = null) : Throwable(msg, e)
class ApiException(msg: String? = null, e: Throwable? = null) : Throwable(msg, e)