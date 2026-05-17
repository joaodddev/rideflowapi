package br.com.joaodddev.rideflow_api.repository;

import br.com.joaodddev.rideflow_api.model.Ride;
import br.com.joaodddev.rideflow_api.model.RideStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends MongoRepository<Ride, String> {

    Page<Ride> findByPassengerId(String passengerId, Pageable pageable);

    Page<Ride> findByDriverId(String driverId, Pageable pageable);

    Optional<Ride> findByDriverIdAndStatus(String driverId, RideStatus status);

    Optional<Ride> findByPassengerIdAndStatus(String passengerId, RideStatus status);

    List<Ride> findByPassengerIdAndStatusIn(String passengerId, List<RideStatus> statuses);

    List<Ride> findByDriverIdAndStatusIn(String driverId, List<RideStatus> statuses);
}