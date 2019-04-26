package org.neo4j.spatial.algo;

import org.neo4j.spatial.core.Point;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

public class DistanceTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldNotWorkWithInvalidPoints() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot calculate distance between points of different dimension: 3 != 2");
        Distance.distance(Point.point(1, 2, 3), Point.point(3, 4));
    }

    @Test
    public void shouldWorkWithValid2DPoints() {
        assertThat(Distance.distance(Point.point(0, 0), Point.point(0, 0)), equalTo(0.0));
        assertThat(Distance.distance(Point.point(0, 0), Point.point(1, 0)), equalTo(1.0));
        assertThat(Distance.distance(Point.point(0, 0), Point.point(0, 1)), equalTo(1.0));
        assertThat(Distance.distance(Point.point(1, 0), Point.point(0, 0)), equalTo(1.0));
        assertThat(Distance.distance(Point.point(0, 1), Point.point(0, 0)), equalTo(1.0));
        assertThat(Distance.distance(Point.point(0, 0), Point.point(1, 1)), closeTo(1.414, 0.001));
        assertThat(Distance.distance(Point.point(-1, -1), Point.point(1, 1)), closeTo(2.828, 0.001));
    }
}
