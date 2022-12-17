package org.hyrical.commands

import org.hyrical.commandHandler
import org.hyrical.store.repository.Repository
import org.hyrical.types.Product
import org.hyrical.types.ProductLicence
import revxrsal.commands.annotation.Command
import revxrsal.commands.cli.core.CommandLineActor
import revxrsal.commands.process.ValueResolver
import java.util.UUID

class LicenceCommands(val licenceController: Repository<ProductLicence>) : ValueResolver<ProductLicence> {

    init {
        commandHandler.registerValueResolver(ProductLicence::class.java, this)
    }

    /**
     * Resolves the value of this resolver
     *
     * @param context The command resolving context.
     * @return The resolved value. May or may not be null.
     * @throws Throwable Any exceptions that should be handled by [CommandExceptionHandler]
     */
    override fun resolve(context: ValueResolver.ValueResolverContext): ProductLicence {
        return licenceController.search(context.pop()) ?: throw IllegalArgumentException("Invalid licence")
    }

    @Command("licence create")
    fun create(actor: CommandLineActor) {
        licenceController.save(
            ProductLicence(
                identifier = UUID.randomUUID().toString()
            )
        ).also {
            actor.reply("Created licence with id ${it.identifier}")
        }
    }

    @Command("licence delete")
    fun delete(actor: CommandLineActor, licence: ProductLicence) {
        licenceController.delete(licence.identifier)

        actor.reply("Deleted licence with id ${licence.identifier}")
    }

    @Command("licence list")
    fun list(actor: CommandLineActor) {
        actor.reply("Licences:")
        licenceController.findAll().forEach {
            actor.reply(" - ${it.identifier}")
        }
    }

    @Command("product licenses")
    fun licenses(actor: CommandLineActor, product: Product) {
        actor.reply("Licenses for ${product.name}:")
        licenceController.findAll().forEach {
            if (it.products.contains(product.identifier)) {
                actor.reply(" - ${it.identifier}")
            }
        }
    }

    // TODO: Command to add product to licence
    @Command("licence add")
    fun add(actor: CommandLineActor, licence: ProductLicence, product: Product) {
        licence.products.add(product.identifier)
        licenceController.save(licence)

        actor.reply("Added ${product.name} to ${licence.identifier}")
    }
}