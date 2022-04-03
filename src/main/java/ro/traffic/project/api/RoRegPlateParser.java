package ro.traffic.project.api;

public interface RoRegPlateParser {
    RoRegPlateProperties parseRegistrationPlate(String registrationPlate) throws InvalidRoRegPlateException;
}
