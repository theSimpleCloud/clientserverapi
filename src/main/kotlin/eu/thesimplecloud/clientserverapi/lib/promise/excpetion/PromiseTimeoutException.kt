package eu.thesimplecloud.clientserverapi.lib.promise.excpetion

class PromiseTimeoutException(message: String, throwable: Throwable) : Exception(message, throwable) {
}