package ro.traffic.project.api;

public interface RoIdCardSeriesJudMapper {
    Judet mapIdCardToJud(String idCardSeries) throws InvalidRoIdCardException;
}
