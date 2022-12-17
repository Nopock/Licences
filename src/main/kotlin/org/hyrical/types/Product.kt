package org.hyrical.types

import org.hyrical.store.Storable

data class Product(
    override val identifier: String,
    val name: String
) : Storable