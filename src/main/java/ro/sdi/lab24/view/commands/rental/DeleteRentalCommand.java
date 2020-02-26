package ro.sdi.lab24.view.commands.rental;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import ro.sdi.lab24.view.Console;

@Command(description = "Delete rental", name = "delete")
public class DeleteRentalCommand implements Runnable
{
    @CommandLine.Parameters(index = "0", description = "Movie id")
    int movieId;

    @CommandLine.Parameters(index = "1", description = "Client id")
    int clientId;

    @Override
    public void run()
    {
        Console.rentalController.deleteRental(movieId, clientId);
    }
}
