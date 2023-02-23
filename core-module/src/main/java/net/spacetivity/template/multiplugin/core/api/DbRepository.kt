package net.spacetivity.template.singleplugin.api

interface DbRepository<T, K> {

    fun save(value: T)
    fun delete(value: T)
    fun find(key: K): T?
    fun findAll(): List<T>

}