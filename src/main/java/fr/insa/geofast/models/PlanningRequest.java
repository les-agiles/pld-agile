package fr.insa.geofast.models;

import lombok.Getter;

import java.util.List;

@Getter
public class PlanningRequest {
    List<Request> requests;
}
