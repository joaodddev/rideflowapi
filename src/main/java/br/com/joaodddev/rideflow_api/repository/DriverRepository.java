package br.com.joaodddev.rideflow_api.repository;

import br.com.joaodddev.rideflow_api.model.Driver;
import br.com.joaodddev.rideflow_api.model.DriverStatus;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends MongoRepository<Driver, String> {

    Optional<Driver> findByUserId(String userId);

    List<Driver> findByStatus(DriverStatus status);

    /**
     * Finds available drivers near a given point within a max distance.
     * Uses MongoDB $nearSphere with 2dsphere index on the "location" field.
     * Spring Data translates findByLocationNearAndStatus into a $nearSphere query.
     */
    List<Driver> findByLocationNearAndStatus(Point point, Distance distance, DriverStatus status);

    /**
     * Raw GeoJSON $nearSphere query — finds the N nearest available drivers.
     * coordinates: [longitude, latitude] (MongoDB GeoJSON order).
     */
    @Query("{ 'location': { $nearSphere: { $geometry: { type: 'Point', coordinates: [?0, ?1] }, $maxDistance: ?2 } }, 'status': 'AVAILABLE' }")
    List<Driver> findNearbyAvailableDrivers(double longitude, double latitude, double maxDistanceMeters);
}
