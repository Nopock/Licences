package org.hyrical.plugins

import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.resources.*
import kotlinx.serialization.Serializable
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import org.hyrical.store.DataStoreController
import org.hyrical.store.repository.Repository
import org.hyrical.store.type.StorageType
import org.hyrical.types.Product
import org.hyrical.types.ProductLicence
import java.util.logging.Logger

fun Application.configureRouting(productController: Repository<Product>, licenceController: Repository<ProductLicence>) {

    install(Resources)

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    routing {
        get("/") {
            call.respondText("Invalid request", status = HttpStatusCode.BadRequest)
        }

        get("/licences/verify") {
            Logger.getLogger("org.hyrical.plugins").info("Received request to verify licence")
            val product = call.request.queryParameters["product"] ?: return@get call.respondText("Invalid request", status = HttpStatusCode.BadRequest)

            val licence = call.request.queryParameters["licence"] ?: return@get call.respondText("Invalid request", status = HttpStatusCode.BadRequest)

            val productObject = productController.search(product) ?: return@get call.respondText("Invalid request", status = HttpStatusCode.BadRequest)
            val licenceObject = licenceController.search(licence) ?: return@get call.respondText("Invalid request", status = HttpStatusCode.BadRequest)

            if (licenceObject.products.contains(productObject.identifier)) {
                call.respondText("Valid", status = HttpStatusCode.OK)

                if (!licenceObject.allIps.contains(call.request.origin.remoteHost)) {
                    licenceObject.allIps.add(call.request.origin.remoteHost)
                    licenceController.save(licenceObject)
                }
            } else {
                call.respondText("Invalid", status = HttpStatusCode.BadRequest)
            }
        }
    }
}

@Serializable
@Resource("/articles")
class Articles(val sort: String? = "new")
