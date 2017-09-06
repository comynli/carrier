package io.carrier.rpc.client

import io.carrier.rpc.Registry
import io.carrier.rpc.Request
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Client @Inject constructor(private val registry: Registry) {
    @SuppressWarnings("unchecked")
    fun <T> create(clazz: Class<T>): T {
        val enhancer = Enhancer()
        enhancer.setSuperclass(clazz)
        enhancer.setCallback(MethodInterceptor { _: Any, method: Method, args: Array<Any>, _: MethodProxy ->
            val request = Request(UUID.randomUUID().toString(), method.declaringClass.name, method.name, method.parameterTypes, args)
            val response = registry.pick(clazz.name).call(request).get()
            if (response.id != request.id) {
                throw Exception("TODO") // TODO
            }
            response.exception?.let { throw it }
            response.result
        })
        return enhancer.create() as T
    }
}