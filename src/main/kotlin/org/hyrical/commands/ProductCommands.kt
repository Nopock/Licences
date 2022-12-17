package org.hyrical.commands

import org.hyrical.commandHandler
import org.hyrical.store.repository.Repository
import org.hyrical.types.Product
import revxrsal.commands.annotation.Command
import revxrsal.commands.cli.core.CommandLineActor
import revxrsal.commands.process.ValueResolver

class ProductCommands(val productController: Repository<Product>) : ValueResolver<Product> {

    init {
        commandHandler.registerValueResolver(Product::class.java, this)
    }
    /**
     * Resolves the value of this resolver
     *
     * @param context The command resolving context.
     * @return The resolved value. May or may not be null.
     * @throws Throwable Any exceptions that should be handled by [CommandExceptionHandler]
     */
    override fun resolve(context: ValueResolver.ValueResolverContext): Product {
        return productController.search(context.pop()) ?: throw IllegalArgumentException("Invalid product")
    }

    @Command("product create")
    fun create(actor: CommandLineActor, name: String) {
        productController.save(
            Product(
                identifier = name.lowercase(),
                name = name
            )
        )

        actor.reply("Created product with name $name")
    }

    @Command("product delete")
    fun delete(actor: CommandLineActor, product: Product) {
        productController.delete(product.identifier)

        actor.reply("Deleted product with name ${product.name}")
    }

    @Command("product list")
    fun list(actor: CommandLineActor) {
        actor.reply("Products:")
        productController.findAll().forEach {
            actor.reply(" - ${it.name}")
        }
    }
}