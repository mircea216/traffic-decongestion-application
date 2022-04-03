package ro.traffic.project.api;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface VehicleOwnerProcessResult extends Serializable {
    Integer getOddToEvenRatio();

    Map<String, Integer> getUnregCarsCountByJud();

    Integer getPassedRegChangeDueDate();

    Set<VehicleOwnerParseError> getErrors();
}
