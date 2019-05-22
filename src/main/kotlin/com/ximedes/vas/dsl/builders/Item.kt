package com.ximedes.vas.dsl.builders

import com.ximedes.vas.dsl.DynamoDbDSL
import com.ximedes.vas.dsl.MutableItem
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.io.InputStream
import java.nio.ByteBuffer

@DynamoDbDSL
class ItemBuilder {

    private val item: MutableItem = mutableMapOf()

    object Null

    infix fun String.from(block: ItemBuilder.() -> Unit) {
        item[this] = AttributeValue.builder().m(ItemBuilder().apply(block).build()).build()
    }

    infix fun String.from(value: String) {
        item[this] = AttributeValue.builder().s(value).build()
    }

    @JvmName("fromStrings")
    infix fun String.from(value: Collection<String>) {
        item[this] = AttributeValue.builder().ss(value).build()
    }

    infix fun String.from(value: Number) {
        item[this] = AttributeValue.builder().n(value.toString()).build()
    }

    infix fun String.from(value: Boolean) {
        item[this] = AttributeValue.builder().bool(value).build()
    }

    @JvmName("fromNumbers")
    infix fun String.from(value: Collection<Number>) {
        item[this] = AttributeValue.builder().ns(value.map { it.toString() }).build()
    }

    infix fun String.from(value: ByteArray) {
        item[this] = AttributeValue.builder().b(SdkBytes.fromByteArray(value)).build()
    }

    @JvmName("fromByteArrays")
    infix fun String.from(value: Collection<ByteArray>) {
        item[this] = AttributeValue.builder().bs(value.map { SdkBytes.fromByteArray(it) }).build()
    }

    infix fun String.from(value: ByteBuffer) {
        item[this] = AttributeValue.builder().b(SdkBytes.fromByteBuffer(value)).build()
    }

    @JvmName("fromByteBuffers")
    infix fun String.from(value: Collection<ByteBuffer>) {
        item[this] = AttributeValue.builder().bs(value.map { SdkBytes.fromByteBuffer(it) }).build()
    }

    infix fun String.from(value: InputStream) {
        item[this] = AttributeValue.builder().b(SdkBytes.fromInputStream(value)).build()
    }

    @JvmName("fromInputStreams")
    infix fun String.from(value: Collection<InputStream>) {
        item[this] = AttributeValue.builder().bs(value.map { SdkBytes.fromInputStream(it) }).build()
    }

    infix fun String.from(value: Null) {
        item[this] = AttributeValue.builder().nul(true).build()
    }

    fun build(): Map<String, AttributeValue> = item

}