package ro.sdi.lab24.view.commands.client;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import ro.sdi.lab24.validation.ProgramException;
import ro.sdi.lab24.view.Console;

@Command(description = "Delete a client", name = "delete")
public class DeleteClientCommand implements Runnable
{
    @Parameters(index = "0", description = "Client id")
    int id;


    @Override
    public void run()
    {
        try
        {
            Console.clientController.deleteClient(id);
        }
        catch (ProgramException e)
        {
            Console.handleException(e);
        }
    }
}
