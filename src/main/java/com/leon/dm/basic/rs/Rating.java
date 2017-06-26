package com.leon.dm.basic.rs;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by ntcon on 6/20/2017.
 */
@Data
@NoArgsConstructor
class Rating {

    private String band;

    private double star;

    Rating(String band, double star) {
        this.band = band;
        this.star = star;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Rating rating = (Rating) o;

        return new EqualsBuilder()
                .append(band, rating.band)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(band)
                .toHashCode();
    }

}
