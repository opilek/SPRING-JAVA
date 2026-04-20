package com.umcsuser.carrent.models;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rental {
    private String id;
    private String vehicleId;
    private String userId;
    private LocalDateTime startDateTime;
    private LocalDateTime returnDateTime;

    public boolean isActive() {
        return returnDateTime == null;
    }

    public Rental copy() {
        return new Rental(id, vehicleId, userId, startDateTime, returnDateTime);
    }
}