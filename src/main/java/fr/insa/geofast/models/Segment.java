package fr.insa.geofast.models;

import lombok.Getter;

@Getter
public class Segment {
    private String name;
    private double length;

    private Intersection destination;
    private Intersection origin;
}
