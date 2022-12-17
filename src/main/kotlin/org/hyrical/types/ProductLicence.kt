package org.hyrical.types

import org.hyrical.store.Storable

data class ProductLicence(
    override val identifier: String,
    val products: MutableList<String> = mutableListOf(),
    val allIps: MutableList<String> = mutableListOf(),
) : Storable