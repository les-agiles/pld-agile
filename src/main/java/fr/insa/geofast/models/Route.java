package fr.insa.geofast.models;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Route {
    private final List<Request> requests = new ArrayList<>();
    private final List<Segment> paths = new ArrayList<>();
}
