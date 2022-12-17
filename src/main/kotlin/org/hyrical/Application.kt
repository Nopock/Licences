package org.hyrical

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.hyrical.commands.LicenceCommands
import org.hyrical.commands.ProductCommands
import org.hyrical.plugins.*
import org.hyrical.store.DataStoreController
import org.hyrical.store.constants.DataTypeResources
import org.hyrical.store.repository.Repository
import org.hyrical.store.type.StorageType
import org.hyrical.types.Product
import org.hyrical.types.ProductLicence
import revxrsal.commands.cli.ConsoleCommandHandler

val commandHandler = ConsoleCommandHandler.create()

fun main() {
    DataTypeResources.enableMongoRepositories("localhost", 27017, "hLicence")

    productController = DataStoreController.of<Product>(type = StorageType.MONGO)
        .also {
            it.construct()
        }.repository

    licenceController = DataStoreController.of<ProductLicence>(type = StorageType.MONGO)
        .also {
            it.construct()
        }.repository

    commandHandler.register(ProductCommands(
        productController
    ),
        LicenceCommands(
            licenceController
        )
    )

    //commandHandler.pollInput()
    Thread {
        commandHandler.pollInput()
    }.start()

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureRouting(productController, licenceController)
}

lateinit var productController: Repository<Product>

lateinit var licenceController: Repository<ProductLicence>