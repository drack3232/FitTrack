
package com.fittrack_app.fittrack.modules.nutrition.dto;

import java.time.LocalDate;

public interface FoodDailyAggregate {

    LocalDate getEntryDate();

    Double getCaloriesConsumed();

    Double getProteinGrams();

    Double getFatGrams();

    Double getCarbsGrams();
}
