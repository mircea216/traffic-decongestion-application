package ro.traffic.project.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface VehicleOwnersProcessor {
    void process(InputStream ciCarRegNbInputStream, OutputStream processResultOutputStream) throws IOException;
}
