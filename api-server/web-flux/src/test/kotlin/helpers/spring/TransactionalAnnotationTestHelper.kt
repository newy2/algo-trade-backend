package helpers.spring

import org.springframework.transaction.annotation.Transactional
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions

open class TransactionalAnnotationTestHelper(private val clazz: KClass<*>) {
    fun hasReadOnlyTransactional(methodName: String): Boolean {
        return getTransactionalAnnotation(methodName)!!.readOnly == true
    }

    fun hasWritableTransactional(methodName: String): Boolean {
        return getTransactionalAnnotation(methodName)!!.readOnly == false
    }

    fun hasNotTransactional(methodName: String): Boolean {
        return getTransactionalAnnotation(methodName) == null
    }

    private fun getMethod(methodName: String) = clazz.functions.find { it.name == methodName }!!
    private fun getTransactionalAnnotation(methodName: String) = getMethod(methodName).findAnnotation<Transactional>()
}