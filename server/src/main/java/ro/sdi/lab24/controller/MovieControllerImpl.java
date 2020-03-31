package ro.sdi.lab24.controller;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import ro.sdi.lab24.exception.AlreadyExistingElementException;
import ro.sdi.lab24.exception.ElementNotFoundException;
import ro.sdi.lab24.exception.SortingException;
import ro.sdi.lab24.model.Movie;
import ro.sdi.lab24.model.Sort;
import ro.sdi.lab24.repository.Repository;
import ro.sdi.lab24.repository.SortingRepository;
import ro.sdi.lab24.validation.Validator;

public class MovieControllerImpl implements MovieController
{
    Repository<Integer, Movie> movieRepository;
    Validator<Movie> movieValidator;
    EntityDeletedListener<Movie> entityDeletedListener = null;

    public MovieControllerImpl(
            Repository<Integer, Movie> movieRepository,
            Validator<Movie> movieValidator
    )
    {
        this.movieRepository = movieRepository;
        this.movieValidator = movieValidator;
    }

    public void setEntityDeletedListener(EntityDeletedListener<Movie> entityDeletedListener)
    {
        this.entityDeletedListener = entityDeletedListener;
    }

    /**
     * This function adds a movie to the repository
     *
     * @param id:   the ID of the movie
     * @param name: the name of the movie
     * @throws AlreadyExistingElementException if the movie (the ID) is already there
     */
    @Override
    public void addMovie(int id, String name, String genre, int rating)
    {
        Movie movie = new Movie(id, name, genre, rating);
        movieValidator.validate(movie);
        movieRepository.save(movie).ifPresent(opt ->
                                              {
                                                  throw new AlreadyExistingElementException(String.format(
                                                          "Movie %d already exists",
                                                          id
                                                  ));
                                              });
    }

    /**
     * This function removes a movie from the repository based on their ID
     *
     * @param id: the ID of the movie
     * @throws ElementNotFoundException if the movie isn't found in the repository based on their ID
     */
    @Override
    public void deleteMovie(int id)
    {
        movieRepository
                .delete(id)
                .ifPresentOrElse(
                        entity -> Optional
                                .ofNullable(entityDeletedListener)
                                .ifPresent(listener -> listener.onEntityDeleted(entity)),
                        () ->
                        {
                            throw new ElementNotFoundException(String.format(
                                    "Movie %d does not exist",
                                    id
                            ));
                        }
                );
    }

    /**
     * This function returns an iterable collection of the current state of the movies in the repository
     *
     * @return all: an iterable collection of movies
     */
    @Override
    public Iterable<Movie> getMovies()
    {
        return movieRepository.findAll();
    }

    /**
     * This function updated a movie based on their ID with a new name
     *
     * @param id     : the movie's ID
     * @param name   : the new name of the movie
     * @param genre  : the genre of the movie
     * @param rating : the rating of the movie
     * @throws ElementNotFoundException if the movie isn't found in the repository based on their ID
     */
    @Override
    public void updateMovie(
            int id,
            String name,
            String genre,
            Integer rating
    )
    {
        Movie storedMovie = movieRepository.findOne(id)
                                           .orElseThrow(() -> new ElementNotFoundException(String.format(
                                                   "Movie %d does not exist",
                                                   id
                                           )));
        Movie movie = new Movie(id, Optional.ofNullable(name).orElseGet(storedMovie::getName),
                                Optional.ofNullable(genre).orElseGet(storedMovie::getGenre),
                                Optional.ofNullable(rating).orElseGet(storedMovie::getRating)
        );
        movieValidator.validate(movie);
        movieRepository.update(movie)
                       .orElseThrow(() -> new ElementNotFoundException(String.format(
                               "Movie %d does not exist",
                               id
                       )));
    }

    @Override
    public Iterable<Movie> filterMoviesByGenre(String genre)
    {
        String regex = ".*" + genre + ".*";
        return StreamSupport.stream(movieRepository.findAll().spliterator(), false)
                            .filter(movie -> movie.getGenre().matches(regex))
                            .collect(Collectors.toUnmodifiableList());
    }

    public Optional<Movie> findOne(int movieId)
    {
        return movieRepository.findOne(movieId);
    }

    @Override
    public Iterable<Movie> sortMovies(Sort criteria)
    {
        SortingRepository<Integer, Movie> sortingRepo = Optional.of(movieRepository)
                                                                .filter(repo -> repo instanceof SortingRepository)
                                                                .map(repo -> (SortingRepository<Integer, Movie>) repo)
                                                                .orElseThrow(() -> new SortingException(
                                                                        "repository doesn't support sorting"));
        return sortingRepo.findAll(criteria);
    }
}