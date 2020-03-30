package ro.sdi.lab24.view.commands.client;

import picocli.CommandLine;
import ro.sdi.lab24.view.Console;
import ro.sdi.lab24.view.FutureResponse;
import ro.sdi.lab24.view.ResponseMapper;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@CommandLine.Command(description = "Filter clients by name", name = "filter")
public class FilterClientsCommand implements Runnable
{
    @CommandLine.Parameters(index = "0", description = "Client name")
    String name;

    @Override
    public void run() {
        Console.responseBuffer.add(
                new FutureResponse<>(
                        Console.clientController.filterClientsByName(name),
                        new ResponseMapper<>(response -> {
                            if (!response.iterator().hasNext()) {
                                return "No clients found!";
                            }
                            return StreamSupport.stream(response.spliterator(), false)
                                    .map(client -> String.format("%d %s", client.getId(), client.getName()))
                                    .collect(Collectors.joining("\n", "", "\n"));
                        })
                )
        );
    }
}
