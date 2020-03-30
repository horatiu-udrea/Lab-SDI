package ro.sdi.lab24.view.commands.report;

import picocli.CommandLine.Command;
import ro.sdi.lab24.view.Console;
import ro.sdi.lab24.view.FutureResponse;
import ro.sdi.lab24.view.ResponseMapper;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Command(description = "Top 10 rented movies", name = "topmovies")
public class ReportTopMoviesCommand implements Runnable {
    @Override
    public void run() {
        Console.responseBuffer.add(
                new FutureResponse<>(
                        Console.controller.getTop10RentedMovies(),
                        new ResponseMapper<>(response -> {
                            if (!response.iterator().hasNext()) {
                                return "No rentals for top movies data found!";
                            }
                            return StreamSupport.stream(response.spliterator(), false)
                                    .map(rentedMovieStatistic -> String.format("%s - rented %d times", rentedMovieStatistic.getMovieName(), rentedMovieStatistic.getNumberOfRentals()))
                                    .collect(Collectors.joining("\n", "", "\n"));
                        })
                )
        );
    }
}