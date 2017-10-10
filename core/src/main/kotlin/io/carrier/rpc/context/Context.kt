package io.carrier.rpc.context

import java.lang.ClassCastException

class Context private constructor(parent: Context?, vararg entries: Pair<Key<*>, Any>) {
    constructor() : this(null)

    class Key<T> private constructor(val name: String) {
        fun get(): T? {
            return Context.current().lookup(this)
        }

        fun get(defaultValue: T): T {
            return Context.current().lookup(this, defaultValue)!!
        }

        override fun equals(other: Any?): Boolean {
            return other is Key<*> && name.equals(other.name, ignoreCase = true)
        }

        override fun hashCode(): Int {
            return name.toUpperCase().hashCode()
        }

        override fun toString(): String {
            return "Key<$name>"
        }

        companion object {
            fun <T> of(name: String): Key<T> {
                return Key(name)
            }
        }
    }

    private val data = mutableMapOf<Key<*>, Any>()

    init {
        parent?.data?.entries?.forEach { data[it.key] = it.value }
        entries.forEach { data[it.first] = it.second }
    }

    @SuppressWarnings("unchecked")
    fun <T> lookup(key: Key<T>, defaultValue: T? = null): T? {
        return try {
            data[key]?.let { it as T } ?: defaultValue
        } catch (e: ClassCastException) {
            defaultValue
        }
    }

    fun <T> withValue(key: Key<T>, value: T): Context {
        if (key in data.keys) {
            throw RuntimeException("$key already exist")
        }
        return Context(this, key to (value as Any))
    }

    fun attach(ctx: Context): Context {
        return Context(this, *ctx.data.toList().toTypedArray())
    }

    fun <R> run(closure: () -> R): R {
        val old = Context.current()
        try {
            Context.set(this)
            return closure()
        } finally {
            Context.set(old)
        }

    }

    override fun toString(): String {
        return data.entries.joinToString(",") { "${it.key.name} => ${it.value}" }
    }

    companion object {
        private val storage = ThreadLocal<Context>()

        fun current(): Context {
            return storage.get() ?: Context()
        }

        fun attach(ctx: Context): Context {
            storage.set(current().attach(ctx))
            return current()
        }

        private fun set(ctx: Context) {
            storage.set(ctx)
        }
    }
}