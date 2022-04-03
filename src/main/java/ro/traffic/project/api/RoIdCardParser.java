package ro.traffic.project.api;

public interface RoIdCardParser {
    RoIdCardProperties parseIdCard(String idCard) throws InvalidRoIdCardException;
}
