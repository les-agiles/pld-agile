graphhopper-core
====================================

Permet le calcul du plus court chemin entre N points

### Source dépendance

<https://mvnrepository.com/artifact/com.graphhopper/graphhopper-core/8.0>

### Utilisation

Plusieurs manière d'utiliser la librairie : 

-   Démarrage d'un serveur GraphHopper qui peut être requêté via des APIs

-   Purement local, en utilisant une tuile au format .pbf (ProtocolBuffer)

### Affecter une tuile

Une tuile correspond à un fichier contenant des informations géographiques, qui peut représenter le monde comme une petite partie de celui-ci. Elle sert de base à GraphHopper pour calculer les plus courts chemins.

On peut télécharger des tuiles via les liens suivant

-   <https://data.maptiler.com/downloads/europe/france/lyon/>

-   <http://download.geofabrik.de/europe/france/>

### Création d'une requête pour calcul du plus court chemin entre 2 points

-   Plus court chemin entre deux points : ```GHRequest(double fromLat, double fromLon, double toLat, double toLon)```
- Plus court chemin entre N points : ```GHRequest(List<GHPoint> points)```

### Récupération du résulat

- ResponsePath
```    
    private final List<Throwable> errors = new ArrayList<>(4);
    
    private List<String> description;
    
    private double distance;
    
    private double ascend;
    
    private double descend;
    
    private double routeWeight;
    
    private long time;
    
    private String debugInfo = "";
    
    private InstructionList instructions;
    
    private PointList waypointList = PointList.EMPTY;
    
    private List<Integer> waypointIndices = new ArrayList<>();
    
    private PointList pointList = PointList.EMPTY;
    
    private int numChanges;
    
    private final List<Trip.Leg> legs = new ArrayList<>(5);
    
    private final List<Integer> pointsOrder = new ArrayList<>(5);
    
    private final Map<String, List<PathDetail>> pathDetails = new HashMap<>();
    
    private BigDecimal fare;
    
    private boolean impossible = false;
```