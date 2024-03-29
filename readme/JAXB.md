# JAXB

JAXB (Java Architecture for XML Binding) est un framework qui permet la conversion bidirectionnelle entre documents XML
et objets Java.

## Fonctionnalités principales

- **Marshalisation** : Conversion d'objets Java en documents XML.
- **Démarshalisation** : Conversion de documents XML en objets Java.
- **Génération de schémas** : Création de schémas XML à partir d'objets Java.

## Comment utiliser JAXB

1. **Annoter les classes Java** : Utiliser des annotations JAXB pour définir comment les classes Java sont converties en
   XML.
2. **Créer un contexte JAXB** : Initialiser JAXB avec les classes annotées.
3. **Marshalisation/Démarshalisation** : Utiliser le contexte pour convertir entre objets Java et XML.

Pour des exemples de code et des explications plus détaillées, consultez
la [documentation officielle](https://docs.oracle.com/javase/tutorial/jaxb/intro/index.html).

## Utilisation dans GeoFast

Dans **GeoFast**, JAXB est utilisé pour la lecture des fichiers XML du plan et des programmes de livraisons.

Certaines classes demandent un parsing en 2 temps. Par exemple sur les classes de programme de
livraison **"PlanningRequest"** ou le plan **"Map"**. Nous ajoutons donc une fonction "setup", une étape réalisée après
le parsing par la classe Factory qui orchestre la création et la configuration des objets à partir des données XML

La qui permet de réaliser l'étape Démarshalisation (Passage de XML à Java) est la classe `XMLParser`.

## Exemple d'utilisation

Voici une petite explication des annotations JAXB utilisées dans la classe PlanningRequest :

```java

@Getter
@XmlRootElement(name = "planningRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlanningRequest {
    @XmlElement(name = "request")
    private List<Request> requests = new ArrayList<>();

    private java.util.Map<String, DeliveryGuy> couriersMap;

    private Warehouse warehouse;

    // ...
}
```

- `@XmlRootElement(name = "planningRequest")` : Cette annotation indique que cette classe peut être le nœud racine d'un
  document XML. L'attribut name spécifie le nom du nœud XML.

- `@XmlAccessorType(XmlAccessType.FIELD)` : Cette annotation définit comment JAXB doit accéder aux données de la classe
  lors de la marshalisation et de la démarshalisation. XmlAccessType.FIELD signifie que JAXB utilisera directement les
  champs de la classe, indépendamment de leur visibilité.
- `@XmlElement(name = "request")` : Cette annotation est utilisée sur un champ pour indiquer que ce champ doit être
  mappé à un élément XML. L'attribut name spécifie le nom de l'élément XML.

Dans cette classe, les champs `couriersMap` et `warehouse` ne sont pas annotés, ce qui signifie qu'ils seront mappés à
des éléments XML avec le même nom que le nom du champ. Cependant, ils ne seront pas inclus dans le XML si leur valeur
est null.

La méthode setup n'est pas annotée et ne sera donc pas incluse dans le processus de marshalisation/démarshalisation.
Cette méthode est utilisée pour configurer l'objet après la démarshalisation.