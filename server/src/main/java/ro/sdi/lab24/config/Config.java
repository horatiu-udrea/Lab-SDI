package ro.sdi.lab24.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiServiceExporter;
import ro.sdi.lab24.controller.*;
import ro.sdi.lab24.model.Client;
import ro.sdi.lab24.model.Movie;
import ro.sdi.lab24.model.Rental;
import ro.sdi.lab24.model.serialization.database.ClientTableAdapter;
import ro.sdi.lab24.model.serialization.database.MovieTableAdapter;
import ro.sdi.lab24.model.serialization.database.RentalTableAdapter;
import ro.sdi.lab24.networking.ServerInformation;
import ro.sdi.lab24.repository.DatabaseRepository;
import ro.sdi.lab24.repository.Repository;
import ro.sdi.lab24.validation.ClientValidator;
import ro.sdi.lab24.validation.MovieValidator;
import ro.sdi.lab24.validation.RentalValidator;

@Configuration
public class Config
{
    @Bean
    ClientTableAdapter clientTableAdapter()
    {
        return new ClientTableAdapter();
    }

    @Bean
    MovieTableAdapter movieTableAdapter()
    {
        return new MovieTableAdapter();
    }

    @Bean
    RentalTableAdapter rentalTableAdapter()
    {
        return new RentalTableAdapter();
    }

    @Bean
    Repository<Integer, Client> clientRepository(ClientTableAdapter clientTableAdapter)
    {
        return new DatabaseRepository<>(clientTableAdapter);
    }

    @Bean
    Repository<Integer, Movie> movieRepository(MovieTableAdapter movieTableAdapter)
    {
        return new DatabaseRepository<>(movieTableAdapter);
    }

    @Bean
    Repository<Rental.RentalID, Rental> rentalRepository(RentalTableAdapter rentalTableAdapter)
    {
        return new DatabaseRepository<>(rentalTableAdapter);
    }

    @Bean
    ControllerImpl controller(
            Repository<Integer, Client> clientRepository,
            Repository<Integer, Movie> movieRepository,
            Repository<Rental.RentalID, Rental> rentalRepository
    )
    {
        return new ControllerImpl(
                clientRepository,
                movieRepository,
                rentalRepository,
                new ClientValidator(),
                new MovieValidator(),
                new RentalValidator()
        );
    }


    @Bean
    ClientControllerImpl clientController(Repository<Integer, Client> clientRepository)
    {
        return new ClientControllerImpl(clientRepository, new ClientValidator());
    }

    @Bean
    MovieControllerImpl movieController(Repository<Integer, Movie> movieRepository)
    {
        return new MovieControllerImpl(movieRepository, new MovieValidator());
    }

    @Bean
    RentalController rentalController(
            ClientControllerImpl clientController,
            MovieControllerImpl movieController,
            Repository<Rental.RentalID, Rental> rentalRepository
    )
    {
        return new RentalControllerImpl(
                clientController,
                movieController,
                rentalRepository,
                new RentalValidator()
        );
    }

    @Bean
    RmiServiceExporter controllerExporter(ControllerImpl controller)
    {
        RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
        rmiServiceExporter.setServiceName("Controller");
        rmiServiceExporter.setServiceInterface(Controller.class);
        rmiServiceExporter.setService(controller);
        rmiServiceExporter.setRegistryPort(ServerInformation.PORT);
        return rmiServiceExporter;
    }

    @Bean
    RmiServiceExporter clientExporter(ClientControllerImpl clientController)
    {
        RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
        rmiServiceExporter.setServiceName("ClientController");
        rmiServiceExporter.setServiceInterface(ClientController.class);
        rmiServiceExporter.setService(clientController);
        rmiServiceExporter.setRegistryPort(ServerInformation.PORT);
        return rmiServiceExporter;
    }

    @Bean
    RmiServiceExporter movieExporter(MovieControllerImpl movieController)
    {
        RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
        rmiServiceExporter.setServiceName("MovieController");
        rmiServiceExporter.setServiceInterface(MovieController.class);
        rmiServiceExporter.setService(movieController);
        rmiServiceExporter.setRegistryPort(ServerInformation.PORT);
        return rmiServiceExporter;
    }

    @Bean
    RmiServiceExporter rentalExporter(RentalController rentalController)
    {
        RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
        rmiServiceExporter.setServiceName("RentalController");
        rmiServiceExporter.setServiceInterface(RentalController.class);
        rmiServiceExporter.setService(rentalController);
        rmiServiceExporter.setRegistryPort(ServerInformation.PORT);
        return rmiServiceExporter;
    }
}