package com.tomlonghurst.plexus

object Plexus {

    fun get(path: String, parameters: Parameters? = null) = PlexusClient.instance.get(path, parameters)

    fun post(path: String, parameters: Parameters? = null) = PlexusClient.instance.post(path, parameters)

    fun put(path: String, parameters: Parameters? = null) = PlexusClient.instance.put(path, parameters)

    fun delete(path: String, parameters: Parameters? = null) = PlexusClient.instance.delete(path, parameters)

    fun head(path: String, parameters: Parameters? = null) = PlexusClient.instance.head(path, parameters)

    fun patch(path: String, parameters: Parameters? = null) = PlexusClient.instance.patch(path, parameters)

}