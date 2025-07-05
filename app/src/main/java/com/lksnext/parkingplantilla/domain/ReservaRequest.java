package com.lksnext.parkingplantilla.domain;

import java.util.List;

public record ReservaRequest(String fecha, List<String> horas, String tipoPlaza) {
}
